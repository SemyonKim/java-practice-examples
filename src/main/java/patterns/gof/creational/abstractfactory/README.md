# Abstract Factory - Object Creational

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](AbstractFactoryDemonstration.java)**

## Intent & Core Problem
Provide an interface for creating families of related or dependent objects without specifying their concrete classes.

> **Also Known As:** Kit.

**☕ Java Note:**
> In Java, the "interface" mentioned in the intent is almost always implemented using the `interface` keyword. If partial default behavior is needed across factories, an `abstract class` can be used, but modern Java's `default` interface methods often make pure interfaces the preferred, more flexible choice.

---

## Motivation & Real-World Analogy
Consider a user interface toolkit that supports multiple look-and-feel standards, such as Motif and Presentation Manager. Different look-and-feels define different appearances and behaviors for user interface "widgets" like scroll bars, windows, and buttons. To be portable across look-and-feel standards, an application should not hard-code its widgets for a particular look and feel. Instantiating look-and-feel-specific classes of widgets throughout the application makes it hard to change the look and feel later.

We can solve this problem by defining an abstract `WidgetFactory` interface that declares an interface for creating each basic kind of widget. There is also an abstract interface for each kind of widget, and concrete subclasses implement widgets for specific look-and-feel standards. `WidgetFactory`'s interface has an operation that returns a new widget object for each abstract widget class. Clients call these operations to obtain widget instances, but clients aren't aware of the concrete classes they're using. Thus, clients stay independent of the prevailing look and feel.

A `WidgetFactory` also enforces dependencies between the concrete widget classes. A Motif scroll bar should be used with a Motif button and a Motif text editor, and that constraint is enforced automatically as a consequence of using a `MotifWidgetFactory`.

**☕ Java Note:**
> Dependency Injection (DI) frameworks like Spring or CDI perfectly complement this motivation. The client class simply declares a dependency on `WidgetFactory` (e.g., `@Autowired WidgetFactory factory`), and the framework injects the correct `MotifWidgetFactory` or `PMWidgetFactory` at runtime based on configuration, completely eliminating hard-coded instantiation.

---

## Motif / PM Toolkit Structure



```text
┌─────────────────────────┐                                        ┌──────────┐
│     WidgetFactory       │◄───────────────────────────────────────┤  Client  │
├─────────────────────────┤                                        └────┬──┬──┘
│ + CreateScrollBar()     │                                             │  │
│ + CreateWindow()        │                   ┌────────────┐            │  │
└────────────△────────────┘                   │   Window   │◄───────────┘  │
             │                                └───────△────┘               │
             │                                        │                    │
      ┌──────┴────────────┐                      ┌────┴─────────┐          │
      │                   │                      │              │          │
┌─────┴────────────┐ ┌────┴────────────┐   ┌─────┴──────┐ ┌─────┴───────┐  │
│MotifWidgetFactory│ │ PMWidgetFactory │   │  PMWindow  │ │ MotifWindow │  │
├──────────────────┤ ├─────────────────┤   └───▲────────┘ └────▲────────┘  │
│CreateScrollBar() │ │CreateScrollBar()│       │               │           │
│CreateWindow()    │ │CreateWindow()   │       │               │           │ 
└──────┬───────────┘ └────┬────────────┘- - - -┘    ┌ - - - -  ┘           │
       │                  │                         │                      │
       │                  │                         │    ┌───────────┐     │
       │                  │                         │    │ ScrollBar │◄─---┘
       │- - - - - - - - - - - - - - - - - - - - - - ┘    └───△───────┘
       │                  │                                  │
       │                  │                           ┌──────┴────────┐
       │                  │                           │               │
       │                  │                     ┌─────┴───────┐ ┌─────┴──────────┐
       │                  └ - - - - - - - - - -▶│ PMScrollBar │ │ MotifScrollBar │
       │                                        └─────────────┘ └───────▲────────┘
       │                                                                │
       └ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -┘
```

There is a concrete subclass of `WidgetFactory` for each look-and-feel standard. Each subclass implements the operations to create the appropriate widget for the look and feel. For example, the `createScrollBar` operation on the `MotifWidgetFactory` instantiates and returns a Motif scroll bar, while the corresponding operation on the `PMWidgetFactory` returns a scroll bar for Presentation Manager. Clients create widgets solely through the `WidgetFactory` interface and have no knowledge of the classes that implement widgets for a particular look and feel. In other words, clients only have to commit to an interface defined by an abstract class, not a particular concrete class.

**☕ Java Note:**
> Notice how the return types in the factory are strictly the abstract interfaces (`Window`, `ScrollBar`), not the concrete implementations (`MotifWindow`). This utilizes Java's polymorphism. Best practice is to package the `ConcreteFactory` and `ConcreteProduct` classes as package-private within their own module, exposing *only* the `AbstractFactory` and `AbstractProduct` `public` interfaces to the client.

---

## Applicability
Use the Abstract Factory pattern when:
* a system should be independent of how its products are created, composed, and represented.
* a system should be configured with one of multiple families of products.
* a family of related product objects is designed to be used together, and you need to enforce this constraint.
* you want to provide a class library of products, and you want to reveal just their interfaces, not their implementations.

**☕ Java Note:**
> Java's Module System (Project Jigsaw, introduced in Java 9) explicitly supports the last point. You can use the `exports` directive in `module-info.java` to expose only the package containing your interfaces, keeping the packages containing implementation details completely inaccessible from outside the module.

---

## Structure & Participants



```text
┌─────────────────────────┐                                        ┌──────────┐
│     AbstractFactory     │◄───────────────────────────────────────┤  Client  │
├─────────────────────────┤                                        └────┬─────┤
│ + createProductA()      │                                             │     │
│ + createProductB()      │                 ┌──────────────────┐        │     │
└────────────△────────────┘                 │ AbstractProductA │◄───────┘     │
             │                              └────────△─────────┘              │
             │                                       │                        │
      ┌──────┴──────────┐                      ┌─────┴─────────┐              │
      │                 │                      │               │              │
┌─────┴──────────┐ ┌────┴───────────┐   ┌──────┴──────┐ ┌──────┴───────┐      │
│ConcreteFactory1│ │ConcreteFactory2│   │ ProductA2   │ │   ProductA1  │      │
├────────────────┤ ├────────────────┤   └───────▲─────┘ └──────▲───────┘      │
│createProductA()│ │createProductA()│           │              │              │
│createProductB()│ │createProductB()│           │              │              │ 
└──────┬─────────┘ └──────┬─────────┘- - - - - -┘   ┌ - - - -  ┘              │
       │                  │                         │                         │
       │                  │                         │   ┌──────────────────┐  │
       │                  │                         │   │ AbstractProductB │◄─┘
       │- - - - - - - - - - - - - - - - - - - - - - ┘   └────────△─────────┘
       │                  │                                      │
       │                  │                               ┌──────┴────────┐
       │                  │                               │               │
       │                  │                         ┌─────┴───────┐ ┌─────┴─────────┐
       │                  └ - - - - - - - - - - - -▶│ ProductB2   │ │   ProductB1   │
       │                                            └─────────────┘ └───────▲───────┘
       │                                                                    │
       └ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -┘
```

* **AbstractFactory** (`WidgetFactory`)
    * declares an interface for operations that create abstract product objects.
* **ConcreteFactory** (`MotifWidgetFactory`, `PMWidgetFactory`)
    * implements the operations to create concrete product objects.
* **AbstractProduct** (`Window`, `ScrollBar`)
    * declares an interface for a type of product object.
* **ConcreteProduct** (`MotifWindow`, `MotifScrollBar`)
    * defines a product object to be created by the corresponding concrete factory.
    * implements the AbstractProduct interface.
* **Client**
    * uses only interfaces declared by AbstractFactory and AbstractProduct classes.

**☕ Java Note:**
> Use `@Override` annotations in your `ConcreteFactory` and `ConcreteProduct` classes to ensure compile-time verification that you are correctly implementing the abstract interfaces. For products that merely hold data, consider using Java 14+ `record` types for concise `ConcreteProduct` definitions.

---

## Collaborations
* Normally a single instance of a `ConcreteFactory` class is created at run-time. This concrete factory creates product objects having a particular implementation. To create different product objects, clients should use a different concrete factory.
* `AbstractFactory` defers creation of product objects to its `ConcreteFactory` subclass.

**☕ Java Note:**
> Instead of manually tracking the Singleton instance, delegate the lifecycle management of your `ConcreteFactory` to an Inversion of Control (IoC) container.

---

## Consequences
The Abstract Factory pattern has the following benefits and liabilities:

1.  **It isolates concrete classes.**  The Abstract Factory pattern helps you control the classes of objects that an application creates. Because a factory encapsulates the responsibility and the process of creating product objects, it isolates clients from implementation classes. Clients manipulate instances through their abstract interfaces; product class names are isolated in the implementation of the concrete factory; they do not appear in client code.
2.  **It makes exchanging product families easy.**  The class of a concrete factory appears only once in an application—that is, where it's instantiated. This makes it easy to change the concrete factory an application uses. It can use different product configurations simply by changing the concrete factory. Because an abstract factory creates a complete family of products, the whole product family changes at once. In our user interface example, we can switch from Motif widgets to Presentation Manager widgets simply by switching the corresponding factory objects and recreating the interface.
3.  **It promotes consistency among products.**  When product objects in a family are designed to work together, it's important that an application use objects from only one family at a time. `AbstractFactory` makes this easy to enforce.
4.  **Supporting new kinds of products is difficult.**  Extending abstract factories to produce new kinds of Products isn't easy. That's because the `AbstractFactory` interface fixes the set of products that can be created. Supporting new kinds of products requires extending the factory interface, which involves changing the `AbstractFactory` class and all of its subclasses. We discuss one solution to this problem in the Implementation section.

**☕ Java Note:**
> The liability (Point 4) represents a violation of the Open/Closed Principle. In modern Java, you can mitigate this by adding `default` methods to the `AbstractFactory` interface that throw an `UnsupportedOperationException`. This prevents breaking all existing `ConcreteFactory` implementations when a new product type is introduced to the interface.

---

## Implementation
Here are some useful techniques for implementing the Abstract Factory pattern.

1.  **Factories as singletons.** An application typically needs only one instance of a `ConcreteFactory` per product family, so it's usually best implemented as a Singleton.
2.  **Creating the products.** `AbstractFactory` only declares an interface for creating products; it's up to `ConcreteProduct` subclasses to actually create them. The most common way to do this is to define a factory method for each product. A concrete factory will specify its products by overriding the factory method for each. While this implementation is simple, it requires a new concrete factory subclass for each product family, even if the product families differ only slightly.
    * **Prototype-based approach:** If many product families are possible, the concrete factory can be implemented using the Prototype pattern. The concrete factory is initialized with a prototypical instance of each product in the family, and it creates a new product by cloning its prototype. This eliminates the need for a new concrete factory class for each new product family. Here is a way to implement a Prototype-based factory in Java. The concrete factory stores the prototypes to be cloned in a Map (acting as a dictionary) called `partCatalog`. The method `make` retrieves the prototype and clones it:
        ```java
        public Product make(String partName) {
            return partCatalog.get(partName).clone();
        }
        ```
      The concrete factory has a method for adding parts to the catalog.
    * **Class-based approach:** A variation on the Prototype-based approach is possible in languages that treat classes as first-class objects, like Java's `Class<?>`. You can think of a `Class` in these languages as a degenerate factory that creates only one kind of product. You can store class references inside a concrete factory in variables, much like prototypes. These classes create new instances on behalf of the concrete factory. You define a new factory by initializing an instance of a concrete factory with classes of products rather than by subclassing. The class-based version will have a single instance variable `partCatalog`, which is a Map whose key is the name of the part. Instead of storing prototypes to be cloned, `partCatalog` stores the `Class` definitions of the products. The method `make` now looks like this in Java:
        ```java
        public Product make(String partName) throws Exception {
            return partCatalog.get(partName).getDeclaredConstructor().newInstance();
        }
        ```
3.  **Defining extensible factories.** `AbstractFactory` usually defines a different operation for each kind of product it can produce. The kinds of products are encoded in the operation signatures; adding a new kind of product requires changing the `AbstractFactory` interface and all the classes that depend on it. A more flexible but less safe design is to add a parameter to operations that create objects. This parameter specifies the kind of object to be created. It could be a class identifier, an integer, a string, or anything else that identifies the kind of product. In fact, with this approach, `AbstractFactory` only needs a single "Make" operation with a parameter indicating the kind of object to create. This is the technique used in the Prototype- and class-based abstract factories discussed earlier.
    * **Type Safety Trade-off:** This variation is easier to use in dynamically typed languages than in statically typed languages like Java. You can use it in Java only when all objects have the same abstract base class or when the product objects can be safely coerced to the correct type by the client that requested them. But even when no coercion is needed, an inherent problem remains: All products are returned to the client with the same abstract interface as given by the return type. The client will not be able to differentiate or make safe assumptions about the class of a product. If clients need to perform subclass-specific operations, they won't be accessible through the abstract interface. Although the client could perform a downcast (e.g., using `instanceof` and casting in Java), that's not always feasible or safe, because the downcast can fail at runtime. This is the classic trade-off for a highly flexible and extensible interface.

**☕ Java Note:**
> When implementing extensible factories in Java, prefer Enums over `String` identifiers to avoid fragile "stringly-typed" logic. To bypass the unsafe downcasting issue described in point 3, modern Java leverages Generics: `<T extends Product> T make(Class<T> type)`. This provides the extensibility of a single parameter while guaranteeing compile-time type safety.

---

## Known Uses & Modern Java Architecture
Original uses of the pattern include:
* **Interviews:** Uses the "Kit" suffix to denote `AbstractFactory` classes. It defines `WidgetKit` and `DialogKit` abstract factories for generating look-and-feel specific user interface objects. It also includes a `LayoutKit` that generates different composition objects depending on the layout desired.
* **ET++:** Uses the Abstract Factory pattern to achieve portability across different window systems. The `WindowSystem` abstract base class defines the interface for creating objects that represent window system resources. Concrete subclasses implement the interfaces for a specific window system. At run-time, ET++ creates an instance of a concrete `WindowSystem` subclass that creates concrete system resource objects.

Modern Java Architectural uses:
* **JAXP (Java API for XML Processing):** `javax.xml.parsers.DocumentBuilderFactory` acts as an abstract factory allowing developers to generate XML parsers without coupling to a specific underlying engine (like Xerces).
* **JDBC (Java Database Connectivity):** The `java.sql.Connection` interface acts as an abstract factory for creating SQL operation objects (`Statement`, `PreparedStatement`) tailored to the specific database vendor (e.g., Oracle, PostgreSQL) connected at runtime.

**☕ Java Note:**
> Naming conventions have evolved. While "Kit" was popular in older C++ frameworks, modern Java overwhelmingly prefers the `Factory` suffix (e.g., `ThreadFactory`, `EntityManagerFactory`).

---

## Related Patterns
* `AbstractFactory` classes are often implemented with factory methods, but they can also be implemented using Prototype.
* A concrete factory is often a singleton.

**☕ Java Note:**
> When making a factory a Singleton in Java, using a single-element `enum` provides serialization safety and prevents reflection attacks, representing the most robust Singleton implementation strategy in the language.