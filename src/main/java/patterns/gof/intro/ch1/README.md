# Introduction to Design Patterns

[A Case Study: Designing a Document Editor (Lexi)](../ch2/README.md)  
[Pattern catalog](../../README.md)

---

## What Is a Design Pattern?

Designing reusable object-oriented software is difficult. It requires finding pertinent objects, factoring them into classes at the right granularity, defining interfaces, and establishing key relationships. Experienced designers reuse solutions that have worked in the past rather than solving every problem from first principles. These recurring patterns of communicating objects solve specific design problems, making designs more flexible, elegant, and reusable.

A pattern has four essential elements:

1. **The Pattern Name:** A handle used to describe a design problem, its solutions, and its consequences. It increases design vocabulary and allows designing at a higher level of abstraction.
2. **The Problem:** Describes when to apply the pattern and explains its context.
3. **The Solution:** Describes the elements that make up the design, their relationships, responsibilities, and collaborations. It is an abstract description or template, not a concrete implementation.
4. **The Consequences:** The results and trade-offs (often space, time, flexibility, and extensibility) of applying the pattern.

---

## Design Patterns in Smalltalk MVC

The Model/View/Controller (MVC) architecture demonstrates multiple patterns working together:
- **Observer:** MVC decouples views and models via a subscribe/notify protocol. When the model changes, it notifies dependent views, allowing multiple varying presentations.
- **Composite:** Views can be nested (e.g., a control panel containing buttons). A composite view acts like a primitive view, letting clients treat groups of objects like individual objects.
- **Strategy:** A view delegates its response mechanism to a Controller object. By swapping controllers, the view changes its response strategy (e.g., ignoring input by attaching a disabled controller).

---

## Organizing Design Patterns

Patterns are classified by two criteria:
1. **Purpose:** What a pattern does (Creational, Structural, Behavioral).
2. **Scope:** Whether the pattern applies primarily to Classes (static relationships via inheritance) or Objects (dynamic relationships via composition at run-time).

| Scope      | Creational                                               | Structural                                                                                 | Behavioral                                                                                                                |
|:-----------|:---------------------------------------------------------|:-------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------|
| **Class**  | Factory Method                                           | Adapter (class)                                                                            | Interpreter <br>Template Method                                                                                           |
| **Object** | Abstract Factory <br>Builder <br>Prototype <br>Singleton | Adapter (object) <br>Bridge <br>Composite <br>Decorator <br>Facade <br>Flyweight <br>Proxy | Chain of Responsibility <br>Command <br>Iterator <br>Mediator <br>Memento <br>Observer <br>State <br>Strategy <br>Visitor |

---

## How Design Patterns Solve Design Problems

Patterns solve day-to-day object-oriented problems in several ways:
- **Specifying Object Interfaces:** Patterns help define signatures and interfaces. They rely on polymorphism (dynamic binding), letting a client make few assumptions about an object other than it supports a specific interface.
- **Class vs. Interface Inheritance:** Class inheritance defines implementation via another object (code sharing), whereas interface inheritance defines when an object can be used in place of another (subtyping).
- **Principle 1:** Program to an interface, not an implementation. Do not declare variables as instances of concrete classes.
- **Inheritance vs. Composition:** Inheritance ("white-box" reuse) is static and breaks encapsulation because it exposes parent details to subclasses. Object composition ("black-box" reuse) happens dynamically at run-time and keeps classes encapsulated and focused.
- **Principle 2:** Favor object composition over class inheritance.
- **Delegation:** Two objects handle a request; a receiving object delegates operations to its delegate. It makes it easy to compose behaviors at run-time.

Here are some common causes of redesign along with the design pattern(s) that address them:
1. **Creating an object by specifying a class explicitly.** Specifying a class name when you create an object commits you to a particular implementation instead of a particular interface. This commitment can complicate future changes. To avoid it, create objects indirectly.  
    ***Design patterns:*** ```Abstract Factory, Factory Method, Prototype```.
2. **Dependence on specific operations.** When you specify a particular operation, you commit to one way of satisfying a request. By avoiding hard-coded requests, you make it easier to change the way a request gets satisfied both at compile-time and at run-time.  
***Design patterns:*** ```Chain of Responsibility, Command```.
3. **Dependence on hardware and software platform.** External operating system interfaces and application programming interfaces (APIs) are different on different hardware and software platforms. Software that depends on a particular platform will be harder to port to other platforms. It may even be difficult to keep it up to date on its native platform. It's important therefore to design your system to limit its platform dependencies.  
   ***Design patterns:*** ```Abstract Factory, Bridge```.
4. **Dependence on object representations or implementations.** Clients that know how an object is represented, stored, located, or implemented might need to be changed when the object changes. Hiding this information from clients keeps changes from cascading.  
   ***Design patterns:*** ```Abstract Factory, Bridge, Memento, Proxy```.
5. **Algorithmic dependencies.** Algorithms are often extended, optimized, and replaced during development and reuse. Objects that depend on an algorithm will have to change when the algorithm changes. Therefore, algorithms that are likely to change should be isolated.  
   ***Design patterns:*** ```Builder, Iterator, Strategy, Template Method, Visitor```.
6. **Tight coupling.** Classes that are tightly coupled are hard to reuse in isolation, since they depend on each other. Tight coupling leads to monolithic systems, where you can't change or remove a class without understanding and changing many other classes. The system becomes a dense mass that's hard to learn, port, and maintain. Loose coupling increases the probability that a class can be reused by itself and that a system can be learned, ported, modified, and extended more easily. Design patterns use techniques such as abstract coupling and layering to promote loosely coupled systems.  
   ***Design patterns:*** ```Abstract Factory, Bridge, Chain of Responsibility, Command, Facade, Mediator, Observer```.
7. **Extending functionality by subclassing.** Customizing an object by subclassing often isn't easy. Every new class has a fixed implementation overhead (initialization, finalization, etc.). Defining a subclass also requires an in-depth understanding of the parent class. For example, overriding one operation might require overriding another. An overridden operation might be required to call an inherited operation. And subclassing can lead to an explosion of classes, because you might have to introduce many new subclasses for even a simple extension. Object composition in general and delegation in particular provide flexible alternatives to inheritance for combining behavior. New functionality can be added to an application by composing existing objects in new ways rather than by defining new subclasses of existing classes. On the other hand, heavy use of object composition can make designs harder to understand. Many design patterns produce designs in which you can introduce customized functionality just by defining one subclass and composing its instances with existing ones.  
   ***Design patterns:*** ```Bridge, Chain of Responsibility, Composite, Decorator, Observer, Strategy```.
8. **Inability to alter classes conveniently.** Sometimes you have to modify a class that can't be modified conveniently. Perhaps you need the source code and don't have it (as may be the case with a commercial class library). Or maybe any change would require modifying lots of existing subclasses. Design patterns offer ways to modify classes in such circumstances.  
   ***Design patterns:*** ```Adapter, Decorator, Visitor```.

---

## Selecting and Using a Design Pattern

To select a pattern, consider the causes of redesign (e.g., tight coupling, algorithmic dependencies) or consider what aspect of your system you want to vary independently. Use patterns cautiously; do not apply them indiscriminately, as they often introduce indirection that can complicate designs or impact performance.

| Purpose        | Design Pattern          | Aspect(s) That Can Vary                                                      |
|:---------------|:------------------------|:-----------------------------------------------------------------------------|
| **Creational** | Abstract Factory        | Families of product objects                                                  |
|                | Builder                 | How a composite object gets created                                          |
|                | Factory Method          | Subclass of object that is instantiated                                      |
|                | Prototype               | Class of object that is instantiated                                         |
|                | Singleton               | The sole instance of a class                                                 |
| **Structural** | Adapter                 | Interface to an object                                                       |
|                | Bridge                  | Implementation of an object                                                  |
|                | Composite               | Structure and composition of an object                                       |
|                | Decorator               | Responsibilities of an object without subclassing                            |
|                | Facade                  | Interface to a subsystem                                                     |
|                | Flyweight               | Storage costs of objects                                                     |
|                | Proxy                   | How an object is accessed; its location                                      |
| **Behavioral** | Chain of Responsibility | Object that can fulfill a request                                            |
|                | Command                 | When and how a request is fulfilled                                          |
|                | Interpreter             | Grammar and interpretation of a language                                     |
|                | Iterator                | How an aggregate's elements are accessed, traversed                          |
|                | Mediator                | How and which objects interact with each other                               |
|                | Memento                 | What private information is stored outside an object, and when               |
|                | Observer                | Number of objects that depend on another object; how they stay up to date    |
|                | State                   | States of an object                                                          |
|                | Strategy                | An algorithm                                                                 |
|                | Template Method         | Steps of an algorithm                                                        |
|                | Visitor                 | Operations that can be applied to object(s) without changing their class(es) |

---

---

## 📚 References & Acknowledgments

This documentation is a structured adaptation of the foundational concepts presented in:
> **Design Patterns: Elements of Reusable Object-Oriented Software** 
> *Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides (The Gang of Four)*

### Documentation Notes:
- **Source Material:** The theoretical definitions, problem statements, and classic design motivations are derived from the original 1994 text.
- **Purpose:** This repository serves as a personal reference guide and a bridge between classic GoF principles and modern software engineering practices.