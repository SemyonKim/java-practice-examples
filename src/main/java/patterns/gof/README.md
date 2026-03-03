# Gang of Four (GoF) Design Patterns
This directory contains programmatic implementations of the design patterns described in the seminal book **"Design Patterns: Elements of Reusable Object-Oriented Software"** by Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides—collectively known as the **Gang of Four (GoF)**.

---

## 🎯 Purpose
The goal of this repository is to provide clear, concise, and well-documented examples of the 23 classic design patterns. These patterns serve as templates for solving common software engineering challenges, promoting code reusability, and establishing a common vocabulary for developers.

---

## 📂 Pattern Catalog
The patterns are categorized into three fundamental groups:

---

### 1. Creational Patterns
Focus on the mechanisms of object creation.
- **Singleton**: Ensures a class has only one instance.
- **Factory Method**: Defines an interface for creating an object but lets subclasses decide which class to instantiate.
- **Abstract Factory**: Provides an interface for creating families of related objects.
- **Builder**: Separates the construction of a complex object from its representation.
- **Prototype**: Creates new objects by copying an existing instance.

### 2. Structural Patterns
Focus on how classes and objects are composed to form larger structures.
- **Adapter**: Allows incompatible interfaces to work together.
- **Bridge**: Decouples an abstraction from its implementation.
- **Composite**: Treats individual objects and compositions of objects uniformly.
- **Decorator**: Dynamically adds responsibilities to an object.
- **Facade**: Provides a simplified interface to a complex subsystem.
- **Flyweight**: Uses sharing to support large numbers of fine-grained objects efficiently.
- **Proxy**: Provides a surrogate or placeholder for another object.

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
- **Book:** Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley Professional.
- **Original Concepts:** This repository is for educational purposes, implementing the structural, creational, and behavioral patterns defined by the "Gang of Four."