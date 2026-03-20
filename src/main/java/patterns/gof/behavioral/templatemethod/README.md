# Template Method - Class Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](TemplateMethodDemonstration.java)**

## Intent & Core Problem
Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.

**☕ Java Note:**
> In modern Java, the Template Method pattern is frequently implemented not just through abstract classes, but also via interface `default` methods. This allows defining an algorithm skeleton directly inside an interface, relying on implementing classes to provide the concrete step definitions. To ensure the skeleton itself cannot be overridden in a class, Java uses the `final` keyword on the template method.

---

## Motivation & Real-World Analogy
Consider an application framework that provides `Application` and `Document` classes. The `Application` class is responsible for opening existing documents stored in an external format, such as a file. A `Document` object represents the information in a document once it's read from the file.

Applications built with the framework can subclass `Application` and `Document` to suit specific needs. For example, a drawing application defines `DrawApplication` and `DrawDocument` subclasses; a spreadsheet application defines `SpreadsheetApplication` and `SpreadsheetDocument` subclasses.

```text
┌───────────────┐         docs ┌───────────────┐
│   Document    │◄───────────<>│  Application  │
├───────────────┤              ├───────────────┤
│ Save()        │              │ AddDocument() │
│ Open()        │              │ OpenDocument()│
│ Close()       │              │ DoCreateDoc() │
│ DoRead()      │              │ CanOpenDoc()  │
└───────────────┘              │ AboutToOpen() │
        ^                      └───────────────┘
        │                              ^
┌───────┴───────┐              ┌───────┴───────┐
│  MyDocument   │◄- - - - - - -│ MyApplication │
├───────────────┤              ├───────────────┤     ┌────────────────────────┐
│ DoRead()      │              │ DoCreateDoc() o- - -┤ return new MyDocument; │
└───────────────┘              │ CanOpenDoc()  │     └────────────────────────┘
                               │ AboutToOpen() │
                               └───────────────┘
```
*Diagram Description: The abstract `Application` class manages `Document` objects. `MyApplication` creates instances of `MyDocument`. The template method `OpenDocument()` resides in `Application` and delegates creation and reading to its subclasses.*

**☕ Modern Java Supplement:**
```java
// Modern Java representation of the structural diagram
abstract class Application {
    private List<Document> docs = new ArrayList<>();

    // The Template Method
    public final void openDocument(String name) {
        if (!canOpenDocument(name)) return;
        Document doc = doCreateDocument();
        if (doc != null) {
            docs.add(doc);
            aboutToOpenDocument(doc);
            doc.open();
            doc.doRead();
        }
    }
    
    // Primitive operations to be implemented by subclasses
    protected abstract Document doCreateDocument();
    protected abstract boolean canOpenDocument(String name);
    
    // Hook operation (optional override)
    protected void aboutToOpenDocument(Document doc) {}
}
```

The abstract `Application` class defines the algorithm for opening and reading a document in its `OpenDocument` operation. `OpenDocument` defines each step for opening a document. It checks if the document can be opened, creates the application-specific `Document` object, adds it to its set of documents, and reads the `Document` from a file.

We call `OpenDocument` a template method. A template method defines an algorithm in terms of abstract operations that subclasses override to provide concrete behavior. `Application` subclasses define the steps of the algorithm that check if the document can be opened (`CanOpenDocument`) and that create the `Document` (`DoCreateDocument`). `Document` classes define the step that reads the document (`DoRead`).

The template method also defines an operation that lets `Application` subclasses know when the document is about to be opened (`AboutToOpenDocument`), in case they care. By defining some of the steps of an algorithm using abstract operations, the template method fixes their ordering, but it lets `Application` and `Document` subclasses vary those steps to suit their needs.

---

## Applicability
The Template Method pattern should be used:
* to implement the invariant parts of an algorithm once and leave it up to subclasses to implement the behavior that can vary.
* when common behavior among subclasses should be factored and localized in a common class to avoid code duplication. This is a good example of "refactoring to generalize" as described by Opdyke and Johnson. You first identify the differences in the existing code and then separate the differences into new operations. Finally, you replace the differing code with a template method that calls one of these new operations.
* to control subclasses extensions. You can define a template method that calls "hook" operations at specific points, thereby permitting extensions only at those points.

---

## Structure & Participants

### Structure

```text
┌───────────────────────┐               ┌───────────────────────────┐
│     AbstractClass     │               │ ...                       │
├───────────────────────┤               │ PrimitiveOperation1()     │
│ TemplateMethod()  o- -│- - - - - - - -┤ ...                       │
│ PrimitiveOperation1() │               │ PrimitiveOperation2()     │
│ PrimitiveOperation1() │               │ ...                       │
└───────────────────────┘               └───────────────────────────┘
            ^
┌───────────┴───────────┐
│    ConcreteClass      │
├───────────────────────┤
│ PrimitiveOperation1() │
│ PrimitiveOperation1() │
└───────────────────────┘
```
*Diagram Description: `AbstractClass` defines the `TemplateMethod()`, which internally calls `PrimitiveOperation1()` and `PrimitiveOperation2()`. `ConcreteClass` inherits from `AbstractClass` and implements these primitive operations.*

### Participants
* **AbstractClass** (`Application`): Defines abstract primitive operations that concrete subclasses define to implement steps of an algorithm. It implements a template method defining the skeleton of an algorithm. The template method calls primitive operations as well as operations defined in `AbstractClass` or those of other objects.
* **ConcreteClass** (`MyApplication`): Implements the primitive operations to carry out subclass-specific steps of the algorithm.

---

## Collaborations
* `ConcreteClass` relies on `AbstractClass` to implement the invariant steps of the algorithm.

---

## Consequences (Trade-offs)
Template methods are a fundamental technique for code reuse. They are particularly important in class libraries, because they are the means for factoring out common behavior in library classes.

Template methods lead to an inverted control structure that's sometimes referred to as "the Hollywood principle," that is, "Don't call us, we'll call you". This refers to how a parent class calls the operations of a subclass and not the other way around.

Template methods call the following kinds of operations:
* concrete operations (either on the `ConcreteClass` or on client classes);
* concrete `AbstractClass` operations (i.e., operations that are generally useful to subclasses);
* primitive operations (i.e., abstract operations);
* factory methods (see Factory Method); and
* hook operations, which provide default behavior that subclasses can extend if necessary. A hook operation often does nothing by default.

It's important for template methods to specify which operations are hooks (may be overridden) and which are abstract operations (must be overridden). To reuse an abstract class effectively, subclass writers must understand which operations are designed for overriding.

A subclass can extend a parent class operation's behavior by overriding the operation and calling the parent operation explicitly. Unfortunately, it's easy to forget to call the inherited operation. We can transform such an operation into a template method to give the parent control over how subclasses extend it. The idea is to call a hook operation from a template method in the parent class. Then subclasses can then override this hook operation.

---

## Implementation Hints & Modern Java Context
Three implementation issues are worth noting:
1. **Using Access Control:** In Java (adapted from C++ ), the primitive operations that a template method calls should be declared `protected`. This ensures that they are only called by the template method. Primitive operations that must be overridden are declared `abstract`. The template method itself should not be overridden; therefore you can make the template method a `final` member function.
2. **Minimizing primitive operations:** An important goal in designing template methods is to minimize the number of primitive operations that a subclass must override to flesh out the algorithm. The more operations that need overriding, the more tedious things get for clients.
3. **Naming conventions:** You can identify the operations that should be overridden by adding a prefix to their names. For example, some frameworks prefix template method names with "do-": `doCreateDocument()`, `doRead()`, and so forth.

**☕ Modern Java Best Practices:**
> While inheritance is the traditional mechanism for Template Method, modern Java leans heavily toward composition and functional programming (like the Strategy pattern). However, when a strict structural enforcement is required, `sealed` classes and interfaces in Java 17+ can restrict *which* concrete classes are allowed to implement the template steps, providing stronger security and domain modeling.

---

## Known Uses & Java API Usage
Template methods are so fundamental that they can be found in almost every abstract class. Wirfs-Brock et al. provide a good overview and discussion of template methods.

* **Java Core Libraries:** `java.util.AbstractList` provides a skeletal implementation of the `List` interface. The `addAll()` method is a template method that iterates over a collection and calls the primitive `add()` operation, which concrete lists must implement. `java.io.InputStream` uses `read(byte b[], int off, int len)` as a template method that repeatedly calls the abstract primitive operation `read()`.
* **Spring Framework:** The `JdbcTemplate` class uses this pattern extensively to handle JDBC workflow (opening connection, creating statements, handling exceptions, closing connection) while leaving the specific query mapping to callbacks.
* **JUnit:** The test execution lifecycle, where a framework orchestrates the sequence of `@BeforeEach`, `@Test`, and `@AfterEach` (or `setUp` and `tearDown` in older versions).

---

## Related Patterns
* **Factory Methods:** Are often called by template methods. In the Motivation example, the factory method `DoCreateDocument` is called by the template method `OpenDocument`.
* **Strategy:** Template methods use inheritance to vary part of an algorithm. Strategies use delegation to vary the entire algorithm.