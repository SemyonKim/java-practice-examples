# Behavioral Patterns

***[Back to the Pattern Catalog](../README.md)***

Behavioral patterns focus on algorithms and the assignment of responsibilities between objects. They detail not only patterns of objects and classes but also the patterns of communication between them. By characterizing complex, hard-to-follow run-time control flows , these patterns shift focus away from the flow of control, allowing developers to concentrate on how objects are interconnected.

## Behavioral Class Patterns
Class patterns use inheritance to distribute behavior between classes.

* **Template Method:** The simpler and more common class pattern. It provides an abstract definition of an algorithm, defining it step by step. Each step invokes either an abstract or a primitive operation, leaving subclasses to flesh out the algorithm by defining the abstract operations.
* **Interpreter:** Represents a grammar as a class hierarchy and implements an interpreter as an operation on instances of these classes.

> **☕ Java Insight:** Template Method is ubiquitous in Java frameworks. For instance, `AbstractList` in the JDK uses it heavily, defining core operations while leaving methods like `get(int)` for subclasses to implement.

## Behavioral Object Patterns
Object patterns utilize object composition instead of inheritance. They describe how groups of peer objects cooperate to perform tasks no single object could accomplish alone.

A critical issue is how peer objects discover and know about each other. Maintaining explicit references between peers increases coupling , which, in extreme cases, leads to every object knowing about every other object.

* **Mediator:** Avoids tight coupling by introducing a mediator object between peers. It provides necessary indirection for loose coupling.
* **Chain of Responsibility:** Offers even looser coupling by letting you send requests implicitly through a chain of candidate objects. Candidates may fulfill the request based on run-time conditions. The number of candidates is open-ended, and participants can be selected dynamically at run-time.
* **Observer:** Defines and maintains dependencies between objects. A classic example is the Smalltalk Model/View/Controller (MVC), where views are notified whenever the model's state changes.
* **Strategy:** Encapsulates an algorithm within an object, making it easy to specify and swap the algorithm an object uses.
* **Command:** Encapsulates a request as an object, allowing it to be passed as a parameter, stored in history, or otherwise manipulated.
* **State:** Encapsulates an object's states, allowing the object to alter its behavior when its internal state changes.
* **Visitor & Iterator:** Visitor encapsulates behavior that would normally be scattered across classes, while Iterator abstracts how elements in an aggregate are accessed and traversed.

---

# Discussion of Behavioral Patterns

## Encapsulating Variation
Encapsulating variation is a recurring theme. When a program aspect changes frequently, behavioral patterns define an object to encapsulate it. Other parts of the program collaborate with this new object when depending on that aspect. These patterns typically define an abstract class describing the encapsulating object, often deriving their name from it.

Examples include:
* A **Strategy** object encapsulates an algorithm.
* A **State** object encapsulates state-dependent behavior.
* A **Mediator** object encapsulates object protocols.
* An **Iterator** object encapsulates aggregate traversal and access.

These patterns target program aspects likely to change. They generally feature two kinds of objects: new objects encapsulating the varying aspect, and existing objects using them. Without the pattern, this functionality would be integrated directly into the existing objects. For example, Strategy or State logic would normally be wired directly into their Context classes.

However, not all patterns partition functionality this way. Chain of Responsibility deals with an arbitrary, open-ended number of existing objects, avoiding static communication relationships between classes.

## Objects as Arguments
Several patterns introduce objects primarily used as arguments.
* **Visitor:** Passed as an argument to a polymorphic `Accept` operation on the objects it visits. It is never considered part of those objects, preventing distributed logic.
* **Command & Memento:** Act as magic tokens passed around for later invocation. Command tokens represent requests , while Memento tokens represent internal states at specific times. Both can have complex internal representations hidden from clients.

Command heavily relies on polymorphism for executing the request. Conversely, Memento has a narrow interface, is passed purely as a value, and presents no polymorphic operations to clients.

> **☕ Java Best Practice:** Modern Java Records are ideal for Memento implementations due to their immutability and concise syntax. For Command, Java 8 Functional Interfaces (like `Runnable` or `Consumer`) eliminate the need for verbose class hierarchies.

```java
// Java Record as a lightweight Memento
public record EditorMemento(String content, int cursorPosition) {}
```

## Should Communication be Encapsulated or Distributed?
Mediator and Observer are competing patterns. Observer distributes communication across Observer and Subject objects, which cooperate to maintain constraints. Communication relies on interconnections: one subject to many observers, or chained observers/subjects.

Mediator centralizes communication, placing constraint responsibility squarely in the mediator. While creating reusable Observers and Subjects is generally easier than creating reusable Mediators , and Observer promotes loose coupling leading to finer-grained, reusable classes , Mediator makes the communication flow much easier to understand.

In Observer systems, tracing connections created dynamically is difficult, though recognizing the pattern helps. Smalltalk's ability to parameterize Observers makes it highly attractive there compared to C++.

> **☕ Java Insight:** In modern Java, parameterizing Observers is trivial using Lambda expressions and Method References. Frameworks like Spring leverage this extensively (e.g., ApplicationEvents), making the distributed Observer approach heavily favored over monolithic Mediators.

## Decoupling Senders and Receivers
Direct references between collaborating objects create dependencies that harm layering and reusability. Command, Observer, Mediator, and Chain of Responsibility decouple senders and receivers with different trade-offs.

### The Command Pattern
Command decouples by defining the binding between sender and receiver in a separate Command object.

*The Command Pattern Structure:*
```text
  anInvoker              aCommand              aReceiver
   (sender)                                    (receiver)
      |                     |                      |
      |---- Execute() ----->|                      |
      |                     |----- Action() ------>|
      |                     |                      |
```
*Description: An invoker sends an `Execute()` request to a Command object, which in turn triggers the specific `Action()` on the target Receiver.*

This single `Execute` interface keeps senders decoupled, making them reusable. Receivers can be parameterized with different senders. While nominally requiring a subclass for each connection, implementation techniques can avoid this  (e.g., using Java lambdas).

### The Observer Pattern
Observer defines an interface for signaling changes, decoupling subjects (senders) from observers (receivers).

*The Observer Pattern Structure:*
```text
   aSubject         anObserver         anObserver         anObserver
   (sender)         (receiver)         (receiver)         (receiver)
      |                  |                  |                  |
      |--- Update() ---->|                  |                  |
      |                  |                  |                  |
      |--- Update() ----------------------->|                  |
      |                  |                  |                  |
      |--- Update() ------------------------------------------>|
      |                  |                  |                  |
```
*Description: A Subject sequentially broadcasts `Update()` messages to multiple registered Observers.*

It creates a looser binding than Command because a subject can have a varying number of multiple observers at run-time. The interfaces are designed for communicating changes, making it ideal for data dependencies.

### The Mediator Pattern
Mediator decouples objects by forcing indirect communication through a central Mediator object.

*The Mediator Pattern Structure:*
```text
  aColleague            aMediator            aColleague           aColleague
(sender/receiver)                         (sender/receiver)    (sender/receiver)
      |                     |                     |                    |
      |-------------------->|                     |                    |
      |                     |-------------------->|                    |
      |                     |<--------------------|                    |
      |<--------------------|                     |                    |
      |                     |                     |                    |
      |                     |<-----------------------------------------|
      |                     |                     |                    |
      |                     |----------------------------------------->|
      |<--------------------|                     |                    |
      |                     |                     |                    |
```
*Description: Colleagues route all their messages through a central Mediator, which is responsible for dispatching the communication back out to other targeted Colleagues.*

Colleagues talk exclusively through the fixed Mediator interface, which routes requests and centralizes communication. To add flexibility, the Mediator often needs a custom dispatching scheme with encoded requests and packed arguments. This reduces system subclassing but often decreases type safety due to ad hoc dispatching.

### The Chain of Responsibility Pattern
This pattern decouples senders and receivers by passing requests along a chain of potential receivers.

*The Chain of Responsibility Pattern Structure:*
```text
   aClient              aHandler             aHandler             aHandler
   (sender)            (receiver)           (receiver)           (receiver)
      |                    |                    |                    |
      |-- HandleHelp() --->|                    |                    |
      |                    |-- HandleHelp() --->|                    |
      |                    |                    |-- HandleHelp() --->|
      |                    |                    |                    |
```
*Description: A client initiates a request (`HandleHelp()`) which is passed sequentially down a chain of Handlers until one processes it.*

Like Mediator, its fixed interface requires custom dispatching and shares similar type-safety drawbacks. It is highly effective if the chain is already part of the system structure and multiple objects might handle the request, offering flexibility to easily change or extend the chain.

> **☕ Java Note:** Chain of Responsibility is heavily utilized in modern Java web servers. The `javax.servlet.Filter` and `FilterChain` APIs are classic implementations, intercepting and processing HTTP requests sequentially.

## Summary
Behavioral design patterns mostly complement and reinforce each other.
* A Chain of Responsibility class often applies Template Method to determine if it should handle or forward a request using primitive operations.
* The chain can use Command to represent requests.
* Interpreter can use State for parsing contexts.
* Iterator traverses aggregates, while Visitor applies operations to elements.
* With the Composite pattern, Visitor can perform operations on components , Chain of Responsibility can access global properties via parents , and Decorator can override properties.
* Observer can tie object structures together, and State allows components to change behavior.
* Builder can construct compositions, treated later as Prototypes.

Well-designed object-oriented systems naturally embed multiple patterns. Composing at the *pattern* level, rather than just the class/object level, helps achieve synergy with greater ease.