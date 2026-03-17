# Factory Method - Class Creational

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](FactoryMethodDemonstration.java)**

## Intent & Core Problem
Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.

> **Also Known As:** Virtual Constructor

**☕ Java Note:**
> While traditionally implemented using abstract classes and overridden methods, modern Java often utilizes interfaces with `default` methods or functional interfaces (like `Supplier<T>`) to achieve the same deferred instantiation with less boilerplate and tighter coupling to functional programming paradigms.

---

## Motivation & Real-World Analogy
Frameworks often use abstract classes to define and maintain relationships between objects. A framework is typically responsible for creating these objects as well.

Consider a framework for applications that present multiple documents to a user. Two key abstractions are the `Application` and `Document` classes. Both are abstract; clients must subclass them for application-specific implementations (e.g., `DrawingApplication` and `DrawingDocument`).

The `Application` class manages `Documents` and creates them when necessary (e.g., when the user clicks "New"). However, the `Application` class cannot predict *which* `Document` subclass to instantiate. It only knows *when* to create a document, not *what kind*. This creates a dilemma: the framework must instantiate classes, but it only knows about abstract classes.

The Factory Method pattern encapsulates the knowledge of which `Document` subclass to create and moves it out of the framework.

### Application/Document Example

```text
       ┌──────────────────────┐  docs   ┌──────────────────────────────┐
       │       Document       │◄────────┤         Application          │
       ├──────────────────────┤         ├──────────────────────────────┤
       │ + open()             │         │ + createDocument()           │
       │ + close()            │         │ + newDocument()              │o─┐
       │ + save()             │         │ + openDocument()             │  │
       │ + revert()           │         └──────────────────────────────┘  │
       └──────────────────────┘                        △                  │ Document doc = createDocument()
                  △                                    │                  │ docs.add(doc)
          ┌───────┴───────┐                  ┌─────────┴──────────┐       │ doc.open()
          │  MyDocument   │                  │   MyApplication    │       │ 
          └───────▲───────┘                  ├────────────────────┤◄──────┘
                  │                          │ + createDocument() │o─┐
                  │       instantiates       └────────────────────┘  │ 
                  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┘ return new MyDocument()
```
*Note: `Application` subclasses redefine the abstract `CreateDocument` operation to return the appropriate `Document` subclass. `CreateDocument` is the factory method.*

---

## Applicability
Use the Factory Method pattern when:
* A class cannot anticipate the class of objects it must create.
* A class wants its subclasses to specify the objects it creates.
* Classes delegate responsibility to one of several helper subclasses, and you want to localize the knowledge of which helper subclass is the delegate.

---

## Structure & Participants

### Common Structure

```text
┌─────────────────┐           ┌─────────────────┐
│     Product     │           │     Creator     │
├─────────────────┤           ├─────────────────┤
│                 │           │ FactoryMethod() │
└─────────────────┘           │ AnOperation()   │o──┐
         △                    └─────────────────┘   │ 
         │                             △            │ ...
         │                             │            │ product = FactoryMethod();
┌─────────────────┐           ┌─────────────────┐   │ ...
│ ConcreteProduct │◄──────────│ ConcreteCreator │   │ 
├─────────────────┤  creates  ├─────────────────┤   │
│                 │           │ FactoryMethod() │◄──┘
└────────────▲────┘           └───────────────o─┘
             └─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
                return new ConcreteProduct();                               
```

### Participants
* **Product** (`Document`): Defines the interface of objects the factory method creates.
* **ConcreteProduct** (`MyDocument`): Implements the Product interface.
* **Creator** (`Application`): Declares the factory method, which returns an object of type Product. Creator may also define a default implementation of the factory method that returns a default ConcreteProduct object.
* **ConcreteCreator** (`MyApplication`): Overrides the factory method to return an instance of a ConcreteProduct.

---

## Collaborations
* The Creator relies on its subclasses to define the factory method so that it returns an object of the appropriate ConcreteProduct.

---

## Consequences (Trade-offs)
* **Eliminates hard-coding:** Factory methods eliminate the need to bind application-specific classes into your code. The code only deals with the Product interface.
* **Subclassing requirement:** A potential drawback is that clients might have to subclass the Creator class *just* to create a particular ConcreteProduct object.
* **Provides hooks for subclasses:** The factory method is a flexible hook inside a class to provide an extended version of an object.
* **Connects parallel class hierarchies:** Factory methods are highly useful when a class delegates some of its responsibilities to a separate class, resulting in parallel hierarchies.

### Parallel Class Hierarchies

```text
       ┌─────────────────────────┐                   ┌─────────────────────────┐
       │         Figure          │                   │       Manipulator       │
       ├─────────────────────────┤                   ├─────────────────────────┤
       │ + createManipulator()   │◄──── Client ─────▶│ + downClick()           │
       │   ...                   │                   │ + drag()                │
       └─────────────────────────┘                   │ + upClick()             │
                    △                                └────────────△────────────┘
          ┌─────────┴─────────┐                         ┌─────────┴─────────────┐
          │                   │                         │                       │
┌────────────────────┐   ┌────────────────────┐       ┌──────────────────┐  ┌──────────────────┐
│   LineFigure       │   │   TextFigure       │       │  LineManipulator │  │  TextManipulator │
├────────────────────┤   ├────────────────────┤       ├──────────────────┤  ├──────────────────┤
│+createManipulator()│   │+createManipulator()│       │ + downClick()    │  │ + downClick()    │
└─────────o──────────┘   └─────────o──────────┘       │ + drag()         │  │ + drag()         │
          │                        │                  │ + upClick()      │  │ + upClick()      │
          │      instantiates      │                  └─────────▲────────┘  └─────────▲────────┘
          └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘                     │
                                   │                                                  │
                                   └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─  ┘
                                            instantiates
```
*Note: A `Figure` acts as a Creator generating its corresponding `Manipulator` Product. If you add a `CircleFigure`, you easily plug in a `CircleManipulator` by overriding the factory method.*

---

## Implementation Hints & Modern Java Context
* **Two major varieties:** The Creator class can be abstract and provide *no* implementation for the factory method, or it can be concrete and provide a *default* implementation.
* **Parameterized factory methods:** The factory method can take a parameter that identifies the kind of object to create, allowing a single method to create multiple Product types.
* **Modern Java - Generics:** Java's generics allow for elegant parameterized factories. A `Creator<T>` can dictate that its factory method returns `T`.
* **Modern Java - Functional Interfaces:** Rather than creating an entire subclass hierarchy just to override a factory method, modern Java often injects a `Supplier<Product>` into the Creator, heavily reducing boilerplate.

---

## Modern Java Architectural Uses
* **Java Collections Framework:** `java.lang.Iterable#iterator()` is a classic factory method. It returns an `Iterator` product, and different collections (like `ArrayList` or `HashSet`) implement it to return their specific concrete iterators.
* **SLF4J:** `LoggerFactory.getLogger(Class)` acts as a factory method, yielding an underlying logging implementation (Logback, Log4j2) configured at runtime.
* **Spring Framework:** The `FactoryBean<T>` interface provides a mechanism to encapsulate complex bean initialization logic, letting subclasses or implementations dictate the actual bean instance returned to the container.

---

## Related Patterns
* **Abstract Factory:** Often implemented using Factory Methods. (e.g., The Abstract Factory's `makeRoom()` and `makeDoor()` methods are Factory Methods).
* **Template Method:** Factory Methods are usually called within Template Methods. (e.g., the `NewDocument` operation in the `Application` class is a Template Method that calls the `CreateDocument` Factory Method).
* **Prototype:** Doesn't require subclassing Creator, but often requires an `Initialize` operation on the Product class, which Creator uses to initialize the object.