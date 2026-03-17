# Singleton - Object Creational

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](SingletonDemonstration.java)**

## Intent & Core Problem
Ensure a class only has one instance, and provide a global point of access to it.

**☕ Java Note:**
> While the intent remains identical today, the Singleton pattern is often heavily scrutinized in modern Java development due to its tendency to introduce tight coupling and hinder unit testing (acting as a glorified global variable). Modern frameworks (like Spring) generally prefer to manage singletons via Dependency Injection (IoC containers) rather than hardcoding the Singleton structure into the class itself.

---

## Motivation & Real-World Analogy
It's important for some classes to have exactly one instance. Although a system might have many printers, there should be only one printer spooler. There should be only one file system and one window manager. An accounting system might be dedicated to serving one specific company.

How do we ensure that a class has only one instance and that it is easily accessible? A global variable makes an object accessible, but it doesn't prevent a developer from instantiating multiple objects. A better solution is to make the class itself responsible for keeping track of its sole instance. The class intercepts requests to create new objects, ensuring no other instance can be created, and provides a centralized way to access it.

**Real-World Analogy:** Think of the office of the President of a country. There can be at most one active President at any given time. Regardless of who is asking or where they are, the title "The President of the United States" provides a global point of access to the exact same individual.

---

## Applicability
Use the Singleton pattern when:
* There must be exactly one instance of a class, and it must be accessible to clients from a well-known access point.
* The sole instance should be extensible by subclassing, and clients should be able to use an extended instance without modifying their code.

---

## Structure & Participants



```text
    +-----------------------------+
    |         Singleton           |
    +-----------------------------+
    | - static uniqueInstance     |
    | - singletonData             |
    +-----------------------------+                      +-------------------------+
    | + static Instance() o-------|--------------------> | return uniqueInstance;  |
    | + SingletonOperation()      |                      +-------------------------+
    | + GetSingletonData()        |
    +-----------------------------+
```

☕ Java Note: 
>In Java, `Instance()` is typically named `getInstance()`. The `-` denotes private visibility (for the constructor and the static instance), preventing the `new` keyword from being used outside the class.

* **Singleton:** Defines an `Instance` operation that lets clients access its unique instance. It may be responsible for creating its own unique instance.

---

## Collaborations
Clients access a Singleton instance *solely* through its designated creation operation (e.g., `getInstance()`).

---

## Consequences (Trade-offs)
* **Controlled access to sole instance:** Because the Singleton class encapsulates its sole instance, it has strict control over how and when clients access it.
* **Reduced name space:** The Singleton pattern is an improvement over global variables. It avoids polluting the namespace with global variables that store sole instances.
* **Permits refinement of operations and representation:** The Singleton class may be subclassed, and it's easy to configure an application with an instance of this extended class at run-time.
* **Permits a variable number of instances:** The pattern makes it easy to change your mind and allow more than one instance of the Singleton class if requirements change (e.g., a connection pool).
* **More flexible than class operations:** Another way to package a singleton's functionality is to use static methods (class operations). However, static methods in Java cannot be overridden polymorphically, making subclassing and extension difficult.

**Drawbacks in Modern Context:**
* **Testing difficulties:** Singletons hide dependencies and carry global state across tests, making isolated unit testing challenging.
* **Concurrency issues:** Lazy initialization in a multithreaded Java environment requires careful synchronization (like double-checked locking) to prevent multiple instances from being created simultaneously.

---

## Implementation Hints & Modern Java Context
The original GoF book heavily discussed subclassing singletons and maintaining registries of singletons. While theoretically sound, modern Java offers specific idioms for Singleton implementation that address language-specific quirks like threading, reflection, and serialization.

**☕ Modern Java Context & Best Practices:**
1.  **Lazy Initialization with Double-Checked Locking:** If the Singleton is resource-intensive, and you want to delay its creation until it's needed, you must use the `volatile` keyword on the instance variable and synchronize the initialization block to ensure thread safety.
2.  **Initialization-on-Demand Holder Idiom (Bill Pugh Singleton):** Utilizes an inner static helper class to hold the instance. This leverages Java's classloader mechanism to guarantee thread safety and lazy initialization without the overhead of `synchronized` blocks.
3.  **Enum Singleton (The Joshua Bloch approach):** *Effective Java* dictates that a single-element `enum` type is the best way to implement a Singleton. It provides an ironclad guarantee against multiple instantiation, even in the face of sophisticated serialization or reflection attacks.

---

## Modern Java Architectural Uses
* **Java Core:** `java.lang.Runtime#getRuntime()` and `java.awt.Desktop#getDesktop()` are classic examples of singletons in the JDK.
* **Spring Framework:** By default, Spring manages all beans as "Singletons." However, these are *container-managed singletons* (one instance per Spring ApplicationContext) rather than strictly enforced classloader singletons. This gives the benefits of a single shared instance without the rigid drawbacks of the GoF pattern.
* **Logging Frameworks:** The root logger in frameworks like Logback or Log4j often operates conceptually as a singleton to centralize log writing.

---

## Related Patterns
* Many patterns can be implemented using the Singleton pattern.
* **Abstract Factory**, **Builder**, and **Prototype** can all be implemented as Singletons when only one instance of the creator/manager is needed.