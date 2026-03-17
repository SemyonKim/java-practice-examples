# Introduction to Structural Patterns

***[Back to the Pattern Catalog](../README.md)***

Structural patterns are fundamentally concerned with how classes and objects are composed to form larger, more complex structures. They help ensure that when system components are combined, the resulting architecture remains flexible, efficient, and resilient to change.

These patterns are broadly categorized into two approaches: **Class Composition** and **Object Composition**.


---

## 1. Structural Class Patterns (Inheritance)

Structural class patterns rely on inheritance to compose interfaces or implementations. By mixing two or more classes into one, the resulting subclass combines the properties of its parent classes. This approach is particularly useful for making independently developed class libraries work together seamlessly.

A classic example is the *Class* version of the **Adapter** pattern. An adapter's goal is to make one interface conform to another, providing a uniform abstraction across different interfaces. A class adapter achieves this by inheriting privately from an adaptee class and then expressing its own interface in terms of the adaptee's.

**☕ Modern Java Insight: The Limits of Class Inheritance**
> Java deliberately restricts classes to single inheritance (a class can only `extend` one parent class) to avoid the "Diamond Problem" prevalent in C++. Because of this, pure Structural Class Patterns are less common in modern Java. Instead, Java developers achieve interface composition by implementing multiple `interface` types.
> ```java
> // Java interface composition (Multiple Inheritance of Type)
> public class SmartDevice implements Networkable, Powerable {
>     // ...
> }
> ```

---

## 2. Structural Object Patterns (Composition)

Rather than hard-coding composition through class inheritance, structural object patterns describe ways to compose distinct objects to realize new functionality. The massive advantage of object composition is the ability to change the composition dynamically at run-time—something that is completely impossible with static class inheritance.

**☕ Modern Java Insight: Favor Composition Over Inheritance**
> "Favor object composition over class inheritance" is a core principle in modern Java development. It leads to looser coupling and easier testing (e.g., mocking dependencies). Java frameworks like Spring heavily rely on this, injecting dependencies (objects) into components at runtime rather than relying on deep class hierarchies.

---

## The Structural Patterns Landscape

Here is a brief overview of the key structural patterns and how they manipulate object structures:

### Adapter
* **Role:** Makes one interface conform to another.
* **Mechanism:** Wraps an incompatible object to provide a uniform abstraction.

### Composite
* **Role:** Builds a unified class hierarchy consisting of both primitive and composite objects.
* **Mechanism:** Allows you to compose these primitive and composite objects into arbitrarily complex, tree-like structures where clients treat all nodes uniformly.

### Proxy
* **Role:** Acts as a convenient surrogate, placeholder, or level of indirection for another object.
* **Mechanism:** Can act as a local representative for a remote object , lazily load a massive object on demand, or restrict access to a sensitive object. It enhances or alters object properties transparently.

### Flyweight
* **Role:** Defines a structure for sharing lots of little objects to improve space efficiency and consistency.
* **Mechanism:** Substantial memory savings are achieved by sharing objects instead of replicating them. Crucially, these shared objects must *not* define any context-dependent state. Any required context is passed into the Flyweight when needed, allowing it to be shared freely across the application.

### Facade
* **Role:** While Flyweight manages many tiny objects, Facade provides a single, simplified object to represent an entire complex subsystem.
* **Mechanism:** The facade carries out its responsibilities simply by forwarding client messages to the appropriate, hidden objects within the subsystem.

### Bridge
* **Role:** Separates an object's core abstraction from its physical implementation.
* **Mechanism:** By uncoupling the two, the abstraction and the implementation can be varied and extended completely independently of one another.

### Decorator
* **Role:** Adds new responsibilities to objects dynamically.
* **Mechanism:** Composes objects recursively, allowing for an open-ended number of enhancements. For example, a UI component can be decorated with a border, a shadow, or scrolling capabilities simply by nesting Decorator objects.
* **Rules:** The Decorator must conform strictly to the component's interface. It forwards messages to the component, doing its own extra job either right before or right after forwarding the request.

**☕ Modern Java Insight: Decorator via Lambdas**
> In modern Java (Java 8+), the Decorator pattern is often implemented fluidly using `java.util.function.Function` composition.
> ```java
> Function<String, String> baseText = text -> text;
> Function<String, String> addBorder = text -> "[ " + text + " ]";
> Function<String, String> addShadow = text -> text + " *shadow*";
> 
> // Recursively composing the object's responsibilities at runtime
> Function<String, String> decorated = baseText.andThen(addBorder).andThen(addShadow);
> System.out.println(decorated.apply("Hello")); // Output: [ Hello ] *shadow*
> ```

---

*Many of these structural patterns are related and share similar underlying mechanisms (like object forwarding), but they are distinguished by their specific intent and the architectural problems they solve.*

---

# Discussion of Structural Patterns

You may notice significant similarities in the participants and collaborations across various structural patterns. This overlap occurs because these patterns rely on the same fundamental object-oriented mechanisms for structuring code: single inheritance for class-based patterns and object composition for object-based patterns. However, these structural similarities mask fundamentally different design intents.

Comparing these groups provides a clearer understanding of their relative merits and distinct use cases.

### Adapter vs. Bridge
Both Adapter and Bridge promote flexibility by introducing a level of indirection, forwarding requests to another object using an interface different from the object's own. The primary distinction lies in their core intent:

* **Adapter:** Focuses entirely on resolving incompatibilities between existing interfaces. It allows independently designed classes to work together without requiring code modification or reimplementation. It does not concern itself with how those interfaces are implemented or how they might independently evolve.
* **Bridge:** Bridges an abstraction with its potentially numerous implementations. It provides a stable interface to clients while allowing you to vary and extend the underlying implementation classes as the system evolves.

Consequently, these patterns are utilized at different stages of the software lifecycle:
* **Adapter** is applied *after* a system is designed, typically when an unforeseen coupling makes adapting an incompatible class necessary to avoid code duplication.
* **Bridge** is applied *before* a system is designed, when the architect understands up-front that an abstraction will require multiple implementations that must evolve independently.

Neither pattern is inherently superior; they simply address different architectural problems.

> **☕ Modern Java Note:** 
> It is easy to confuse a **Facade** with an **Adapter**, thinking of a facade as an adapter for a collection of objects. However, an Adapter reuses an *existing* interface to make things work together, whereas a Facade defines an entirely *new*, simplified interface. In modern Java, Facades are often implemented as stateless Spring `@Service` beans that orchestrate complex domain logic, whereas Adapters are often specific wrapper classes implementing functional interfaces.

### Composite vs. Decorator vs. Proxy

#### Composite vs. Decorator
Composite and Decorator share almost identical structure diagrams because both utilize recursive composition to manage an open-ended number of objects.  However, treating a decorator as merely a degenerate composite misses the pattern's entire point. Their similarity ends at recursive composition due to their differing intents:

* **Decorator:** Designed to dynamically add responsibilities to objects without subclassing. It prevents the static subclass explosion that occurs when trying to account for every combination of features statically. Its primary focus is on *embellishment*.
* **Composite:** Designed to structure classes so that multiple related objects can be treated uniformly as a single entity. Its primary focus is on *representation*.

Because their intents are distinct but complementary, they are frequently used in concert. This combination allows developers to build complex applications simply by plugging objects together, without defining new classes. In such systems:
* Composites and decorators share a common interface.
* From the Composite's perspective, a decorator acts as a `Leaf`.
* From the Decorator's perspective, a composite acts as a `ConcreteComponent`.

#### Proxy vs. Decorator
Another pattern structurally similar to Decorator is Proxy. Both compose an object, provide a level of indirection, keep a reference to a target object, forward requests to it, and present an identical interface to the client.
* **Proxy:** Acts as a stand-in for a subject when direct access is inconvenient or undesirable (e.g., the object is on a remote machine, is persistent, or requires restricted access). The subject defines the core functionality, and the proxy simply controls access to it. This relationship is typically static and is not concerned with attaching properties dynamically or recursive composition.
* **Decorator:** Addresses scenarios where an object's total functionality cannot be conveniently determined at compile time. The core component provides only partial functionality, and one or more decorators furnish the rest. This open-endedness makes recursive composition essential.

> **☕ Modern Java Note:** 
> While these patterns solve specific recurring problems, modern Java frameworks often blur the lines by combining them. For instance, Spring Data JPA utilizes a hybrid approach: it uses a **Proxy** to intercept database calls (lazily loading persistent objects) while simultaneously acting as a **Decorator** by dynamically adding transactional behavior (`@Transactional`) to the execution flow. While these hybrids exist, analyzing them through their fundamental GoF pattern components remains the best way to understand their architecture.