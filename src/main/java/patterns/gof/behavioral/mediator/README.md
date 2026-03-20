# Mediator - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](MediatorDemonstration.java)**

## Intent & Core Problem
Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly, allowing you to vary their interaction independently.

**☕ Java Note: The Core Philosophy**
> In modern Java, the Mediator is the ultimate "middleman." It shifts a system from a complex many-to-many communication web (a graph) to a one-to-many star topology, drastically reducing dependency graphs and preventing "spaghetti code."

---

## Motivation & Real-World Analogy
Object-oriented design encourages distributing behavior among objects. However, this distribution can result in an object structure with so many interconnections that every object ends up knowing about every other object. This monolithic entanglement makes it difficult to reuse or modify objects independently.


*Analogy: Just as airplanes do not communicate directly with each other to avoid collisions, but instead route all status updates and requests through a central Air Traffic Control tower (the Mediator), objects in software should often route complex interactions through a central coordinator.*

Consider the implementation of a graphical user interface dialog box. A dialog box consists of multiple widgets (buttons, list boxes, text entry fields). Dependencies between these widgets can quickly become tangled:
* Selecting an item in a list box might change the contents of a text entry field.
* Typing in the text entry field might enable a "Submit" button.

### The Problem: Tangled Dependencies
Without a central coordinator, each widget must hold references to other widgets to trigger updates, resulting in a chaotic web of dependencies:

```text
      +-------------+                          +----------------+
      |   ListBox   | <----------------------> |   EntryField   |
      +-------------+ \                      / +----------------+
     ^     ^           \                    /            ^      ^
    /      |            \                  /             |       \
   /       |             \                /              |        \
  V        v              v              v               v         V
      +-------------+                          +----------------+
      | Button (OK) | <----------------------> | Button (Cancel)|
      +-------------+                          +----------------+
```
*Diagram 1: A tightly coupled system where every UI widget communicates directly with others. This structure is highly rigid and impossible to reuse.*

### The Solution: The Mediator (Director)
To avoid this, we introduce a `DialogDirector` object that acts as a Mediator. The widgets no longer communicate directly. Instead, when a widget's state changes, it simply notifies the director. The director contains the logic to update other widgets accordingly.

```text
  +-------------+                          +----------------+
  |   ListBox   |                          |   EntryField   |
  +-------------+                          +----------------+
         \                                        /
          \                                      /
           v                                    v
         +----------------------------------------+
         |          FontDialogDirector            |
         +----------------------------------------+
           ^                                    ^
          /                                      \
         /                                        \
  +-------------+                          +----------------+
  | Button (OK) |                          | Button (Cancel)|
  +-------------+                          +----------------+
```
*Diagram 2: The Mediator pattern replaces a tangled web with a star topology. The `FontDialogDirector` acts as the central hub, decoupling the widgets from one another.*

### Class & Interaction Diagrams

```text
      +----------------+                    +----------------+
      | DialogDirector |<-------------------|    Widget      |
      +----------------+                    +----------------+
      | ShowDialog()   |                    | Changed()      |
      | WidgetChanged()|                    +----------------+
      +----------------+                            ^
              ^                                     |
              |                         +-----------+-----------+
              |                         |                       |
   +--------------------+       +----------------+   +----------------+
   | FontDialogDirector |       |    ListBox     |   |   EntryField   |
   +--------------------+       +----------------+   +----------------+
   | WidgetChanged()    |       | GetSelection() |   | SetText()      |
   | CreateWidgets()    |       +----------------+   +----------------+
   +--------------------+
```
*Diagram 3: Class structure of the Dialog system. Notice how `Widget` relies only on the abstract `DialogDirector`, keeping it completely isolated from other sibling widgets.*

```text
    aListBox        aFontDialogDirector         anEntryField
       |                     |                       |
       |---- Changed() ----->|                       |
       |                     |                       |
       |<-- GetSelection() --|                       |
       |                     |                       |
       |                     |------ SetText() ----->|
       |                     |                       |
```
*Diagram 4: Object interaction sequence. The `ListBox` triggers the `Changed()` event on the director. The director queries the `ListBox` and explicitly updates the `EntryField`.*

**☕ Java Note: Event-Driven UIs**
> This exact pattern is how Java Swing and JavaFX manage complex form states. Instead of hardcoding widget-to-widget interaction, a controller class listens to `ActionEvent` or `ChangeListener` callbacks and updates the rest of the form.
```java
// Java: Mediator handling an event
public void widgetChanged(Widget w) {
    if (w == fontListBox) {
        entryField.setText(fontListBox.getSelection());
    }
}
```

---

## Applicability
Use the Mediator pattern when:
* A set of objects communicate in well-defined but complex ways, resulting in unstructured and hard-to-understand dependencies.
* Reusing an object is difficult because it refers to and communicates with many other objects.
* A behavior that's distributed between several classes should be customizable without a lot of subclassing.

---

## Structure & Participants

```text
       +-----------+                      mediator +-----------+
       | Mediator  |<------------------------------| Colleague |
       +-----------+                               +-----------+
             ^                                           ^
             |                                           |
             |                            +--------------+--------------+
    +------------------+                  |                             |
    | ConcreteMediator |        +--------------------+         +--------------------+
    +------------------+------->| ConcreteColleague1 |   +---->| ConcreteColleague2 |
                       |        +--------------------+   |     +--------------------+
                       |                                 |
                       +---------------------------------+         
```
*Diagram 5: The generic structure of the Mediator pattern.*

```text
    aColleague1          aMediator           aColleague2
         |                   |                    |
         |----- Changed ---->|                    |
         |                   |                    |
         |                   |------ Action ----->|
         |                   |                    |
```
*Diagram 6: The generic interaction sequence showing the Mediator routing an action between two loosely coupled Colleagues.*

### Participants
1.  **Mediator (`DialogDirector`):** Defines an interface for communicating with `Colleague` objects.
2.  **ConcreteMediator (`FontDialogDirector`):** Implements cooperative behavior by coordinating `Colleague` objects. It knows and maintains its colleagues.
3.  **Colleague Classes (`ListBox`, `EntryField`):** Each colleague class knows its `Mediator` object. It communicates with its mediator whenever it would have otherwise communicated with another colleague.

---

## Collaborations
Colleagues send and receive requests from a Mediator object. The mediator routes these requests to the appropriate colleague(s).

---

## Consequences & Trade-offs
1.  **Limits Subclassing:** A mediator localizes behavior that would otherwise be distributed among several objects. Changing this behavior requires subclassing *only* the Mediator, not the Colleagues.
2.  **Decouples Colleagues:** Colleagues become independent. You can reuse Colleague and Mediator classes independently.
3.  **Simplifies Object Protocols:** It replaces many-to-many interactions with one-to-many interactions between the mediator and colleagues, which are easier to understand and maintain.
4.  **Abstracts Object Cooperation:** The mediator encapsulates how objects interact, allowing you to focus on high-level system behavior rather than individual object connections.
5.  **Centralizes Control (The Monolith Risk):** The primary drawback is that the `ConcreteMediator` can grow into a massive, overly complex "God Object" (a monolith) that is difficult to maintain.

---

## Implementation Hints & Modern Java Context

### 1. Omitting the Abstract Mediator
There is no need to define an abstract `Mediator` interface if colleagues are only ever going to work with one specific `ConcreteMediator`.

### 2. Colleague-Mediator Communication
In modern Java, Colleagues typically communicate with the Mediator using the **Observer** pattern. Instead of a hardcoded `Changed()` method, the Mediator registers itself as an event listener for various Colleagues.

**☕ Java Note: Modernizing with Event Buses**
> While the classic Mediator involves direct references, modern Java backends often implement a decentralized Mediator using an Event Bus (like Guava's `EventBus` or Spring's `ApplicationEventPublisher`). This further decouples the system, as colleagues just publish events, and the mediator (or specialized listener classes) subscribes to them.
```java
// Spring Framework Mediator via Event Publishing
@Component
public class OrderService { // Colleague
    @Autowired private ApplicationEventPublisher publisher; // Mediator
    
    public void placeOrder() {
        publisher.publishEvent(new OrderPlacedEvent(this));
    }
}
```

---

## Known Uses & Java API Usage
* **Spring MVC / WebFlux:** The `DispatcherServlet` acts as a central Mediator, receiving all incoming HTTP requests and routing them to the appropriate `@Controller` (Colleagues).
* **Java Message Service (JMS):** Message Brokers (like ActiveMQ or RabbitMQ) act as out-of-process mediators, routing messages between decoupled microservices.
* **Java `java.util.Timer` / `ScheduledThreadPoolExecutor`:** Coordinates the execution of multiple independent `TimerTask` colleagues.
* **Java UI Toolkits:** Classes like `ButtonGroup` in Swing act as specific mediators to ensure only one `JRadioButton` is selected at a time.

---

## Related Patterns
* **Facade:** Differs from Mediator in that it abstracts a subsystem to provide a convenient, *unidirectional* interface (Facade makes requests of the subsystem, but not vice versa). Mediator enables *multidirectional* cooperative behavior.
* **Observer:** Colleagues can communicate with the Mediator using the Observer pattern, where the Mediator acts as a subscriber to the Colleagues' events.