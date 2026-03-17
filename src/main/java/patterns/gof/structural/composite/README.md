# Composite - Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](CompositeDemonstration.java)**

## Intent & Core Problem
Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.

**☕ Java Note:**
> In modern Java, this pattern is essential for building hierarchical structures like UI component trees, file systems, or complex rule engines. It heavily relies on polymorphism to ensure that a client doesn't need to know whether it's dealing with a single leaf node or a complex branch containing multiple elements.

---

## Motivation & Real-World Analogy
Graphics applications like drawing editors and schematic capture systems let users build complex diagrams out of simple components. The user can group components to form larger components, which in turn can be grouped to form still larger components. A simple implementation could define classes for graphical primitives such as `Text` and `Line` plus other classes that act as containers for these primitives.

However, treating primitive and container objects differently makes the application more complex, even if the user treats them identically most of the time. The Composite pattern describes how to use recursive composition so that clients don't have to distinguish between these object types.

The key to the Composite pattern is an abstract class or interface that represents both primitives and their containers. For the graphics system, this is `Graphic`. `Graphic` declares operations like `draw()` that are specific to graphical objects, as well as operations for accessing and managing children.

### Graphic Example Class Structure

```text
      ┌───────────────────┐
      │    «interface»    │<─────────────────────────────────────┐
      │      Graphic      │                                      │
      ├───────────────────┤                                      │
      │ draw()            │                                      │
      │ add(Graphic)      │                                      │
      │ remove(Graphic)   │                                      │
      │ getChild(int)     │                                      │
      └───────────────────┘                                      │
                ^                                                │
                │                                                │
      ┌─────────┼──────────────┬────────┐                        │
      │         │              │        │                        │
    ┌─┴────┐ ┌──┴────────┐ ┌───┴──┐ ┌───┴──────────────┐ graphics│
    │ Line │ │ Rectangle │ │ Text │ │     Picture      │<>───────┘   
    ├──────┤ ├───────────┤ ├──────┤ ├──────────────────┤   
    │draw()│ │draw()     │ │draw()│ │draw() <>─────────│──────── for all g in graphics: 
    └──────┘ └───────────┘ └──────┘ │add(Graphic g) <>─│───┐         g.draw()  
                                    │...               │   │
                                    └──────────────────┘   └──── add g to list of graphics
```
**Diagram Description:** A UML class diagram showing the `Graphic` interface with fundamental operations like `draw()`, `add()`, `remove()`, and `getChild()`. `Line`, `Rectangle`, and `Text` implement `Graphic` as leaf nodes. `Picture` implements `Graphic` but also aggregates a collection of `Graphic` objects, demonstrating recursive composition.

**☕ Java Note:** 
> In modern Java, child-management methods in the base interface (`add`, `remove`) often use `default` methods that throw an `UnsupportedOperationException`, allowing leaf classes to ignore them while composites override them.

The subclasses `Line`, `Rectangle`, and `Text` define primitive graphical objects. Since primitive graphics have no child graphics, none of these subclasses implements child-related operations natively (they would throw an exception or do nothing). The `Picture` class defines an aggregate of `Graphic` objects. `Picture` implements `draw()` to call `draw()` on its children, and it implements child-related operations accordingly. Because the `Picture` interface conforms to the `Graphic` interface, `Picture` objects can compose other `Pictures` recursively.

### Object Composition Example

```text
  aPicture
┌──────────┐      aLine
│          │ ──> ┌─────┐
│          │     └─────┘
│          │      aRectangle
│          │ ──> ┌─────┐
│          │     └─────┘
│          │      aText
│          │ ──> ┌─────┐
│          │     └─────┘
│          │      aPicture       aLine
│          │ ──> ┌────────┐ ──> ┌─────┐
└──────────┘     │        │     └─────┘
                 │        │      aLine
                 │        │ ──> ┌─────┐
                 └────────┘     └─────┘
```
**Diagram Description:** An object diagram depicting an instance of `Picture` (`aPicture`) holding references to `aLine`, `aRectangle`, `aText`, and another `aPicture`. The nested `aPicture` holds references to two `aLine` objects.

---

## Applicability
Use the Composite pattern when:
* You want to represent part-whole hierarchies of objects.
* You want clients to be able to ignore the difference between compositions of objects and individual objects. Clients will treat all objects in the composite structure uniformly.

---

## Structure & Participants

### Common Structure

```text
    ┌──────────────────┐            ┌───────────────────┐
    │      Client      │ ────────>  │     Component     │<────────────────┐
    └──────────────────┘            ├───────────────────┤                 │
                                    │ operation()       │                 │
                                    │ add(Component)    │                 │
                                    │ remove(Component) │                 │
                                    │ getChild(int)     │                 │
                                    └───────────────────┘                 │
                                             ^                            │
                                             │                            │
                                  ┌──────────┴─────────┐                  │
                                  │                    │                  │
                             ┌────┴──────┐      ┌──────┴───────┐ children │
                             │   Leaf    │      │  Composite   │<>────────┘          
                             ├───────────┤      ├──────────────┤          
                             │operation()│      │operation() --│----------- for all g in children:      
                             └───────────┘      │add(Component)│                g.operation()
                                                │...           │
                                                └──────────────┘
```
**Diagram Description:** The generic structural UML diagram for the Composite pattern. The `Client` interacts with the `Component` abstraction. `Leaf` implements the core operations, while `Composite` implements the operations by delegating them to its children, managing the collection of `Component` references.

* **Component (`Graphic`)**: Declares the interface for objects in the composition and implements default behavior for the interface common to all classes, as appropriate. It declares an interface for accessing and managing its child components.
* **Leaf (`Rectangle`, `Line`, `Text`)**: Represents leaf objects in the composition. A leaf has no children. It defines behavior for primitive objects in the composition.
* **Composite (`Picture`)**: Defines behavior for components having children, stores child components, and implements child-related operations in the `Component` interface.
* **Client**: Manipulates objects in the composition through the `Component` interface.

### Typical Object Structure

```text
aClient ──▶ aComposite ──▶ aLeaf
                │
                ▼
            aComposite ──▶ aLeaf
                │
                ▼
              aLeaf
```
**Diagram Description:** An object diagram showing a `Client` interacting with a root `Composite`. The root holds references to a `Leaf` and another `Composite`, which in turn holds its own `Leaf` nodes.

---

## Collaborations
Clients use the `Component` interface to interact with objects in the composite structure. If the recipient is a `Leaf`, the request is handled directly. If the recipient is a `Composite`, it typically forwards requests to its child components, possibly performing additional operations before and/or after forwarding.

---

## Consequences
The Composite pattern provides the following benefits and trade-offs:
* **Defines class hierarchies consisting of primitive objects and composite objects.** Primitive objects can be composed into more complex objects, recursively.
* **Makes the client simple.** Clients can treat composite structures and individual objects uniformly. They don't need to write branching logic (like `instanceof` checks) to determine if they are handling a leaf or a node.
* **Makes it easier to add new kinds of components.** Newly defined `Composite` or `Leaf` subclasses work automatically with existing structures and client code.
* **Can make your design overly general.** The disadvantage of making it easy to add new components is that it makes it harder to restrict the components of a composite. You might want a composite to only have certain components. With this pattern, you can't rely on the type system to enforce those constraints; instead, you must use run-time checks.

---

## Implementation Hints & Modern Java Context
* **Transparency vs. Safety:** Declaring child-management operations in the base `Component` interface gives clients transparency (they treat all elements exactly the same). However, it costs safety because clients might try to do meaningless things like add a component to a `Leaf`. Conversely, moving those operations solely to `Composite` ensures safety but loses transparency. In Java, a common middle ground is to define the methods in the base interface but provide `default` implementations that throw `UnsupportedOperationException`.
* **Explicit Parent References:** Maintaining a reference from a child to its parent can simplify traversal and deletion. This is easily achieved by having the `Composite#add()` method pass `this` to the child.
* **Data Structures:** Modern Java offers robust collections. Use `List<Component>` for ordered children or `Set<Component>` if duplicates aren't allowed.
* **Caching:** If a composite operation is expensive (e.g., calculating the total size of a massive directory tree), cache the results. Ensure children invalidate their parents' cache when they change.

---

## Known Uses (Modern Java)
* **AWT and Swing (`java.awt.Component` and `java.awt.Container`):** The foundation of Java's traditional GUI. A `Container` (like a `JPanel`) is a `Component` that can hold other `Component`s (like `JButton` or other `JPanel`s).
* **JavaFX (`javafx.scene.Node` and `javafx.scene.Parent`):** The modern UI toolkit for Java uses a similar scene graph hierarchy.
* **DOM (Document Object Model):** Frameworks parsing XML or HTML represent documents as trees where elements, text nodes, and attributes are uniformly treated as Nodes.
* **JUnit 5 (`org.junit.platform.engine.TestDescriptor`):** Represents the hierarchical structure of a test suite (containers and individual tests).

---

## Related Patterns
* Often the component-parent link is used for a **Chain of Responsibility**.
* **Decorator** is often used with Composite. When decorators and composites are used together, they will usually have a common parent class. So decorators will have to support the Component interface with operations like `add()`, `remove()`, and `getChild()`.
* **Flyweight** lets you share components, but they can no longer refer to their parents.
* **Iterator** can be used to traverse composites cleanly without exposing their internal representations.
* **Visitor** localizes operations and behavior that would otherwise be distributed across Composite and Leaf classes.