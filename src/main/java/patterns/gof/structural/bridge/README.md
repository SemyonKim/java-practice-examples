# Bridge - Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](BridgeDemonstration.java)**

## Intent & Core Problem
Decouple an abstraction from its implementation so that the two can vary independently.

> **Also Known As:** Handle/Body 

**☕ Java Note:**
> In modern Java, this pattern is foundational. It heavily relies on the principle of "composition over inheritance" and is often realized using interfaces for the implementation side and abstract classes for the abstraction side.

---

## Motivation & Real-World Analogy
When an abstraction can have one of several possible implementations, the usual way to accommodate them is to use inheritance. An abstract class defines the interface to the abstraction, and concrete subclasses implement it in different ways. But this approach isn't always flexible enough. Inheritance binds an implementation to the abstraction permanently, which makes it difficult to modify, extend, and reuse abstractions and implementations independently.

Consider the implementation of a portable `Window` abstraction in a user interface toolkit. This abstraction should enable us to write applications that work on both the X Window System and IBM's Presentation Manager (PM), for example. Using inheritance, we could define an abstract class `Window` and subclasses `XWindow` and `PMWindow` that implement the `Window` interface for the different platforms.

But this approach has two drawbacks:

1. It's inconvenient to extend the `Window` abstraction to cover different kinds of windows or new platforms. Imagine an `IconWindow` subclass of `Window` that specializes the `Window` abstraction for icons. To support `IconWindow`s for both platforms, we have to implement two new classes, `XIconWindow` and `PMIconWindow`. Worse, we'll have to define two classes for every kind of window. Supporting a third platform requires yet another new `Window` subclass for every kind of window.

```text
  +-------------+                    +-------------+
  |   Window    |                    |   Window    |
  +-------------+                    +-------------+
         ^                ->                ^
         | extends                          | extends
  +------+------+                    +------+-------------------+
  |             |                    |          |               |
+---------+ +----------+      +---------+ +----------+ +-------------+
| XWindow | | PMWindow |      | XWindow | | PMWindow | | IconWindow  |
+---------+ +----------+      +---------+ +----------+ +-------------+
                                                              ^
                                                              | extends
                                                       +------+-------+
                                                       |              |
                                                +-------------+ +--------------+
                                                | XIconWindow | | PMIconWindow |
                                                +-------------+ +--------------+
```
*Diagram Description: The inheritance combinatorial explosion. Expanding both the window type (e.g., `IconWindow`) and the platform (e.g., X Window, PM) causes the class hierarchy to grow exponentially. Adding a single new concept requires creating subclasses for all existing platform variations.*

2. It makes client code platform-dependent. Whenever a client creates a window, it instantiates a concrete class that has a specific implementation. For example, creating an `XWindow` object binds the `Window` abstraction to the X Window implementation, which makes the client code dependent on the X Window implementation. This, in turn, makes it harder to port the client code to other platforms.

    Clients should be able to create a window without committing to a concrete implementation. Only the window implementation should depend on the platform on which the application runs. Therefore, client code should instantiate windows without mentioning specific platforms.

The Bridge pattern addresses these problems by putting the `Window` abstraction and its implementation in separate class hierarchies. There is one class hierarchy for window interfaces (`Window`, `IconWindow`, `TransientWindow`) and a separate hierarchy for platform-specific window implementations, with `WindowImp` as its root. The `XWindowImp` subclass, for example, provides an implementation based on the X Window System.

```text
    [Bridge]
          +-----------------+ imp                 +--------------------+
          |     Window      |<>------------------>|     WindowImp      |
          +-----------------+                     +--------------------+
          | DrawText()      |                     | DevDrawText()      |
          | DrawRect()  o   |                     | DevDrawLine()      |
          +--------+----|---+                     +----------+---------+
                   ^    +-- imp->DevDrawLine()               ^
                   |        imp->DevDrawLine()               | 
                   |        imp->DevDrawLine()               |
                   |        imp->DevDrawLine()               | 
           extends |                                         | implements
            +------+------+                           +------+------+
            |             |                           |             |
    +-------+-----+ +-----+----------+      +---------+-------+ +-----+---------+
    | IconWindow  | |TransientWindow |      |  XWindowImp     | |  PMWindowImp  |
    +-------------+ +----------------+      +-----------------+ +---------------+
    | DrawBorder()| | DrawCloseBox() |      | DevDrawText() o | | DevDrawLine() |
    | o           | | o              |      | DevDrawLine() | | | DevDrawText() |
    +-|-----------+ +-|--------------+      |  o            | | |               |
      |               |                     +--|------------|-+ +---------------+
      |              DrawRect()                |            |      
     DrawRect()                                |           XDrawString()
     DrawText()                               XDrawLine()       
                                            
```
*Diagram Description: The Bridge pattern applied to the Window example. The `Window` class delegates low-level rendering requests to a `WindowImp` object. This creates a bridge between the window type hierarchy (left) and the rendering platform hierarchy (right).*

All operations on `Window` subclasses are implemented in terms of abstract operations from the `WindowImp` interface. This decouples the window abstractions from the various platform-specific implementations. We refer to the relationship between `Window` and `WindowImp` as a bridge, because it bridges the abstraction and its implementation, letting them vary independently.

---

## Applicability
Use the Bridge pattern when:
* You want to avoid a permanent binding between an abstraction and its implementation. This might be the case, for example, when the implementation must be selected or switched at run-time.
* Both the abstractions and their implementations should be extensible by subclassing. In this case, the Bridge pattern lets you combine the different abstractions and implementations and extend them independently.
* Changes in the implementation of an abstraction should have no impact on clients ; that is, their code should not have to be recompiled.
* You want to hide the implementation of an abstraction completely from clients.
* You have a proliferation of classes as shown earlier in the first Motivation diagram. Such a class hierarchy indicates the need for splitting an object into two parts. Rumbaugh uses the term "nested generalizations" to refer to such class hierarchies.
* You want to share an implementation among multiple objects (perhaps using reference counting), and this fact should be hidden from the client.

---

## Structure & Participants

```text
  +-------------+      +-----------------+ imp                      +-------------------+
  |   Client    |----->|   Abstraction   |<>----------------------->|    Implementor    |
  +-------------+      +-----------------+                          +-------------------+
                       | Operation() o   |                          | OperationImp()    |
                       +-------+-----|---+                          +---------+---------+
                               ^     +---- imp->OperationImp();               ^
                       extends |                                              | implements
                       +-------+----------+                         +---------+---------+
                       |RefinedAbstraction|                         |                   |
                       +------------------+             +-----------+--------+ +--------+-----------+
                                                        |ConcreteImplementorA| |ConcreteImplementorB|
                                                        +--------------------+ +--------------------+
                                                        | OperationImp()     | | OperationImp()     |
                                                        +--------------------+ +--------------------+
```
*Diagram Description: The common structure of the Bridge pattern. The `Client` interacts solely with the `Abstraction`. The `Abstraction` maintains a reference (`imp`) to an `Implementor` and forwards requests to it.*

### Participants
* **Abstraction** (`Window`)
    * Defines the abstraction's interface.
    * Maintains a reference to an object of type `Implementor`.
* **RefinedAbstraction** (`IconWindow`)
    * Extends the interface defined by `Abstraction`.
* **Implementor** (`WindowImp`)
    * Defines the interface for implementation classes. This interface doesn't have to correspond exactly to `Abstraction`'s interface; in fact, the two interfaces can be quite different. Typically, the `Implementor` interface provides only primitive operations, and `Abstraction` defines higher-level operations based on these primitives.
* **ConcreteImplementor** (`XWindowImp`, `PMWindowImp`)
    * Implements the `Implementor` interface and defines its concrete implementation.

---

## Collaborations
* `Abstraction` forwards client requests to its `Implementor` object.

---

## Consequences (Trade-offs)
The Bridge pattern has the following consequences:
1. **Decoupling interface and implementation**. An implementation is not bound permanently to an interface. The implementation of an abstraction can be configured at run-time. It's even possible for an object to change its implementation at run-time. Decoupling `Abstraction` and `Implementor` also eliminates compile-time dependencies on the implementation. Furthermore, this decoupling encourages layering that can lead to a better-structured system. The high-level part of a system only has to know about `Abstraction` and `Implementor`.
2. **Improved extensibility**. You can extend the `Abstraction` and `Implementor` hierarchies independently.
3. **Hiding implementation details from clients**. You can shield clients from implementation details, like the sharing of implementor objects and the accompanying reference count mechanism (if any).

---

## Implementation Hints & Modern Java Context

1. **Only one Implementor:** In situations where there's only one implementation, creating an abstract `Implementor` class isn't necessary. This is a degenerate case of the Bridge pattern; there's a one-to-one relationship between `Abstraction` and `Implementor`. Nevertheless, this separation is still useful when a change in the implementation of a class must not affect its existing clients.
2. **Creating the right Implementor object:** How, when, and where do you decide which `Implementor` class to instantiate when there's more than one? 
    * If `Abstraction` knows about all `ConcreteImplementor` classes, then it can instantiate one of them in its constructor; it can decide between them based on parameters passed to its constructor.
    * Another approach is to choose a default implementation initially and change it later according to usage.
    * It's also possible to delegate the decision to another object altogether. We can introduce a factory object whose sole duty is to encapsulate platform-specifics. A benefit of this approach is that `Abstraction` is not coupled directly to any of the `Implementor` classes.

**☕ Java Best Practices:**
> * **Interfaces over Abstract Classes for Implementors:** In Java, the `Implementor` should almost always be an `interface`. This gives you maximum flexibility to use concrete implementors that may already extend other class hierarchies.
> * **Dependency Injection:** Modern Java applications heavily rely on frameworks like Spring or CDI. Instead of the `Abstraction` creating its `Implementor` or calling a Singleton Factory, the `Implementor` is typically injected into the `Abstraction` via its constructor.

---

## Known Uses (Modern Java)
* **JDBC API:** The `java.sql.Connection` acts as an Abstraction, while the database-specific drivers (like `com.mysql.cj.jdbc.ConnectionImpl`) act as Concrete Implementors. Your code works with the Abstraction without knowing the underlying implementation.
* **SLF4J (Simple Logging Facade for Java):** The `Logger` interface is the Abstraction, and the underlying logging frameworks (Logback, Log4j2) provide the Implementors.
* **AWT / Swing:** The Java Abstract Window Toolkit historically used a peer architecture, where cross-platform components (like `java.awt.Button`) acted as Abstractions that delegated rendering and event handling to platform-specific peers (`java.awt.peer.ButtonPeer`).

---

## Related Patterns
* An **Abstract Factory** can create and configure a particular Bridge.
* The **Adapter** pattern is geared toward making unrelated classes work together. It is usually applied to systems after they're designed. Bridge, on the other hand, is used up-front in a design to let abstractions and implementations vary independently.