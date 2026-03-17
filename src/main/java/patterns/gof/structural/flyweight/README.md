# Flyweight - Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](FlyweightDemonstration.java)**

## Intent & Core Problem
Use sharing to support large numbers of fine-grained objects efficiently.

**☕ Java Note:**
> In modern Java, the Flyweight pattern is embedded directly into the core language and standard library to save memory. The string constant pool and wrapper class caches (like `Integer.valueOf()`) are prime examples of the Flyweight pattern in action.

---

## Motivation & Real-World Analogy
Some applications could greatly benefit from an object-oriented design throughout, but naive implementations become prohibitively expensive.

Consider a document editor. Typically, editors use objects to represent embedded elements like tables and figures. However, they usually stop short of using an object for *each character*, even though doing so would promote extreme flexibility (allowing uniform treatment of characters and graphical elements). Object structures mimicking the physical document would be highly extensible, allowing new character sets without disturbing existing functionality.

**The Conceptual Document Structure**
```text
                  ┌─────────┐
                  │ Column  │
                  └─────────┘
                   /   |   \
       ┌─────────┐ ┌───────┐ ┌─────────┐
       │   Row   │ │  Row  │ │   Row   │
       └─────────┘ └───────┘ └─────────┘
         |             |             |
        'a' ...   ... 'l' ...   ... 'a' 
```
*Description: A physical document structure where every element, down to the character, is treated as an object.*

The drawback is the sheer cost: moderate-sized documents would require hundreds of thousands of character objects, consuming massive amounts of memory and incurring unacceptable run-time overhead.

The Flyweight pattern describes how to share objects to allow their use at fine granularities without these prohibitive costs. A **flyweight** is a shared object usable in multiple contexts simultaneously, acting as an independent instance in each context. Flyweights cannot make assumptions about the context in which they operate.

The critical concept is the distinction between **intrinsic** and **extrinsic** state:
* **Intrinsic state:** Stored inside the flyweight. It consists of context-independent information (e.g., the character code 'a'), making it sharable.
* **Extrinsic state:** Depends on and varies with the flyweight's context (e.g., coordinate position, typographic style) and cannot be shared. Client objects must pass this state to the flyweight when invoking its operations.

**Logically, an Object for Every Character**
```text
 ┌───────────────┐  ┌───────────────┐  ┌───────────────┐
 │ Character 'a' │  │ Character 'l' │  │ Character 'a' │
 │ font: Times   │  │ font: Times   │  │ font: Arial   │
 │ size: 12      │  │ size: 12      │  │ size: 14      │
 │ pos: x1, y1   │  │ pos: x2, y2   │  │ pos: x3, y3   │
 └───────────────┘  └───────────────┘  └───────────────┘
```
*Description: A naive approach where every character instance holds both its identity and its formatting/positional data.*

**Physically, Shared Flyweights**
```text
 Contexts (Extrinsic)                Shared Flyweight Pool (Intrinsic)
 
 [Row: 1, font: Times] ────────┐     ┌───────────────┐
                               ├───> │ Character 'a' │
 [Row: 3, font: Arial] ────────┘     └───────────────┘
 
                                     ┌───────────────┐
 [Row: 2, font: Times] ────────────> │ Character 'l' │
                                     └───────────────┘
```
*Description: The physical implementation. The Context computes or stores the formatting, while pointers point to a single shared instance of the 'a' character.*

**The Glyph Hierarchy**
```text
                                  ┌────────────────────────────┐
                                  │           Glyph            │
                                  ├────────────────────────────┤
                                  │ draw(Context)              │
                                  │ intersects(Point, Context) │
                                  └────────────────────────────┘
                                                 ^ 
                                                 │
               ┌─────────────────────────────────│─────────────────────────────────┐
               │                                 │                                 │
 ┌────────────────────────────┐    ┌────────────────────────────┐    ┌────────────────────────────┐
 │         Character          │    │            Row             │    │           Column           │
 ├────────────────────────────┤    ├────────────────────────────┤    ├────────────────────────────┤
 │ charCode: char             │    │ children: List<Glyph>      │    │ children: List<Glyph>      │
 ├────────────────────────────┤    ├────────────────────────────┤    ├────────────────────────────┤
 │ draw(Context)              │    │ draw(Context)              │    │ draw(Context)              │
 │ intersects(Point, Context) │    │ intersects(Point, Context) │    │ intersects(Point, Context) │
 └────────────────────────────┘    └────────────────────────────┘    └────────────────────────────┘
```

*Description: `Glyph` is the abstract class for graphical objects. Operations dependent on extrinsic state (`draw`, `intersects`) have the context passed to them. A flyweight representing a letter only stores the character code (intrinsic state); it doesn't store location or font. Clients supply the context-dependent information.*

Because the number of distinct character objects is far less than the total characters in a document, memory usage plummets. A typical document will allocate roughly 100 character objects (the size of the ASCII set) regardless of length. This makes an object abstraction practical for individual characters.

---

## Applicability
Apply the Flyweight pattern when **all** the following are true:
* An application uses a very large number of objects.
* Storage costs are high because of the sheer quantity of objects.
* Most object state can be made extrinsic (moved outside the object).
* Many groups of objects may be replaced by relatively few shared objects once extrinsic state is removed.
* The application doesn't depend on object identity (since shared objects are conceptually distinct in different contexts but physically the same instance).

---

## Structure & Participants

**Common Flyweight Structure**
```text
            ┌───────────────────┐ flyweights    ┌───────────────────────────┐
     ┌----->│ FlyweightFactory  │o─────────────>│        Flyweight          │
     │      ├───────────────────┤               ├───────────────────────────┤
     │      │ GetFlyweight(key) │               │ Operation(extrinsicState) │
     │      └───────+───────────┘               └─────────────+─────────────┘
     │              │                                         ^
     │              v                                         │
     │       ┌──────+───────────┐           ┌─────────────────+─────────────────────┐
     │       │   flyweightPool  │           │                                       │
     │       └──────────────────┘           │                                       │
     │                         ┌────────────+──────────────┐         ┌──────────────+────────────┐
     │                    ┌--->│    ConcreteFlyweight      │    ┌--->│ UnsharedConcreteFlyweight │
     │                    │    ├───────────────────────────┤    │    ├───────────────────────────┤
     │                    │    │ intrinsicState            │    │    │ allState                  │
     │                    │    ├───────────────────────────┤    │    ├───────────────────────────┤
     │                    │    │ Operation(extrinsicState) │    │    │ Operation(extrinsicState) │
 ┌───+────┐               │    └───────────────────────────┘    │    └───────────────────────────┘
 │ Client │---------------┘-------------------------------------┘
 └────────┘
```
*Description: The architectural structure of the Flyweight pattern, delineating shared and unshared components.*

**flyweightPool Structure**
```text
                                        ┌─────────┐          ┌─────────┐
                                        │ aClient │--┐    ┌--│ aClient │-----┐
                                        └─────────┘  │    │  └─────────┘     │
                                                     │    │                  │
                            ┌────────────────────────│────│──────────────────│──────────────┐
                            │ flyweight              V    V                  V              │
 ┌───────────────────┐      │ pool      ┌────────────+────+──┐      ┌────────+───────────┐  │
 │ aFlyweightFactory │      │      ┌--->│ aConcreteFlyweight │  ┌-->│ aConcreteFlyweight │  │
 ├───────────────────┤      │      │    ├────────────────────┤  │   ├────────────────────┤  │
 │ flyweights    ┐---│------│------┘    │ intrinsicState     │  │   │ intrinsicState     │  │
 └───────────────│───┘      │           └────────────────────┘  │   └────────────────────┘  │
                 │          └───────────────────────────────────│───────────────────────────┘
                 └----------------------------------------------┘
```
*Description: Runtime object diagram showing multiple clients referencing the same shared instances managed by the factory.*

### Participants
* **Flyweight (`Glyph`)**: Declares an interface through which flyweights can receive and act on extrinsic state.
* **ConcreteFlyweight (`Character`)**: Implements the Flyweight interface and adds storage for intrinsic state. It must be sharable and independent of context.
* **UnsharedConcreteFlyweight (`Row`, `Column`)**: The interface enables sharing but doesn't enforce it. Unshared composite objects often have `ConcreteFlyweight` objects as children.
* **FlyweightFactory**: Creates and manages flyweight objects, ensuring they are shared properly. It supplies an existing instance or creates one if it doesn't exist.
* **Client**: Maintains references to flyweights and computes or stores their extrinsic state.

---

## Collaborations
* State must be strictly characterized as intrinsic (stored in `ConcreteFlyweight`) or extrinsic (computed/stored by `Client` objects). Clients pass extrinsic state to flyweight operations.
* Clients must *never* instantiate `ConcreteFlyweights` directly; they must obtain them exclusively from the `FlyweightFactory` to guarantee proper sharing.

---

## Consequences
* **Run-time Costs:** May introduce overhead associated with transferring, finding, or computing extrinsic state (especially if it was formerly intrinsic).
* **Storage Savings:** These costs are vastly offset by memory savings. Savings increase based on: the reduction of total instances, the amount of intrinsic state, and whether extrinsic state is computed rather than stored. The greatest savings occur when extrinsic state is computed.
* **Architectural Impact:** Often combined with the *Composite* pattern to represent hierarchical graphs with shared leaf nodes. Consequently, leaf nodes cannot store parent pointers (since they are shared across multiple parents); parent pointers must be passed as extrinsic state.

---

## Implementation Hints & Modern Java Context

1. **Removing Extrinsic State**

   The pattern's applicability hinges on how easily you can identify and extract extrinsic state. Removing it offers no benefit if managing the extrinsic state requires as much storage as the objects did before sharing. Ideally, extrinsic state is computed from a separate, highly compressed structure.

   **Extrinsic State Mapping (The BTree Approach)**
   In a document editor, instead of storing fonts with each character, a separate mapping structure tracks "runs" of characters sharing typographic attributes. When a character draws itself, it receives its attributes dynamically.

    **Excerpt from Glyph Composition**
    ```text
    Index:  102  103  104  105  106  107
    Char:    e    x    p    e    c    t
    ```
   *Description: A specific run of text starting at index 102.*

    **Initial BTree Structure**
    ```text
         ┌─────────────────────┐
         │ 102 : defaultFont   │
         └─────────────────────┘
    ```
   *Description: The initial state where text is mapped to a default font. Note: the BTree maps ranges to fonts.*

   If we update the word "expect" (length 6) to a 12-point Times Roman font (`times12`):
    **BTree Updated for "expect"**
    ```text
                    ┌─────────────────┐
                    │      Node       │
                    └───────┬─────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
       ┌──────────────┐            ┌──────────────┐
       │ 102: times12 │            │ 6: default   │
       └──────────────┘            └──────────────┘
    ```
   *Description: The tree updates to isolate the 6-character run of "expect" with the `times12` font.*

   If we insert "don't " (length 6) in `timesItalic12` before "expect":
    **BTree Updated for Insertion**
    ```text
                          ┌─────────────────┐
                          │      Node       │
                          └───────┬─────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
            ┌───────────────┐           ┌─────────────────┐
            │102:timesItalic│           │      Node       │
            └───────────────┘           └───────┬─────────┘
                                                │
                                  ┌─────────────┴─────────────┐
                                  ▼                           ▼
                           ┌─────────────┐             ┌─────────────┐
                           │ 6: times12  │             │ 6: default  │
                           └─────────────┘             └─────────────┘
    ```
   *Description: The tree scales logarithmically, breaking ranges down as new styles are applied. Because font changes are infrequent compared to document length, the tree remains small, keeping storage costs down without severely impacting lookup time.*

2. **Managing Shared Objects:**
   Because objects are shared, clients use an associative store (the `FlyweightFactory`) to look up flyweights. The factory returns existing instances or creates new ones. While garbage collection is sometimes needed to reclaim unused flyweights, it is unnecessary for small, fixed sets (like the 128 ASCII characters), which are kept permanently.

**☕ Java Insights:**
> * **Records for Context:** Java `record` types (Java 14+) are perfect for passing extrinsic state (like the `GlyphContext` mentioned above) because they are immutable and highly optimized.
> * **NavigableMap:** Instead of writing a custom `BTree`, Java developers can use `java.util.TreeMap` or `NavigableMap<Integer, StyleContext>` to efficiently map character indices to stylistic runs.
> * **Concurrency:** When building a `FlyweightFactory`, use `ConcurrentHashMap.computeIfAbsent()` to guarantee thread-safe sharing and lazy initialization without explicit locking overhead.

---

## Known Uses (Modern Java)
* **`java.lang.String` Pool:** Java natively implements Flyweight for String literals. Identical string literals share the exact same memory reference in the JVM's String Pool.
* **Wrapper Class Caches:** The `Integer.valueOf(int i)` method caches values from -128 to 127. Similar caches exist for `Byte`, `Character`, `Short`, `Long`, and `Boolean` (which naturally caches `TRUE` and `FALSE`).
* **Game Development / Voxel Engines:** Rendering millions of blocks (like in Minecraft). Instead of millions of distinct `Block` objects containing redundant texture and hardness data, a single `BlockType` flyweight holds the intrinsic data, while the 3D chunk array provides the extrinsic X/Y/Z coordinates.

---

## Related Patterns
* **Composite:** Often combined with Flyweight to implement logically hierarchical structures as directed-acyclic graphs with shared leaf nodes.
* **State / Strategy:** Can be implemented as flyweights if their state is entirely intrinsic, allowing the strategies to be shared across multiple contexts.