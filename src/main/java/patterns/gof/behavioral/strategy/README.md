# Strategy - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](StrategyDemonstration.java)**

## Intent & Core Problem
Define a family of algorithms, encapsulate each one, and make them interchangeable. The Strategy pattern lets the algorithm vary independently from the clients that use it.

> **Also Known As:** Policy

**☕ Java Note:**
> In modern Java (8+), the Strategy pattern is the cornerstone of functional programming capabilities. By defining single-method interfaces (Functional Interfaces), you can pass behaviors dynamically using lambdas and method references. This drastically reduces the boilerplate of creating standalone concrete strategy classes for every algorithm variation.

---

## Motivation & Real-World Analogy
Consider an application that breaks a stream of text into lines. There are many algorithms for doing this (e.g., simple ragged-right, optimized paragraph filling like TeX, etc.). Hard-wiring all these algorithms directly into the classes that need them is problematic:
* **Bloat:** Clients become massive and difficult to maintain if they contain multiple line-breaking routines.
* **Waste:** Different algorithms are appropriate at different times; loading all of them into memory when only one is used is inefficient.
* **Rigidity:** It is difficult to add new algorithms or modify existing ones when they are tightly integrated into the client logic.

We can avoid these problems by defining classes that encapsulate different line-breaking algorithms. An algorithm encapsulated in this way is called a **Strategy**.

Suppose a `Composition` class is responsible for maintaining and updating the text linebreaks. Instead of implementing the algorithm itself, `Composition` delegates the work to a `Compositor` interface. Subclasses of `Compositor` implement different strategies.

```text
┌─────────────────┐ compositor                    ┌─────────────────┐
│   Composition   │<>────────────────────────────►│   Compositor    │
├─────────────────┤                               ├─────────────────┤
│ Traverse()      │                               │ Compose()       │
│ Repair() o──────┼─┐                             └─────────────────┘
└─────────────────┘ │                                      △
                    │                                      │
  ┌─────────────────┘                                      │
  │ compositor.Compose()                                   │
  └─────────────────────┘                                  │
                                                           │
                                 ┌─────────────────────────┼─────────────────────────┐
                                 │                         │                         │
                        ┌────────┴────────┐       ┌────────┴────────┐       ┌────────┴────────┐
                        │SimpleCompositor │       │  TeXCompositor  │       │ ArrayCompositor │
                        ├─────────────────┤       ├─────────────────┤       ├─────────────────┤
                        │ Compose()       │       │ Compose()       │       │ Compose()       │
                        └─────────────────┘       └─────────────────┘       └─────────────────┘
```
*Diagram Description: `Composition` (Context) maintains a reference to the `Compositor` (Strategy) interface. When `Composition.Repair()` is called, it delegates the algorithmic work to `compositor.Compose()`. The concrete strategies (`SimpleCompositor`, `TeXCompositor`, `ArrayCompositor`) define the specific line-breaking behaviors.*

**☕ Modern Java Note:**
> While the UML above shows class inheritance, modern Java often replaces the `ConcreteStrategy` classes with lambda expressions if the algorithm is concise enough, or injects them via frameworks like Spring (e.g., autowiring a `List<Compositor>` and selecting one at runtime).

---

## Applicability
Use the Strategy pattern when:
* Many related classes differ only in their behavior. Strategies provide a way to configure a class with one of many behaviors.
* You need different variants of an algorithm (e.g., algorithms optimizing for time vs. space).
* An algorithm uses data that clients shouldn't know about. Use the Strategy pattern to avoid exposing complex, algorithm-specific data structures.
* A class defines many behaviors, which appear as multiple conditional statements (`switch` or `if-else`) in its operations. Instead of conditionals, move related conditional branches into their own Strategy class.

---

## Structure & Participants



### Structure

```text
┌──────────────────────┐ strategy        ┌──────────────────────┐
│       Context        │<>──────────────►│      Strategy        │
├──────────────────────┤                 ├──────────────────────┤
│ ContextInterface() o │                 │ AlgorithmInterface() │
└────────────────────│─┘                 └──────────────────────┘
                     │                              △
   ┌─────────────────┘                              │
   │ strategy.AlgorithmInterface()                  │
   └──────────────────────────────┘                 │
                                                    │
                                                    │
                     ┌──────────────────────────────┼──────────────────────────────┐
                     │                              │                              │
            ┌────────┴─────────────┐       ┌────────┴─────────────┐       ┌────────┴─────────────┐
            │  ConcreteStrategyA   │       │  ConcreteStrategyB   │       │  ConcreteStrategyC   │
            ├──────────────────────┤       ├──────────────────────┤       ├──────────────────────┤
            │ AlgorithmInterface() │       │ AlgorithmInterface() │       │ AlgorithmInterface() │
            └──────────────────────┘       └──────────────────────┘       └──────────────────────┘
```
*Diagram Description: The `Context` relies on the `Strategy` abstraction to perform a task. It does not know the specific implementation. `ConcreteStrategyA`, `B`, and `C` provide interchangeable implementations of the `AlgorithmInterface()`.*

### Participants
* **Strategy** (`Compositor`): Declares an interface common to all supported algorithms. The Context uses this interface to call the algorithm defined by a ConcreteStrategy.
* **ConcreteStrategy** (`SimpleCompositor`, `TeXCompositor`, etc.): Implements the algorithm using the Strategy interface.
* **Context** (`Composition`):
    * Is configured with a ConcreteStrategy object.
    * Maintains a reference to a Strategy object.
    * May define an interface that lets Strategy access its data.

---

## Collaborations
* Strategy and Context interact to implement the chosen algorithm. A context may pass all required data to the strategy when the algorithm is called. Alternatively, the context can pass *itself* as an argument to Strategy operations, allowing the strategy to call back on the context as required.
* A context forwards requests from its clients to its strategy. Clients usually create and pass a ConcreteStrategy object to the context; thereafter, clients interact exclusively with the context.

---

## Consequences (Trade-offs)
The Strategy pattern has the following benefits and drawbacks:

1. **Families of related algorithms.** Hierarchies of Strategy classes define a family of algorithms or behaviors for contexts to reuse.
2. **An alternative to subclassing.** Subclassing a Context directly to change its behavior mixes the algorithm implementation with the Context's state, making it harder to understand and maintain. Strategies encapsulate the algorithm independently.
3. **Eliminates conditional statements.** Encapsulating behaviors in separate Strategy classes eliminates the need for large conditional statements in the Context.
4. **A choice of implementations.** Strategies provide different implementations of the same behavior (e.g., different space/time trade-offs).
5. **Clients must be aware of different Strategies.** The client must understand how Strategies differ before it can select the appropriate one. Use this pattern only when the variation in behavior is relevant to clients.
6. **Communication overhead.** The Strategy interface is shared by all ConcreteStrategies. Some strategies might not use all the information passed to them by the Context, meaning the Context might create and initialize parameters that go unused.
7. **Increased object count.** Strategies increase the total number of objects in an application.

---

## Implementation Hints & Modern Java Context
1. **Defining the Strategy and Context interfaces:** You must decide how data flows between Context and Strategy.
    * *Approach A:* Context passes data as parameters to the Strategy. This keeps them decoupled but might result in passing unused data.
    * *Approach B:* Context passes a reference to itself, and the Strategy requests data explicitly. This reduces parameter bloat but tightly couples the Strategy to the Context's interface.
2. **Strategies as optional:** The Context can implement a default behavior. A Strategy object is then required only if the client wishes to override that default.
3. **Modern Java Context:** In Java, passing a behavior has become trivial using functional interfaces. For simple strategies, defining a full hierarchy of classes is often unnecessary.

**☕ Java Context Example:**
```java
// Java 8+ Functional Strategy
public class PaymentContext {
    // Strategy is simply a built-in functional interface, or a custom one
    public void pay(int amount, Consumer<Integer> paymentStrategy) {
        paymentStrategy.accept(amount);
    }
}

// Client code using lambdas instead of ConcreteStrategy classes
PaymentContext context = new PaymentContext();
context.pay(100, amount -> System.out.println("Paid " + amount + " using Credit Card"));
context.pay(50, amount -> System.out.println("Paid " + amount + " using PayPal"));
```

---

## Known Uses & Java API Usage
* **Java Collections Framework:** `java.util.Comparator<T>` is the quintessential Strategy interface in Java. It allows clients to define sorting strategies completely independently of the sorting algorithm (`Collections.sort()`).
* **Java Concurrency:** `java.util.concurrent.RejectedExecutionHandler` defines a strategy for handling tasks that cannot be executed by a `ThreadPoolExecutor` (e.g., `AbortPolicy`, `CallerRunsPolicy`).
* **Spring Framework:** 
  * `Resource` loading strategies (file system, classpath, URL).
  * `PlatformTransactionManager` encapsulates various transaction management strategies (JDBC, Hibernate, JTA) behind a single interface.

---

## Related Patterns
* **Flyweight:** Strategy objects often make good flyweights if they contain no instance state and can be shared across multiple Contexts.
* **State:** Very similar structurally, but differ in intent. Strategies are externally configured and represent a single algorithm. State objects represent internal states, manage transitions, and frequently alter the Context's behavior completely.