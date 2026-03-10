# A Case Study: Designing a Document Editor (Lexi)

[Introduction to Design Patterns](../ch1/README.md)  
[Pattern catalog](../../README.md)

---

## Introduction

Lexi is a "What-You-See-Is-What-You-Get" (WYSIWYG) document editor. It freely mixes text and graphics in a variety of formatting styles. Throughout its design, we encounter seven specific problems and solve them using eight design patterns. By walking through these problems, we bridge the gap between abstract pattern theory and concrete application logic.

---

## Problem 1: Document Structure

**Motivation:** A document is ultimately just an arrangement of basic graphical elements (characters, lines, shapes), but authors view them as physical structures (lines, columns, tables). Our internal representation needs to match this physical structure. Crucially, we must treat text and graphics uniformly to avoid redundant formatting mechanisms. We also shouldn't distinguish between single elements (like a character) and groups of elements (like an intricate diagram).  

**Expected Outcome:** A design that supports recursive composition, allowing us to build increasingly complex elements out of simpler ones.

**The Solution: Composite Pattern.**  We define a common `Glyph` interface for all visible and structural elements. A parent glyph can treat any child uniformly, whether it's a primitive character or a complex row containing hundreds of elements.

**Modern Java Implementation (JDK 25):** We utilize `sealed` interfaces to explicitly model the closed domain of structural elements, and `record` for immutable boundary definitions.

```java
// Mock Object implementations for Chapter 2
record Window() { void drawRect(int x, int y, int width, int height) {} }
record Bounds(int x, int y, int width, int height) {}
record Point(int x, int y) {}

/**
 * Component: The common interface for all document elements.
 * By sealing the interface, we strictly control the taxonomy of structural elements.
 */
sealed interface Glyph permits CharacterGlyph, Row, Column, Composition, MonoGlyph {
    /** Renders the glyph onto the provided window. */
    void draw(Window w);
    /** Returns the rectangular area that the glyph occupies. */
    Bounds bounds();
    /** Determines if a specific point intersects the glyph for hit detection. */
    boolean intersects(Point p);

    // Child management operations
    void insert(Glyph glyph, int index);
    void remove(Glyph glyph);
    Glyph child(int index);
    Glyph parent();
}

/** 
 * Leaf: Primitive elements that do not contain children.
 */
final class CharacterGlyph implements Glyph {
    private final char c;
    public CharacterGlyph(char c) { this.c = c; }

    @Override public void draw(Window w) { /* Renders the character */ }
    @Override public Bounds bounds() { return new Bounds(0,0,10,10); }
    @Override public boolean intersects(Point p) { return false; } // Simplified

    // Leaf nodes ignore child management operations
    @Override public void insert(Glyph g, int i) {}
    @Override public void remove(Glyph g) {}
    @Override public Glyph child(int i) { return null; }
    @Override public Glyph parent() { return null; }
}

/** 
 * Composite: Structural elements containing children.
 */
final class Row implements Glyph {
    private final java.util.List<Glyph> children = new java.util.ArrayList<>();

    /** Iterates through all child glyphs and recursively draws them. */
    @Override public void draw(Window w) { children.forEach(c -> c.draw(w)); }
    @Override public Bounds bounds() { return new Bounds(0,0,100,10); }
    @Override public boolean intersects(Point p) { return false; }

    @Override public void insert(Glyph g, int i) { children.add(i, g); }
    @Override public void remove(Glyph g) { children.remove(g); }
    @Override public Glyph child(int i) { return children.get(i); }
    @Override public Glyph parent() { return null; }
}

final class Column implements Glyph { /* Similar to Row implementation */
    @Override public void draw(Window w) {}
    @Override public Bounds bounds() { return null; }
    @Override public boolean intersects(Point p) { return false; }
    @Override public void insert(Glyph g, int i) {}
    @Override public void remove(Glyph g) {}
    @Override public Glyph child(int i) { return null; }
    @Override public Glyph parent() { return null; }
}
```

---

## Problem 2: Formatting

**Motivation:** Lexi needs to break text into lines and columns. Formatting algorithms are complex, and we want to balance formatting speed with quality without hard-coding the algorithm into the document structure. We also want the ability to easily swap out line-breaking algorithms (e.g., at compile-time or run-time).

**Expected Outcome:** Strong separation between the code that supports the document's physical structure and the code that handles formatting.

**The Solution: Strategy Pattern**  
We encapsulate the formatting algorithm inside a `Compositor` object (the Strategy). A `Composition` glyph (the Context) maintains a reference to a `Compositor` and delegates formatting to it.  

```java
/** 
 * Strategy: Encapsulates the formatting/line-breaking algorithm.
 */
@FunctionalInterface
interface Compositor {
    /** Formats a collection of child glyphs into lines and columns. */
    void compose(java.util.List<Glyph> children);
}

/** Concrete Strategy: Prioritizes speed over quality. */
class SimpleCompositor implements Compositor {
    @Override public void compose(java.util.List<Glyph> children) {
        System.out.println("Doing a quick formatting pass...");
    }
}

/** Concrete Strategy: Prioritizes quality (even distribution of whitespace) over speed. */
class TeXCompositor implements Compositor {
    @Override public void compose(java.util.List<Glyph> children) {
        System.out.println("Doing high-quality TeX formatting...");
    }
}

/** 
 * Context: A structural Glyph that delegates its formatting to a Strategy.
 */
final class Composition implements Glyph {
    private Compositor compositor;
    private final java.util.List<Glyph> children = new java.util.ArrayList<>();

    public Composition(Compositor compositor) {
        this.compositor = compositor;
    }

    /** Allows changing the formatting strategy at run-time. */
    public void setCompositor(Compositor compositor) {
        this.compositor = compositor;
    }

    /** Triggers the line-breaking algorithm. */
    public void format() {
        compositor.compose(this.children);
    }

    // Glyph interface implementations...
    @Override public void draw(Window w) {}
    @Override public Bounds bounds() { return null; }
    @Override public boolean intersects(Point p) { return false; }
    @Override public void insert(Glyph g, int i) { children.add(i, g); }
    @Override public void remove(Glyph g) { children.remove(g); }
    @Override public Glyph child(int i) { return children.get(i); }
    @Override public Glyph parent() { return null; }
}
```

---

## Problem 3: Embellishing the User Interface

**Motivation:** We need to add borders and scroll bars to the UI. Using inheritance to do this (e.g., creating a `BorderedScrollableComposition` class) leads to an unworkable explosion of classes.

**Expected Outcome:** A flexible extension mechanism that allows us to add or remove embellishments dynamically at run-time without clients knowing the embellishments are there.

**The Solution: Decorator Pattern**  
We use transparent enclosure. A `MonoGlyph` implements the `Glyph` interface, holds a single child component, and forwards drawing requests to it. Subclasses like `Border` add their own behavior before or after forwarding.

```java
/** 
 * Decorator Base: Implements transparent enclosure for a single component.
 */
abstract non-sealed class MonoGlyph implements Glyph {
    protected final Glyph component;

    public MonoGlyph(Glyph component) {
        this.component = component;
    }

    // Default behavior is strictly transparent forwarding
    @Override public void draw(Window w) { component.draw(w); }
    @Override public Bounds bounds() { return component.bounds(); }
    @Override public boolean intersects(Point p) { return component.intersects(p); }
    @Override public void insert(Glyph g, int i) { component.insert(g, i); }
    @Override public void remove(Glyph g) { component.remove(g); }
    @Override public Glyph child(int i) { return component.child(i); }
    @Override public Glyph parent() { return component.parent(); }
}

/** 
 * Concrete Decorator: Augments the component's drawing behavior.
 */
class Border extends MonoGlyph {
    private final int width;

    public Border(Glyph component, int width) {
        super(component);
        this.width = width;
    }

    /** Extends the parent class operation to add the border embellishment. */
    @Override public void draw(Window w) {
        super.draw(w); // Draw the component first
        drawBorder(w); // Then embellish it
    }

    private void drawBorder(Window w) {
        System.out.println("Drawing border of width " + width);
    }
}
```

---

## Problem 4: Supporting Multiple Look-and-Feel Standards

**Motivation:** Lexi must adapt easily to different UI look-and-feel standards (like Motif and Presentation Manager). Hardcoding explicit constructor calls (e.g., `new MotifScrollBar()`) tightly couples the implementation to a specific standard, making it a nightmare to port or swap styles at run-time.

**Expected Outcome:** An abstraction that allows Lexi to determine the target standard and consistently create the appropriate widget families without directly referencing concrete classes.

**The Solution: Abstract Factory Pattern**
We abstract the process of object creation. A `GUIFactory` defines an interface for creating families of related product objects (widgets). Concrete factories (like `MotifFactory`) implement this interface to produce specific instances.

```java
// Product Interfaces
interface ScrollBar extends Glyph {}
interface Button extends Glyph {}

// Concrete Products for the Motif look-and-feel family
class MotifScrollBar implements ScrollBar {
    @Override public void draw(Window w) {}
    @Override public Bounds bounds() { return null; }
    @Override public boolean intersects(Point p) { return false; }
    @Override public void insert(Glyph g, int i) {}
    @Override public void remove(Glyph g) {}
    @Override public Glyph child(int i) { return null; }
    @Override public Glyph parent() { return null; }
}
class MotifButton implements Button {
    @Override public void draw(Window w) {}
    @Override public Bounds bounds() { return null; }
    @Override public boolean intersects(Point p) { return false; }
    @Override public void insert(Glyph g, int i) {}
    @Override public void remove(Glyph g) {}
    @Override public Glyph child(int i) { return null; }
    @Override public Glyph parent() { return null; }
}

/** 
 * Abstract Factory: Interface for creating families of related GUI widgets.
 */
interface GUIFactory {
    ScrollBar createScrollBar();
    Button createButton();
}

/** 
 * Concrete Factory: Manufactures only Motif-styled widgets.
 */
class MotifFactory implements GUIFactory {
    @Override public ScrollBar createScrollBar() { return new MotifScrollBar(); }
    @Override public Button createButton() { return new MotifButton(); }
}

/** Client Application */
class LexiApp {
    private final GUIFactory factory;

    // The specific factory is injected, completely decoupling Lexi from Motif/PM
    public LexiApp(GUIFactory factory) { this.factory = factory; }

    public void buildUI() {
        ScrollBar sb = factory.createScrollBar(); // No hard-coded Motif references!
    }
}
```

---

## Problem 5: Supporting Multiple Window Systems

**Motivation:** Lexi needs to run on different, largely incompatible windowing environments (X Windows, PM, Mac). We cannot rely on an Abstract Factory alone here because vendor hierarchies are incompatible and lack a common interface. Alternatively, creating subclasses for every environment (e.g., `XApplicationWindow`) leads to another subclass explosion.

**Expected Outcome:** A uniform set of windowing abstractions that allows the application programmers to interact with windows logically, while hiding the varying system implementations underneath.

**The Solution: Bridge Pattern**
We separate the windowing abstraction (`Window`) from its implementation (`WindowImp`). `WindowImp` encapsulates window system dependencies and `Window` delegates to an instance of `WindowImp`.

```java
/** 
 * Implementor: Encapsulates window system-dependent code.
 * Driven by what the target window system actually provides.
 */
interface WindowImp {
    void deviceRect(int x0, int y0, int x1, int y1);
}

/** Concrete Implementor for X Windows */
class XWindowImp implements WindowImp {
    @Override public void deviceRect(int x0, int y0, int x1, int y1) {
        System.out.println("Drawing rect via X Windows API");
    }
}

/** Concrete Implementor for Presentation Manager */
class PMWindowImp implements WindowImp {
    @Override public void deviceRect(int x0, int y0, int x1, int y1) {
        System.out.println("Drawing rect via Presentation Manager API");
    }
}

/** 
 * Abstraction: The logical view of a window for application programmers.
 */
class ApplicationWindow {
    private final WindowImp imp; // The Bridge separating logic from implementation

    public ApplicationWindow(WindowImp imp) {
        this.imp = imp;
    }

    /** Logical operation delegating to system-specific implementation. */
    public void drawRect(int x, int y, int w, int h) {
        imp.deviceRect(x, y, x + w, y + h);
    }
}
```

---

## Problem 6: User Operations

**Motivation:** Functionality like cutting, pasting, and changing fonts is accessed via multiple UI elements (menus, buttons). Tying a specific request directly to a specific UI widget via inheritance is inflexible and scatters functionality. Furthermore, we need a unified way to support multi-level undo/redo operations.

**Expected Outcome:** A uniform interface that encapsulates requests, allowing us to parameterize menu items by the request they fulfill and easily maintain a command history for undo operations.

**The Solution: Command Pattern**
We encapsulate requests as `Command` objects. UI elements simply hold a Command and call `execute()` on it. Commands manage their own state and can reverse their effects via `unexecute()`, facilitating a command history list.

```java
/**
 * Command: Provides a uniform interface for issuing and undoing requests.
 */
interface Command {
    void execute();
    void unexecute();
    /** Determines dynamically if the command actually changed state. */
    boolean reversible();
}

/** Concrete Command: Encapsulates the request to change a font. */
class FontCommand implements Command {
    private final String newFont;
    private String previousFont;

    public FontCommand(String newFont) { this.newFont = newFont; }

    @Override public void execute() {
        previousFont = "Arial"; // Mock fetching current font
        System.out.println("Changing font to " + newFont);
    }

    @Override public void unexecute() {
        System.out.println("Reverting font to " + previousFont);
    }

    @Override public boolean reversible() { return true; }
}

/** Invoker: A UI element parameterized by a command. */
class MenuItem {
    private final Command command;
    public MenuItem(Command command) { this.command = command; }

    public void clicked() {
        command.execute();
        // Here we would push 'command' to a history stack for undo support
    }
}
```

---

## Problem 7: Spelling Checking and Hyphenation

**Motivation:** Textual analysis (spelling, hyphenation) requires traversing scattered data structures (lists, arrays). Putting traversal logic inside glyphs biases the `Glyph` interface toward specific data structures. Conversely, putting analytical operations directly into the `Glyph` interface causes severe class bloat every time a new analysis is added.

**Expected Outcome:** A decoupled mechanism where traversal logic is separated from the data structures, and analytical logic is separated from the structural hierarchy, allowing new analyses to be added without touching the `Glyph` classes.

**The Solution: Iterator & Visitor Patterns**
1. We encapsulate access and traversal algorithms in **Iterators**, shielding clients from internal data structures.
2. We encapsulate the analysis logic in a **Visitor**, allowing open-ended analyses without modifying the `Glyph` hierarchy.

**Modern Java Adaptation (JDK 25):** While traditional GoF uses double-dispatch (`accept(Visitor)`), modern Java's Pattern Matching for Switch (JEP 441) on sealed interfaces provides a functional and arguably cleaner alternative to the classic Visitor, extracting behavior cleanly without touching the `Glyph` classes at all.

```java
// 1. Iterator Pattern: Abstracts the traversal algorithm
interface GlyphIterator {
    void first();
    void next();
    boolean isDone();
    Glyph currentItem();
}

class ListIterator implements GlyphIterator {
    private final java.util.List<Glyph> list;
    private int current = 0;

    public ListIterator(java.util.List<Glyph> list) { this.list = list; }
    @Override public void first() { current = 0; }
    @Override public void next() { current++; }
    @Override public boolean isDone() { return current >= list.size(); }
    @Override public Glyph currentItem() { return list.get(current); }
}

// 2. Visitor Pattern via Modern Java Pattern Matching (replaces double dispatch)
class SpellingChecker {
    private final java.util.List<String> misspellings = new java.util.ArrayList<>();

    /** 
     * The analysis logic is fully encapsulated here.
     * Because Glyph is sealed, the compiler ensures exhaustiveness.
     */
    public void analyze(Glyph glyph) {
        switch(glyph) {
            case CharacterGlyph c -> checkCharacter(c);
            case Row r            -> System.out.println("Ignoring row structural elements");
            case Column c         -> System.out.println("Ignoring column structural elements");
            case Composition c    -> System.out.println("Ignoring composition");
            case MonoGlyph m      -> analyze(m.child(0)); // Recursive descent through decorators
        }
    }

    private void checkCharacter(CharacterGlyph c) {
        // Mock spelling check logic
        System.out.println("Checking character for spelling...");
    }
}
```

---

## Summary

We've applied eight different patterns to Lexi's design:
1. **Composite:** To represent the document's physical structure.
2. **Strategy:** To allow different formatting algorithms.
3. **Decorator:** For embellishing the user interface.
4. **Abstract Factory:** For supporting multiple look-and-feel standards.
5. **Bridge:** To allow multiple windowing platforms.
6. **Command:** For undoable user operations.
7. **Iterator:** For accessing and traversing object structures.
8. **Visitor:** For allowing an open-ended number of analytical capabilities without complicating the document structure's implementation.

None of these design issues is limited to document editing applications like Lexi. Most nontrivial applications will have occasion to use many of these patterns, though perhaps to do different things. 

For example:
- A financial analysis application might use the **Composite** pattern to define investment portfolios made up of subportfolios and accounts of different sorts.
- A compiler might use the **Strategy** pattern to allow different register allocation schemes for different target machines.
- Applications with a graphical user interface will probably apply at least **Decorator** and **Command** just as we have here.

---

---

## 📚 References & Acknowledgments

This documentation is a structured adaptation of the foundational concepts presented in:
> **Design Patterns: Elements of Reusable Object-Oriented Software** 
> *Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides (The Gang of Four)*

### Documentation Notes:
* **Source Material:** The theoretical definitions, problem statements, and classic design motivations are derived from the original 1994 text.
* **Modern Adaptations:** The code implementations and architectural summaries have been updated for **Modern Java (JDK 21+)**, utilizing contemporary features such as:
    * **Sealed Interfaces & Classes** (JEP 409) for controlled hierarchies.
    * **Records** (JEP 395) for immutable data carriers.
    * **Pattern Matching for switch** (JEP 441) as a functional alternative to the Visitor pattern.
    * **Functional Interfaces & Lambdas** for Strategy and Observer implementations.
* **Purpose:** This repository serves as a personal reference guide and a bridge between classic GoF principles and modern software engineering practices.