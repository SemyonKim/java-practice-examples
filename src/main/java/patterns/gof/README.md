# Gang of Four (GoF) Design Patterns
This directory contains programmatic implementations of the design patterns described in the seminal book **"Design Patterns: Elements of Reusable Object-Oriented Software"** by Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides—collectively known as the **Gang of Four (GoF)**.

---

## 🎯 Purpose
The goal of this repository is to provide clear, concise, and well-documented examples of the 23 classic design patterns. These patterns serve as templates for solving common software engineering challenges, promoting code reusability, and establishing a common vocabulary for developers.

---

- [Introduction to Design Patterns](intro/ch1/README.md)
- [A Case Study: Designing a Document Editor (Lexi)](intro/ch2/README.md)

---

## 📂 Pattern Catalog
The patterns are categorized into three fundamental groups:

---

### 1. [Creational Patterns](creational/README.md)
Focus on the mechanisms of object creation.
- **[Singleton](creational/singleton/README.md)**: Ensures a class has only one instance.
- **[Factory Method](creational/factorymethod/README.md)**: Defines an interface for creating an object but lets subclasses decide which class to instantiate.
- **[Abstract Factory](creational/abstractfactory/README.md)**: Provides an interface for creating families of related objects.
- **[Builder](creational/builder/README.md)**: Separates the construction of a complex object from its representation.
- **[Prototype](creational/prototype/README.md)**: Creates new objects by copying an existing instance.

### 2. [Structural Patterns](structural/README.md)
Focus on how classes and objects are composed to form larger structures.
- **[Adapter](structural/adapter/README.md)**: Allows incompatible interfaces to work together.
- **[Bridge](structural/bridge/README.md)**: Decouples an abstraction from its implementation.
- **[Composite](structural/composite/README.md)**: Treats individual objects and compositions of objects uniformly.
- **[Decorator](structural/decorator/README.md)**: Dynamically adds responsibilities to an object.
- **[Facade](structural/facade/README.md)**: Provides a simplified interface to a complex subsystem.
- **[Flyweight](structural/flyweight/README.md)**: Uses sharing to support large numbers of fine-grained objects efficiently.
- **[Proxy](structural/proxy/README.md)**: Provides a surrogate or placeholder for another object.

### 3. Behavioral Patterns
Focus on communication between objects and the assignment of responsibilities.
- **Chain of Responsibility**: Passes a request along a chain of handlers.
- **Command**: Encapsulates a request as an object.
- **Interpreter**: Provides a way to evaluate language grammar or expressions.
- **Iterator**: Provides a way to access elements of an aggregate object sequentially.
- **Mediator**: Defines how a set of objects interact to reduce direct dependencies.
- **Memento**: Captures and restores an object's internal state.
- **Observer**: A way of notifying multiple objects about any events that happen to the object they’re observing.
- **State**: Allows an object to alter its behavior when its internal state changes.
- **Strategy**: Defines a family of algorithms and makes them interchangeable.
- **Template Method**: Defines the skeleton of an algorithm, deferring steps to subclasses.
- **Visitor**: Separates an algorithm from the object structure on which it operates.

---

## 🚀 How to Use
Each subfolder corresponds to a specific pattern. Inside, you will find:
- **Source Code**: The implementation of the pattern.
- **Explanation**: A brief comment or local markdown file explaining the "When" and "Why" of the pattern.
> "Design patterns are not a silver bullet; they are tools to help you communicate intent and structure your code more effectively."

--- 

## 📚 References

This documentation is a structured adaptation of the foundational concepts presented in:
> **Design Patterns: Elements of Reusable Object-Oriented Software**
> *Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides (The Gang of Four)*

### Documentation Notes:
* **Source Material:** The theoretical definitions, problem statements, and classic design motivations are derived from the original 1994 text.
* **Modern Adaptations:** The code implementations and architectural summaries have been updated for **Modern Java (JDK 21+)**, utilizing contemporary features such as:
    * **Sealed Interfaces & Classes** (JEP 409) for controlled hierarchies.
    * **Records** (JEP 395) for immutable data carriers.
    * **Pattern Matching for switch** (JEP 441) as a functional alternative to the Visitor pattern.
    * **Functional Interfaces & Lambdas** for Strategy and Observer implementations.
* **Purpose:** This repository serves as a personal reference guide and a bridge between classic GoF principles and modern software engineering practices.