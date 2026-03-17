# Decorator - Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](DecoratorDemonstration.java)**

## Intent & Core Problem
Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

> **Also Known As:** Wrapper

**☕ Java Note:**
> The Decorator pattern is heavily used in modern Java, particularly in the standard I/O libraries and whenever composition is favored over deep, rigid inheritance trees. It strictly adheres to the Single Responsibility Principle and the Open/Closed Principle.

---

## Motivation & Real-World Analogy
Sometimes we want to add responsibilities to individual objects, not to an entire class. A graphical user interface toolkit, for example, should let you add properties like borders or behaviors like scrolling to any user interface component.

One way to add responsibilities is with inheritance. Inheriting a border from another class puts a border around every subclass instance. This is inflexible, however, because the choice of border is made statically. A client can't control how and when to decorate the component with a border.

A more flexible approach is to enclose the component in another object that adds the border. The enclosing object is called a *decorator*. The decorator conforms to the interface of the component it decorates so that its presence is transparent to the component's clients. The decorator forwards requests to the component and may perform additional actions (such as drawing a border) before or after forwarding. Transparency lets you nest decorators recursively, thereby allowing an unlimited number of added responsibilities.

### Conceptual Decorator Nesting

```text
┌──────────────────────────────────────────────────┐
│ BorderDecorator                                  │
│ ┌──────────────────────────────────────────────┐ │
│ │ ScrollDecorator                              │ │
│ │ ┌──────────────────────────────────────────┐ │ │
│ │ │ TextView                                 │ │ │
│ │ │                                          │ │ │
│ │ │  This is a text view that has            │ │ │
│ │ │  been decorated with both a              │ │ │
│ │ │  scroll bar and a border.                │ │ │
│ │ └──────────────────────────────────────────┘ │ │
│ │                                          [▲] │ │
│ │                                          [▼] │ │
│ └──────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────┘
```
**Diagram Description:** A visual representation of how decorators enclose their targets. A `TextView` is enclosed by a `ScrollDecorator`, which in turn is enclosed by a `BorderDecorator`. The client interacts with the outermost boundary, unaware of the nesting.

For example, suppose we have a `TextView` object that displays text in a window. `TextView` has no scroll bars by default, because we might not always need them. When we do, we can use a `ScrollDecorator` to add them. Suppose we also want to add a thick black border around the `TextView`. We can use a `BorderDecorator` to add this as well.

### Object Composition

```text
  aBorderDecorator      aScrollDecorator          aTextView
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ component: ──────┼─>│ component: ──────┼─>│                  │
└──────────────────┘  └──────────────────┘  └──────────────────┘
```
**Diagram Description:** An object diagram showing how instances link together at runtime. `aBorderDecorator` holds a reference to `aScrollDecorator`, which holds a reference to the core `aTextView`.

The `ScrollDecorator` and `BorderDecorator` classes are subclasses of `Decorator`, an abstract class for visual components that decorate other visual components.

### Example Class Structure

```text
                        ┌───────────────┐
                        │VisualComponent│<──────────────────────────┐
                        ├───────────────┤                           │
                        │ draw()        │                           │
                        └───────────────┘                           │
                                ▲                                   │
                                │                                   │
                ┌───────────────┴───────────────┐                   │
                │                               │                   │
        ┌───────────────┐               ┌───────────────┐ component │
        │   TextView    │               │   Decorator   │<>─────────┘
        ├───────────────┤               ├───────────────┤
        │ draw()        │               │ draw() -------│------------ component->draw()
        └───────────────┘               └───────────────┘
                                                ▲
                                                │
                                ┌───────────────┴───────────────┐
                                │                               │
                        ┌───────────────┐               ┌───────────────┐
                        │ScrollDecorator│               │BorderDecorator│
                        ├───────────────┤               ├───────────────┤
                        │ draw()        │               │ draw()        │
                        │ scrollTo()    │               │ drawBorder()  │
                        └───────────────┘               └───────────────┘
```
**Diagram Description:** UML class diagram where `VisualComponent` is the common base. `TextView` is the concrete implementation. `Decorator` also implements `VisualComponent` but aggregates a `VisualComponent` instance. `ScrollDecorator` and `BorderDecorator` extend `Decorator` to add specific functionalities.

**☕ Java Note:** 
> In modern Java, `VisualComponent` is usually an `interface`. The `Decorator` is often an `abstract class` that implements the interface and delegates all method calls to the encapsulated `VisualComponent` by default.

---

## Applicability
Use Decorator:
* To add responsibilities to individual objects dynamically and transparently, that is, without affecting other objects.
* For responsibilities that can be withdrawn.
* When extension by subclassing is impractical. Sometimes a large number of independent extensions are possible and would produce an explosion of subclasses to support every combination. Or a class definition may be hidden or otherwise unavailable for subclassing.

---

## Structure & Participants

### Common Structure

```text
                    ┌───────────────┐
                    │   Component   │<──────────────────────────┐
                    ├───────────────┤                           │
                    │ operation()   │                           │
                    └───────────────┘                           │
                            ▲                                   │
                            │                                   │
            ┌───────────────┴───────────────┐                   │
            │                               │                   │
    ┌───────────────────┐           ┌───────────────┐ component │
    │ ConcreteComponent │           │   Decorator   │<>─────────┘
    ├───────────────────┤           ├───────────────┤
    │ operation()       │           │ operation() --│----------- component->operation()
    └───────────────────┘           └───────────────┘
                                            ▲
                                            │
                            ┌───────────────┴───────────────┐
                            │                               │
                    ┌────────────────────┐         ┌────────────────────┐
                    │ ConcreteDecoratorA │         │ ConcreteDecoratorB │
                    ├────────────────────┤         ├────────────────────┤
                    │ operation()        │         │ operation() -------│---┐ super.operation();
                    │ addedState         │         │ addedBehavior()    │   │ addedBehavior();
                    └────────────────────┘         └────────────────────┘   └───────────────────┘
```
**Diagram Description:** The generic structural UML diagram. `Client` depends on `Component`. `ConcreteComponent` implements the base behavior. `Decorator` implements `Component` and wraps another `Component`. Concrete decorators add state or behavior before/after delegating to the wrapped component.

* **Component (`VisualComponent`)**: Defines the interface for objects that can have responsibilities added to them dynamically.
* **ConcreteComponent (`TextView`)**: Defines an object to which additional responsibilities can be attached.
* **Decorator**: Maintains a reference to a `Component` object and defines an interface that conforms to `Component`'s interface.
* **ConcreteDecorator (`BorderDecorator`, `ScrollDecorator`)**: Adds responsibilities to the component.

---

## Collaborations
* Decorator forwards requests to its `Component` object. It may optionally perform additional operations before and after forwarding the request.

---

## Consequences
1. **More flexibility than static inheritance:** Decorators provide a more flexible way to add responsibilities to objects than can be had with static (multiple) inheritance. You can add or remove responsibilities at run-time simply by attaching or detaching them.
2. **Avoids feature-laden classes high up in the hierarchy:** Decorator offers a pay-as-you-go approach to adding responsibilities. Instead of trying to support all foreseeable features in a complex, customizable class, you can define a simple class and add functionality incrementally.
3. **A decorator and its component aren't identical:** A decorator acts as a transparent enclosure. But from an object identity point of view, a decorated component is not identical to the component itself. Hence, you shouldn't rely on object identity (`==` in Java) when you use decorators.
4. **Lots of little objects:** A design that uses Decorator often results in systems composed of lots of little objects that all look alike. They differ only in the way they are interconnected, not in their class or in the value of their variables. This can make the system hard to learn and debug.

---

## Implementation Hints & Modern Java Context

### Omitting the Abstract Decorator

```text
                          ┌───────────────┐
                          │   Component   │<─────────────────────┐
                          ├───────────────┤                      │
                          │ operation()   │                      │
                          └───────────────┘                      │
                                  ▲                              │
                                  │                              │
                  ┌───────────────┴────────────────┐             │
                  │                                │             │
        ┌───────────────────┐               ┌────────────────┐   │
        │ ConcreteComponent │               │   Decorator    │<>─┘
        ├───────────────────┤               ├────────────────┤
        │ operation()       │               │ operation()    │
        └───────────────────┘               │ addedBehavior()│
                                            └────────────────┘
```
**Diagram Description:** If you only need to add one specific responsibility, there is no need to define an abstract `Decorator` class. You can merge the `Decorator` and `ConcreteDecorator` responsibilities into a single class.

### Keeping Component Classes Lightweight

```text
      (Heavyweight)                             (Lightweight)
┌───────────────────────┐                 ┌───────────────────────┐
│       Component       │                 │       Component       │
├───────────────────────┤                 ├───────────────────────┤
│ complexState          │                 │ operation()           │
│ manyMethods()         │                 └───────────────────────┘
└───────────────────────┘                             ▲
          ▲                                           │
          │                               ┌───────────┴───────────┐
┌─────────┴─────────┐                     │                       │
│    Decorator      │           ┌─────────┴─────────┐     ┌───────┴───────┐
└───────────────────┘           │ ConcreteComponent │     │   Decorator   │
                                └───────────────────┘     └───────────────┘
```
**Diagram Description:** To ensure decorators remain transparent and cheap to instantiate, the base `Component` should be as lightweight as possible. It should focus on defining an interface, not storing data. If the base class is too heavy, the decorators become bloated.

* **Interface vs Abstract Class:** In Java, always prefer making `Component` an `interface`. This prevents decorators from inheriting unnecessary state and avoids Java's single-inheritance limitation.
* **Default Methods:** While Java 8 `default` methods allow adding behavior to interfaces, they are static at compile time. Decorator is still required when you need to combine behaviors *dynamically* at runtime.

---

## Known Uses (Modern Java)
* **Java I/O Streams:** The classic example. `InputStream` is the `Component`. `FileInputStream` is a `ConcreteComponent`. `FilterInputStream` is the abstract `Decorator`. `BufferedInputStream` and `DataInputStream` are concrete decorators.
    ```java
    // Decorating a file stream with buffering and data-parsing capabilities
    InputStream in = new DataInputStream(
                         new BufferedInputStream(
                             new FileInputStream("data.txt")));
    ```
* **Java Collections Framework:** Methods like `Collections.synchronizedList(List)` or `Collections.unmodifiableList(List)` return decorated versions of the provided list, adding thread-safety or immutability dynamically.
* **UI Frameworks (Swing/JavaFX):** Borders and ScrollPanes often act as decorators around core UI components.

---

## Related Patterns
* **Adapter:** A decorator changes an object's responsibilities, not its interface; an adapter gives an object a completely new interface.
* **Composite:** A decorator can be viewed as a degenerate composite with only one component. However, a decorator adds additional responsibilities—it isn't intended for object aggregation.
* **Strategy:** A decorator lets you change the skin of an object; a strategy lets you change the guts.