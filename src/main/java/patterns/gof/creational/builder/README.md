# Builder - Object Creational

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](BuilderDemonstration.java)**

## Intent & Core Problem
Separate the construction of a complex object from its representation so that the same construction process can create different representations.

**☕ Java Note:**
> The classic Gang of Four (GoF) Builder focuses heavily on abstraction and polymorphism (using a Director and multiple builder implementations to create entirely different classes). In modern Java, the "Builder Pattern" most commonly refers to the *Effective Java* style (Joshua Bloch's Builder), which uses a `static` inner class to handle complex constructors and optional parameters for a *single* immutable class. Both solve creation complexity, but the GoF pattern is about polymorphic construction steps, while the Modern Java pattern is about fluent, safe instantiation.

---

## Motivation & Real-World Analogy
Imagine writing a reader for the RTF (Rich Text Format) document exchange format. The reader needs to convert RTF into multiple target formats, such as plain ASCII text, HTML, or an interactive text widget. The number of possible target formats is open-ended, so the RTF reader shouldn't be hard-coded to support specific conversions.

The solution is to configure the `RTFReader` class with a `TextConverter` object. As the reader parses the RTF document, it issues requests to the converter to transform text and formatting tokens. `TextConverter` objects are responsible for both performing the data conversion and assembling the final representation.

Subclasses of `TextConverter` specialize in different outputs. An `ASCIIConverter` ignores styling requests and only extracts plain text. An `HTMLConverter` translates styling into HTML tags. A `TextWidgetConverter` builds a complex UI component.

Each converter takes the same sequence of construction instructions from the reader but produces a wildly different result. The `RTFReader` (the Director) knows *how* to parse RTF and sequence the steps, while the `TextConverter` (the Builder) knows *how* to represent the final product.

### Conceptual Example Diagram

```text
┌─────────────┐       builder      ┌───────────────────────┐
│  RTFReader  │───────────────────►│    TextConverter      │
├─────────────┤                    ├───────────────────────┤
│ parseRTF()  │                    │ convertCharacter(c)   │
└─────────────┘                    │ convertFontChange(f)  │
                                   │ convertParagraph()    │
                                   └───────────────────────┘
                                               △
                                               │
       ┌─────────────────────┬─────────────────┴─────────┬─────────────────────────┐
       │                     │                           │                         │
┌───────────────────┐    ┌────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│  ASCIIConverter   │    │    TeXConverter    │    │    HTMLConverter    │    │ TextWidgetConverter │
├───────────────────┤    ├────────────────────┤    ├─────────────────────┤    ├─────────────────────┤
│convertCharacter(c)│    │convertCharacter(c) │    │convertCharacter(c)  │    │convertCharacter(c)  │
│getASCIIText()     │    │convertFontChange(f)│    │convertFontChange(f) │    │convertFontChange(f) │
│                   │    │convertParagraph()  │    │convertParagraph()   │    │convertParagraph()   │
│                   │    │getTeXText()        │    │getHTMLText()        │    │getTextWidget()      │
└───────────────────┘    └────────────────────┘    └─────────────────────┘    └─────────────────────┘
```
*Note: The `RTFReader` directs the construction process, while concrete converters assemble their specific representations.*

---

## Applicability
Use the Builder pattern when:
* The algorithm for creating a complex object should be independent of the parts that make up the object and how they are assembled.
* The construction process must allow different representations for the object that's constructed.
* **Modern Context:** You have a class with a telescoping constructor (many parameters, some optional) and want to enforce immutability without confusing the caller.

---

## Structure & Participants

### Common Structure

```text
┌─────────────┐       builder      ┌───────────────────────┐
│  Director   │───────────────────►│       Builder         │
├─────────────┤                    ├───────────────────────┤
│ construct() │                    │ buildPart()           │
└─────────────┘                    └───────────────────────┘
       o                               ▲       △
       │                               │       │
       │ for all objects in structure: │       │
       │     builder.buildPart()       │       │
       └───────────────────────────────┘       │
                                               │
                                   ┌───────────────────────┐
                                   │   ConcreteBuilder     │
                                   ├───────────────────────┤
                                   │ buildPart()           │
                                   │ getResult()           │
                                   └───────────────────────┘
                                               │ creates
                                               ▼
                                   ┌───────────────────────┐
                                   │       Product         │
                                   └───────────────────────┘
```

### Participants
* **Builder** (`TextConverter`): Specifies an abstract interface for creating parts of a Product object.
* **ConcreteBuilder** (`ASCIIConverter`, `HTMLConverter`, `TeXConverter`, `TextWidgetConverter`): Constructs and assembles parts of the product by implementing the Builder interface. Defines and keeps track of the representation it creates and provides an interface for retrieving the product.
* **Director** (`RTFReader`): Constructs an object using the Builder interface. Focuses on the sequence of assembly.
* **Product** (`ASCIIText`, `HTMLText`, `TextWidget`, `TextWidget`): The complex object under construction. Includes classes that define the constituent parts and interfaces for assembling them.

**☕ Java Note:**
> In Java, the `Product` classes returned by different `ConcreteBuilder` implementations often do *not* share a common base class or interface. An HTML string and a complex UI Widget have fundamentally different interfaces, which is why the `getResult()` method is usually declared on the `ConcreteBuilder` rather than the abstract `Builder` root.

---

## Collaborations
* The client creates the `Director` object and configures it with the desired `ConcreteBuilder` object.
* The `Director` notifies the builder whenever a part of the product should be built.
* The builder handles requests from the director and adds parts to the product.
* The client retrieves the product directly from the builder.

### Interaction Sequence

1. `aClient` instantiates `aConcreteBuilder`.
2. `aClient` instantiates `aDirector`.
3. `aClient` calls `Construct()` on `aDirector`.
4. `aDirector` iteratively calls build methods on `aConcreteBuilder`.
5. `aClient` calls `GetResult()` on `aConcreteBuilder` to obtain the finished product.

```text
     aClient                         aDirector            aConcreteBuilder
        │                                │                       │
        │ new ConcreteBuilder()          │                       │
        │───────────────────────────────────────────────────────►│
        │                                │                       │
        │ new Director(aConcreteBuilder) │                       │
        │───────────────────────────────►│                       │
        │                                │                       │
        │ construct()                    │                       │
        │───────────────────────────────►│                       │
        │                                │ buildPartA()          │
        │                                │──────────────────────►│
        │                                │                       │
        │                                │ buildPartB()          │
        │                                │──────────────────────►│
        │                                │                       │
        │                                │ buildPartC()          │
        │                                │──────────────────────►│
        │                                │                       │
        │ getResult()                    │                       │
        │───────────────────────────────────────────────────────►│
        │                                │                       │
```

---

## Consequences (Trade-offs)
* **Varying a product's internal representation:** The Builder object hides the internal structure of the product and how it gets assembled. To change the representation, you simply define a new builder.
* **Isolating code for construction and representation:** Clients do not need to know about the classes that define the product's internal structure. Encapsulation is heavily promoted.
* **Finer control over the construction process:** Unlike Creational patterns that build products in one shot (like Abstract Factory), the Builder pattern constructs the product step-by-step under the director's control, allowing for complex validation before finalizing the object.

---

## Implementation Hints & Modern Java Context
* **Empty Default Methods:** In modern Java, the abstract `Builder` is best implemented as an `interface` with `default` methods that do nothing. This allows `ConcreteBuilder` subclasses to only override the operations they care about (e.g., an `ASCIIConverter` doesn't need to implement a complex font-change method; the default empty method is sufficient).
* **Fluent Interfaces:** Builders naturally pair with fluent APIs. Having build methods return `this` allows for chaining (e.g., `builder.addWall().addDoor().build()`).
* **The Bloch Builder:** The most common form of Builder in Java uses a `static final` inner class to gather parameters before passing them to a `private` constructor of the outer class. This provides thread safety and immutability without the complexity of a separate Director.

---

## Modern Java Architectural Uses
* **Java Core:** `java.lang.StringBuilder` (and `StringBuffer`) are classic examples of the GoF Builder pattern (where the string being built is the Product).
* **Java Streams:** `java.util.stream.Stream.Builder` handles step-by-step element accumulation before yielding a terminal `Stream`.
* **Lombok:** The `@Builder` annotation automatically generates Bloch-style builders for Java POJOs.
* **Spring Framework:** Classes like `BeanDefinitionBuilder` construct complex bean configurations programmatically.

---

## Related Patterns
* **Abstract Factory:** Similar in that it creates complex objects. However, Builder focuses on step-by-step construction of a *single* complex object, while Abstract Factory focuses on creating *families* of related objects in a single step.
* **Composite:** What the Builder often builds is a Composite structure (like a parsed syntax tree or a UI component tree).