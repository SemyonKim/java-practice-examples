# Adapter - Class, Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](AdapterDemonstration.java)**

## Intent & Core Problem
Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.

> **Also Known As:** Wrapper

**☕ Java Note:**
> In modern Java development, Adapters are ubiquitous. They often act as the "glue" layer between your core domain logic and external dependencies (like third-party libraries, legacy APIs, or distinct architectural layers).

---

## Motivation & Real-World Analogy
Sometimes a toolkit class that is designed for reuse isn't reusable only because its interface does not match the domain-specific interface an application requires.

Consider a drawing editor that lets users draw and arrange graphical elements into pictures and diagrams. The editor's key abstraction is the graphical object, defined by an interface called `Shape`. The editor defines concrete implementations for each graphical object: `LineShape`, `PolygonShape`, etc.

While elementary geometric shapes are straightforward, a `TextShape` subclass that can display and edit text is considerably more difficult to implement. Meanwhile, an off-the-shelf user interface toolkit might already provide a sophisticated `TextView` class for displaying and editing text. Ideally, we would reuse `TextView` to implement `TextShape`, but the toolkit wasn't designed with `Shape` in mind, so their interfaces are incompatible.

We can define `TextShape` so that it adapts the `TextView` interface to `Shape`. We can do this in two ways:
1. By inheriting `Shape`'s interface and `TextView`'s implementation (a **Class Adapter**).
2. By composing a `TextView` instance within a `TextShape` and implementing `Shape` in terms of `TextView`'s interface (an **Object Adapter**).

```text
  +----------------+        +-----------------+
  |    Client      |------->|    <<Target>>   |
  | (DrawingEditor)|        |      Shape      |
  +----------------+        +-----------------+
                            | getBoundingBox()|
                            | createManip()   |
                            +--------+--------+
                                     ^
                                     | implements
                            +--------+--------+      +-------------------+
                            |    TextShape    |      |   <<Adaptee>>     |
                            |    (Adapter)    |      |     TextView      |
                            +-----------------+      +-------------------+
                            | getBoundingBox()|----->| getExtent()       |
                            | createManip()   |      |                   |
                            +-----------------+      +-------------------+
```
*Diagram Description: The `DrawingEditor` (Client) expects a `Shape`. `TextShape` implements `Shape` but delegates the actual work to the incompatible `TextView` (Adaptee). By implementing the `createManip()` operation to return a `TextManipulator`, the `TextShape` adapter provides the interactive dragging functionality that `TextView` lacks but the `Shape` interface requires. This allows the adapter to fulfill its responsibility of augmenting an adapted class to meet specific system requirements.*

---

## Applicability
Use the Adapter pattern when:
* You want to use an existing class, and its interface does not match the one you need.
* You want to create a reusable class that cooperates with unrelated or unforeseen classes, that is, classes that don't necessarily have compatible interfaces.
* *(Object Adapter only)* You need to use several existing subclasses, but it's impractical to adapt their interface by subclassing everyone. An object adapter can adapt the interface of its parent class.

---

## Structure & Participants

There are two primary ways to structure an Adapter: Class Adapter and Object Adapter.

### 1. Class Adapter
A class adapter uses multiple inheritance to adapt one interface to another.

**☕ Java Note:**
> Because Java does not support multiple class inheritance, a Class Adapter in Java is implemented by *implementing* the expected interface (`Target`) and *extending* the existing class (`Adaptee`).

```text
  +-------------+        +-----------------+        +--------------------+
  |   Client    |------->|  <<Interface>>  |        |      Adaptee       |
  +-------------+        |     Target      |        +--------------------+
                         +-----------------+        | specificRequest()  |
                         | request()       |        +------+-------------+
                         +--------+--------+               ^
                                  ^                        |
                                  |                        | 
                       implements +--------------+---------+ extends implementation
                                                 | 
                                        +--------+--------+                              
                                        |     Adapter     |                              
                                        +-----------------+                              
                                        | request() ------|------> calls super.specificRequest()
                                        +-----------------+
```

### 2. Object Adapter
An object adapter relies on object composition.

```text
  +-------------+        +-----------------+        +-------------------+
  |   Client    |------->|  <<Interface>>  |        |     Adaptee       |
  +-------------+        |     Target      |        +-------------------+
                         +-----------------+        | specificRequest() |
                         | request()       |        +-------------------+
                         +--------+--------+                ^
                                  ^                         |
                                  | implements              |
                         +--------+--------+                |
                         |     Adapter     |----------------+ uses adaptee field
                         +-----------------+      
                         | request() ------|------> calls adaptee.specificRequest()   
                         +-----------------+
```

### Participants
* **Target** (`Shape`): Defines the domain-specific interface that Client uses.
* **Client** (`DrawingEditor`): Collaborates with objects conforming to the Target interface.
* **Adaptee** (`TextView`): Defines an existing interface that needs adapting.
* **Adapter** (`TextShape`): Adapts the interface of Adaptee to the Target interface.

---

## Collaborations
* Clients call operations on an Adapter instance. In turn, the adapter calls Adaptee operations that carry out the request.

---

## Consequences (Trade-offs)

**Class Adapters:**
* **Pros:** Adapts Adaptee to Target by committing to a concrete Adaptee class. It lets the Adapter override some of Adaptee's behavior easily since Adapter is a subclass. Introduces only one object, requiring no additional pointer indirection.
* **Cons:** Won't work when we want to adapt a class *and* all its subclasses. In Java, you are locked into extending one specific class, consuming your single inheritance allowance.

**Object Adapters:**
* **Pros:** Lets a single Adapter work with many Adaptees (the Adaptee itself and all of its subclasses). The Adapter can add functionality to all Adaptees at once.
* **Cons:** Makes it harder to override Adaptee behavior. It requires subclassing the Adaptee and making the Adapter refer to the subclass rather than the Adaptee itself.

**☕ Java Note on Pluggable Adapters:**
> The original GoF text discusses "Pluggable Adapters" to minimize the assumptions a class makes about its environment. In modern Java, this is elegantly achieved using Lambdas and Functional Interfaces (e.g., `Function<T, R>`, `Consumer<T>`). Instead of creating a whole Adapter class, a client can be parameterized with a lambda expression that acts as a lightweight, inline object adapter.

---

## Implementation Hints & Modern Java Context

1. **How much adaptation does Adapter do?** Adapters vary in the amount of work they do to match interfaces. It ranges from simple interface translation (changing operation names) to supporting an entirely different set of operations.
2. **Two-Way Adapters to provide transparency:** A two-way adapter implements both interfaces, allowing it to act as either the Target or the Adaptee. This is useful when two different clients need to view an object differently. In Java, this is done by having the Adapter implement multiple interfaces.

**☕ Java Best Practices:**
> * **Favor Object Adapters:** Prefer object composition over class inheritance. Object adapters are far more flexible, easier to test (you can mock the Adaptee), and bypass Java's single-inheritance limitation.
> * **Default Methods:** In Java 8+, interfaces can have `default` methods. This can sometimes reduce the need for an Adapter if the interface can provide a sensible default mapping itself, though it shouldn't be abused to mix domains.

---

## Known Uses (Modern Java)
* **`java.io.InputStreamReader`:** This is a classic Object Adapter. It adapts a byte stream (`InputStream`) into a character stream (`Reader`).
* **`java.util.Arrays#asList()`:** Adapts an array into the `List` interface.
* **SLF4J (Simple Logging Facade for Java):** Acts as a massive adapter layer, routing logging calls from its unified interface to various incompatible underlying logging frameworks (Logback, Log4j, `java.util.logging`).

---

## Related Patterns
* **Bridge:** Has a structure similar to an object adapter, but a different intent. Bridge is meant to separate an interface from its implementation so they can vary independently. An adapter is meant to change the interface of an *existing* object.
* **Decorator:** Enhances another object without changing its interface. A decorator is thus more transparent to the application than an adapter and supports recursive composition.
* **Proxy:** Defines a representative or surrogate for another object without changing its interface.