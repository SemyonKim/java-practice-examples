# Prototype - Object Creational

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](PrototypeDemonstration.java)**

## Intent & Core Problem
Specify the kinds of objects to create using a prototypical instance, and create new objects by copying this prototype.

**☕ Java Note:**
> In Java, the core concept of the Prototype pattern is deeply intertwined with object copying mechanisms (like the `Cloneable` interface). However, due to well-documented design flaws in Java's native cloning mechanism, modern implementations often favor copy constructors or explicit custom copy interfaces over `java.lang.Cloneable`.

---

## Motivation & Real-World Analogy
Imagine building a music score editor by customizing a general graphical framework. The editor framework provides a palette of tools for adding objects (like notes, rests, and staves) to the score.

The framework might define an abstract `Graphic` interface for graphical components and a `GraphicTool` for creating these components. However, `GraphicTool` belongs to the framework and doesn't know the specifics of your application's `MusicalNote` or `Staff` classes. Creating a specific subclass of `GraphicTool` for every possible music object would lead to an explosion of classes that differ only in the object they instantiate.

The Prototype pattern solves this by having `GraphicTool` store a *prototype* of the `Graphic` it is supposed to create. When a user needs a new note, the tool simply asks its prototype to clone itself.

```text
+-----------------+       prototype      +-----------------+
|   GraphicTool   | -------------------> |     Graphic     |
+-----------------+                      +-----------------+
| Manipulate()    |                      | Draw()          |
+-----------------+                      | Clone()         |
                                         +-----------------+
                                                  ^
                                                  |
                              +-------------------+-------------------+
                              |                                       |
                      +-----------------+                     +-----------------+
                      |   MusicalNote   |                     |      Staff      |
                      +-----------------+                     +-----------------+
                      | Draw()          |                     | Draw()          |
                      | Clone()         |                     | Clone()         |
                      +-----------------+                     +-----------------+
```

☕ Java Note:
>In a modern context, `Graphic` would be an interface declaring  a `clone()` method with a covariant return type, allowing `MusicalNote` to explicitly return a `MusicalNote` instance instead of a generic `Object`.

**Real-World Analogy:** Think of cellular mitosis. A cell doesn't construct a new cell from scratch using a factory; it duplicates its own DNA and splits into two identical, independent cells. The original cell acts as the prototype.

---

## Applicability
Use the Prototype pattern when a system should be independent of how its products are created, composed, and represented; **and**
* When the classes to instantiate are specified at run-time (e.g., via dynamic loading).
* To avoid building a hierarchy of factory classes that mirrors the hierarchy of product classes.
* When instances of a class can have only a few different combinations of state. It may be more convenient to install a corresponding number of prototypes and clone them rather than instantiating the class manually with the appropriate state each time.

---

## Structure & Participants

```text
+-----------------+       prototype      +-----------------+
|     Client      | -------------------> |    Prototype    |
+-----------------+                      +-----------------+
| Operation()     |                      | Clone()         |
+-o ---------------+                      +-----------------+
  |                                               ^
  | p = prototype.Clone()                         |
  └──────────────────────┘    +-------------------+-------------------+
                              |                                       |
                      +-----------------+                     +-----------------+
                      |ConcretePrototype1                     |ConcretePrototype2
                      +-----------------+                     +-----------------+
                      | Clone()         |                     | Clone()         |
                      +-----------------+                     +-----------------+
                      | return copy     |                     | return copy     |
                      +-----------------+                     +-----------------+
```

☕ Java Note:
> The `Client` holds a reference to the `Prototype` interface. When `Operation()` is invoked, the `Client` calls `Clone()` on the prototype, delegating the creation responsibility entirely to the concrete instances.

* **Prototype (`Graphic`, `MazeComponent`):** Declares an interface for cloning itself.
* **ConcretePrototype (`MusicalNote`, `Room`, `Wall`):** Implements an operation for cloning itself.
* **Client (`GraphicTool`, `MazePrototypeFactory`):** Creates a new object by asking a prototype to clone itself.

---

## Collaborations
A client asks a prototype to clone itself. The client relies on the prototype's self-duplication mechanism rather than using the `new` operator directly.

---

## Consequences (Trade-offs)
Prototype offers many of the same benefits as Abstract Factory and Builder: it hides concrete product classes from the client, reducing coupling. Additional benefits include:

* **Adding and removing products at run-time:** Prototypes let you incorporate a new concrete product class into a system simply by registering a prototypical instance with the client.
* **Specifying new objects by varying values:** Highly dynamic systems let you define new behavior through object composition—by specifying values for an object's variables. You can instantiate new "classes" by configuring existing objects and registering them as prototypes.
* **Specifying new objects by varying structure:** Applications can build complex, composite structures from parts and use the entire structure as a prototype for further duplication (e.g., copying a complex circuit component in an electrical design tool).
* **Reduced subclassing:** Unlike Factory Method, which often requires an overriding creator class for every product class, Prototype relies on object composition and cloning.

**Drawbacks:**
* The primary liability is that implementing the `Clone()` operation can be extremely difficult, particularly when objects contain circular references or complex object graphs that require deep copying rather than shallow copying.

---

## Implementation Hints & Modern Java Context
* **Using a Prototype Manager:** When the number of prototypes in a system isn't fixed, keep a registry of available prototypes (often an associative array mapping names to prototype instances).
* **Shallow vs. Deep Copy:** 
  * *Shallow Copy:* Copies primitive fields and *references* to objects. If the prototype modifies an internal object reference, the clone sees the change.
  * *Deep Copy:* Recursively copies all referenced objects. This is much harder to implement due to circular references but is often required if prototypes and clones must be completely independent.
* **Initialization Requirements:** While `clone()` creates a duplicate, sometimes clients need to initialize the clone to specific, distinct states. Some designs introduce an `Initialize(...)` method that clients call immediately after cloning.

**☕ Java Context:**
* **The `Cloneable` Pitfall:** Java's native `java.lang.Cloneable` interface is an empty marker interface. It relies on `Object.clone()`, which performs a shallow copy and throws `CloneNotSupportedException`. Joshua Bloch (author of *Effective Java*) strongly recommends avoiding `Cloneable`.
* **Copy Constructors:** A safer modern Java approach is using a "Copy Constructor" (e.g., `public Room(Room source)`) or a static factory method (e.g., `public static Room newInstance(Room source)`). This avoids the reflection and exception-handling mess of `Object.clone()`.
* **Serialization:** For deep copying complex object graphs, developers sometimes serialize the object to a byte stream and deserialize it back. While effective for deep copies, it carries a severe performance penalty.

---

## Modern Java Architectural Uses
* **Spring Framework:** The `@Scope("prototype")` annotation tells the Spring IoC container to create and return a *new* instance of a bean every time it is requested, functioning conceptually as a prototype factory (though often implemented via reflection/CGLIB rather than explicit cloning).
* **Java Core:** `java.util.Date` and various `Collection` implementations (like `ArrayList`) implement custom `clone()` methods to allow quick duplication of standard data structures.

---

## Related Patterns
* **Abstract Factory:** Prototype and Abstract Factory are often competing patterns. However, they can also work together: an Abstract Factory might store a set of Prototypes from which to clone and return product objects.
* **Composite & Decorator:** Designs that make heavy use of these patterns often benefit from Prototype to easily duplicate complex, nested object structures.