# State - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](StateDemonstration.java)**

## Intent & Core Problem
The State pattern allows an object to alter its behavior when its internal state changes. To the client, the object will appear as though it has dynamically changed its class.

> **Also Known As:** Objects for States

**☕ Java Note:**
> In modern Java, the State pattern replaces cumbersome, heavily nested `switch` or `if-else` blocks with polymorphism. It treats state as a first-class object, aligning well with Open/Closed principles. This is often implemented using interfaces, abstract classes, or comprehensively via Java `enum` types that override abstract methods for state-specific behavior.

---

## Motivation & Real-World Analogy
Consider a `TCPConnection` class that represents a network connection. A connection object can exist in one of several different states: Established, Listening, or Closed. When this object receives requests from other components, it responds differently depending on its current state. For example, an "Open" request will have completely different effects depending on whether the connection is currently Closed or Established.

The State pattern describes how `TCPConnection` can exhibit different behavior across these states without relying on massive conditional statements. The core idea is to introduce an abstract class or interface called `TCPState` to represent the operational states of the network connection. This abstraction declares an interface common to all state representations. Subclasses of `TCPState` (such as `TCPEstablished` and `TCPClosed`) implement the behavior specific to their respective states.

```text
┌─────────────────┐ state                           ┌─────────────────┐
│ TCPConnection   │<>──────────────────────────────►│    TCPState     │
├─────────────────┤                                 ├─────────────────┤
│ Open() o────────┼─┐                               │ Open()          │
│ Close()         │ │                               │ Close()         │
│ Acknowledge()   │ │                               │ Acknowledge()   │
└─────────────────┘ │                               └─────────────────┘
                    │                                        △
 ┌──────────────────┴─┐                                      │
 │ state.Open()       │                                      │
 └────────────────────┘                                      │
                                     ┌───────────────────────┼───────────────────────┐
                                     │                       │                       │
                            ┌────────┴────────┐     ┌────────┴────────┐     ┌────────┴────────┐
                            │ TCPEstablished  │     │    TCPListen    │     │    TCPClosed    │
                            ├─────────────────┤     ├─────────────────┤     ├─────────────────┤
                            │ Open()          │     │ Open()          │     │ Open()          │
                            │ Close()         │     │ Close()         │     │ Close()         │
                            │ Acknowledge()   │     │ Acknowledge()   │     │ Acknowledge()   │
                            └─────────────────┘     └─────────────────┘     └─────────────────┘
```
*Diagram Description: `TCPConnection` acts as the context, aggregating a `TCPState` instance. `TCPConnection` delegates incoming methods like `Open()` to the current `state` object. `TCPState` is the abstract base, implemented by concrete states like `TCPEstablished`, `TCPListen`, and `TCPClosed`.*

The `TCPConnection` class maintains a state object representing the current status of the connection. It delegates all state-specific requests to this internal state object. Whenever the connection transitions from one state to another (e.g., from established to closed), the `TCPConnection` object replaces its current state object (e.g., `TCPEstablished`) with the new one (e.g., `TCPClosed`).

---

## Applicability
Use the State pattern in either of the following cases:
* An object's behavior depends entirely on its state, and it must change its behavior at run-time depending on that state.
* Operations contain large, multipart conditional statements that depend on the object's state. This state is usually represented by enumerated constants, and several operations will contain the exact same conditional structure. The State pattern extracts each branch of the conditional into a separate class, letting you treat the state as an independent object.

---

## Structure & Participants

### Structure

```text
┌─────────────────┐ state                                 ┌─────────────────┐
│     Context     │<>────────────────────────────────────►│      State      │
├─────────────────┤                                       ├─────────────────┤
│ Request()     o─┼─┐                                     │ Handle()        │
└─────────────────┘ │                                     └─────────────────┘
                    │                                              △
 ┌──────────────────┴─┐                                            │
 │ state.Handle()     │                                            │
 └────────────────────┘                                            │
                                                 ┌─────────────────┼──────────── ─ ─ ─
                                                 │                 │
                                        ┌────────┴────────┐ ┌──────┴──────────┐
                                        │ ConcreteStateA  │ │ ConcreteStateB  │
                                        ├─────────────────┤ ├─────────────────┤
                                        │ Handle()        │ │ Handle()        │
                                        └─────────────────┘ └─────────────────┘
```
*Diagram Description: The Context object holds a reference to a State interface. When `Context.Request()` is invoked, it delegates the work to `state.Handle()`. `ConcreteStateA` and `ConcreteStateB` inherit from `State` to provide specific implementations.*

### Participants
* **Context** (`TCPConnection`): Defines the interface of interest to clients and maintains an instance of a `ConcreteState` subclass that defines the current state.
* **State** (`TCPState`): Defines an interface for encapsulating the behavior associated with a particular state of the Context.
* **ConcreteState subclasses** (`TCPEstablished`, `TCPListen`, `TCPClosed`): Each subclass implements a behavior associated with a specific state of the Context.

---

## Collaborations
* The Context delegates state-specific requests to the current `ConcreteState` object.
* A context may pass itself as an argument to the State object handling the request, allowing the State object to access the context and initiate transitions if necessary.
* The Context acts as the primary interface for clients. Clients configure a context with an initial State object; once configured, clients generally do not interact with State objects directly.
* Either the Context or the `ConcreteState` subclasses can decide which state succeeds another and under what circumstances.

---

## Consequences (Trade-offs)
The State pattern has the following consequences:

1.  **It localizes state-specific behavior and partitions behavior for different states .** All state-specific code lives in a `State` subclass, allowing new states and transitions to be added easily. Using an alternative approach (like internal data values with explicit checks) results in conditional statements scattered throughout the Context's implementation, making maintenance difficult. While the State pattern avoids this, it distributes behavior across multiple classes, making the code less compact. However, this distribution is beneficial when states are numerous, as it prevents monolithic and hard-to-modify conditionals. Encapsulating state actions elevates the execution state to full object status, clarifying intent.
2.  **It makes state transitions explicit .** When an object defines its state via internal variables, transitions only show up as scattered variable assignments. State objects make transitions explicit and atomic—they happen by rebinding a single variable (the Context's State object reference), protecting the Context from inconsistent internal states.
3.  **State objects can be shared .** If State objects have no instance variables (i.e., state is entirely encoded in their type), contexts can share them as Flyweights.

---

## Implementation Hints & Modern Java Context
1.  **Who defines the state transitions?** The pattern does not mandate where transition criteria live. Fixed transitions can be implemented in the Context. However, letting `State` subclasses specify their successor is generally more flexible. This requires the Context to expose an interface for explicitly setting the current state. While decentralizing makes extension easier, it introduces implementation dependencies between `State` subclasses.
2.  **Table-based vs. Object-based.** An alternative approach maps inputs to state transitions using tables (converting conditionals into look-ups). Tables make transition criteria regular and modifiable as data. However, table look-ups can be less efficient, obscure transition logic, and complicate the execution of arbitrary actions alongside transitions. The State pattern models state-specific *behavior*, whereas tables focus purely on defining *transitions*.
3.  **Creating and destroying State objects.** You must choose whether to dynamically create State objects upon need or create them ahead of time. Dynamic creation is better if states are unknown at runtime, states change infrequently, or State objects store significant data. Pre-allocation is preferred for rapid state changes to avoid destruction/re-instantiation costs, though the Context must retain references to all potential states.
4.  **Dynamic Inheritance.** Languages that support dynamic inheritance (like Self) can implement this pattern natively by changing the delegation target at run-time. Java simulates this via object composition and delegation.

**☕ Java Context & Best Practices:**
> In modern Java, Java `enum` classes natively support behavior attached to specific constants, making them an excellent tool for the State pattern when states are known at compile-time and don't require instance-specific memory.
```java
// Modern Java Alternative: Enum-based State Pattern
public enum TCPState {
    ESTABLISHED {
        @Override
        public void close(TCPConnection context) {
            context.changeState(CLOSED);
        }
    },
    CLOSED {
        @Override
        public void open(TCPConnection context) {
            context.changeState(ESTABLISHED);
        }
    };
    
    // Default implementation or abstract method
    public void open(TCPConnection context) {}
    public void close(TCPConnection context) {}
}
```
> For modern applications using Java 21+, `sealed` interfaces combined with pattern matching in `switch` expressions offer a functional alternative to the classical Object-Oriented State pattern.

---

## Known Uses & Java API Usage
* **Drawing Editors:** Popular interactive drawing frameworks (like HotDraw and Unidraw) use the State pattern to manage drawing "tools". When a line tool is active, clicking creates shapes; when the selection tool is active, clicking selects shapes . The editor changes its behavior by delegating requests to the currently active tool object .
* **Java API Context:** 
  * `javax.faces.lifecycle.Lifecycle` in JavaServer Faces utilizes state representations for managing UI component request lifecycles.
  * **Spring State Machine:** A comprehensive framework built entirely around the State pattern, allowing developers to configure states, transitions, actions, and guards for complex enterprise applications.

---

## Related Patterns
* **Flyweight:** Explains when and how State objects without local instance state can be shared among multiple Contexts.
* **Singleton:** State objects are often implemented as Singletons to avoid constant instantiation, particularly when they maintain no local data.