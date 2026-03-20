# Observer - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](ObserverDemonstration.java)**

## Intent & Core Problem
Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.

> **Also Known As:** Dependents, Publish-Subscribe

**☕ Java Note:**
> In modern Java, the Observer pattern is fundamental to event-driven architectures. While the legacy `java.util.Observer` and `java.util.Observable` classes have been deprecated since Java 9 due to their inflexibility, the pattern thrives through `java.beans.PropertyChangeListener`, the Reactive Streams API (`java.util.concurrent.Flow`), and robust framework-level event dispatchers (like Spring's `ApplicationEventPublisher`).

---

## Motivation & Real-World Analogy
A common challenge when partitioning a system into a collection of cooperating classes is maintaining consistency between related objects without creating tight coupling, which severely reduces reusability.

Consider graphical user interfaces (GUIs) that separate presentation logic from underlying application data. Classes defining application data and those defining presentations can be reused independently. However, a spreadsheet object and a bar chart object can both depict information from the identical application data object. Neither presentation object knows about the other, allowing you to reuse only the components you need. Yet, they behave as though they do: when the user alters information in the spreadsheet, the bar chart instantly reflects the changes.

```text
    ┌──────────────────────────────────────┐
    │           ApplicationData            │
    │              (Subject)               │
    └──────────────────────────────────────┘                     ^ - requests, modifications
       │    ^       │    ^        │    ^                         │
       ▼    │       ▼    │        ▼    │
    ┌──────────┐ ┌──────────┐ ┌────────────┐                │ 
    │aBarChart │ │aPieChart │ │aSpreadsheet│                ▼  - change notification       
    │(Observer)│ │(Observer)│ │(Observer)  │
    └──────────┘ └──────────┘ └────────────┘
```
*Diagram 1: The ApplicationData subject notifies multiple independent presentation observers whenever its state changes.*

The Observer pattern describes how to establish these relationships. The key participants are **Subject** and **Observer**. A subject may have any number of dependent observers. All observers are notified whenever the subject undergoes a state change. In response, each observer interrogates the subject to synchronize its state with the subject's state.

---

## Applicability
Use the Observer pattern in any of the following situations:
* When an abstraction has two aspects, one dependent on the other. Encapsulating these aspects in separate objects lets you vary and reuse them independently.
* When a change to one object requires changing others, and you don't know exactly how many objects need to be changed.
* When an object should be able to notify other objects without making assumptions about what those objects are (i.e., avoiding tight coupling).

---

## Structure & Participants

```text
┌──────────────────┐                                ┌──────────────────┐
│     Subject      │ ─────────────────────────────► │     Observer     │
├──────────────────┤  observers                     ├──────────────────┤
│ + Attach(o)      │                                │ + Update()       │
│ + Detach(o)      │                                └──────────────────┘
│ + Notify() ------│---> for all o in observers:              ▲
└──────────────────┘         o->Update()                      │
         ▲                                                    │
         │                                                    │
┌──────────────────┐                                  ┌──────────────────┐
│ ConcreteSubject  │ ◄─────────────────────────────── │ ConcreteObserver │
├──────────────────┤                         subject  ├──────────────────┤
│ - subjectState   │                                  │ - observerState  │
├──────────────────┤                                  ├──────────────────┤
│ + GetState() ----│---> return subjectState          │ + Update() ------│---> observerState 
│ + SetState()     │                                  └──────────────────┘         = subject->GetState()
└──────────────────┘
```
*Diagram 2: Standard Observer structural model showcasing abstract coupling between Subject and Observer.*

* **Subject:** Knows its observers. Any number of `Observer` objects may observe a subject. Provides an interface for attaching and detaching `Observer` objects.
* **Observer:** Defines an updating interface for objects that should be notified of changes in a subject.
* **ConcreteSubject:** Stores state of interest to `ConcreteObserver` objects. Sends a notification to its observers when its state changes.
* **ConcreteObserver:** Maintains a reference to a `ConcreteSubject` object. Stores state that must remain consistent with the subject's. Implements the `Observer` updating interface to keep its state consistent with the subject's.

**☕ Java Note: The Modern Standard**
> A common approach in modern Java is utilizing generic interfaces rather than standard class inheritance, often relying on `Consumer<T>` or dedicated Listener interfaces to enable lambdas and method references.
```java
// Modern Observer via functional interfaces
public interface Subject<T> {
    void register(Consumer<T> observer);
    void notifyObservers(T event);
}
```

---

## Collaborations

```text
aConcreteSubject      aConcreteObserver     anotherConcreteObserver
       │                      │                       │
       │◄────── SetState() ───│                       │
       │                      │                       │
       │──────┐               │                       │
       │      │ Notify()      │                       │
       │◄─────┘               │                       │
       │                      │                       │
       │─────── Update() ────►│                       │
       │◄────── GetState() ───│                       │
       │                      │                       │
       │─────── Update() ────────────────────────────►│
       │◄────── GetState() ───────────────────────────│
       │                      │                       │
       ▼                      ▼                       ▼
```

*Diagram 3: A sequence of collaborations illustrating how the subject triggers a notification cascade, prompting observers to fetch the new state.*

* `ConcreteSubject` notifies its observers whenever a change occurs that could make its observers' state inconsistent with its own.
* After being informed of a change, a `ConcreteObserver` queries the subject for information to reconcile its state.

---

## Consequences (Trade-offs)
* **Abstract Coupling:** The subject only knows it has a list of observers conforming to the simple `Observer` interface. It doesn't know the concrete classes.
* **Support for Broadcast Communication:** Unlike ordinary requests, the notification that a subject sends needn't specify its receiver. The notification is broadcast automatically to all interested objects.
* **Unexpected Updates:** Because observers have no knowledge of each other, they can be blind to the ultimate cost of changing the subject. A seemingly innocuous operation on the subject may cause a cascade of updates to observers and their dependent objects.
* **State Retrieval Overhead:** If the `Update()` protocol provides no details about *what* changed, observers are forced to deduce the changes, potentially leading to inefficient state-checking.

---

## Implementation Hints & Modern Java Context
Several issues related to the implementation of the dependency mechanism are worth noting:

1. **Mapping Subjects to their Observers:** Instead of storing observer references directly in the subject (which costs space if there are many subjects but few observers), a centralized associative look-up (like a `HashMap`) can trade space for time.
2. **Push vs. Pull Models:** 
   * **Pull Model:** The subject sends a minimal notification, and observers request details explicitly afterward. (More flexible, but can be inefficient).
   * **Push Model:** The subject sends detailed information about the change (often as an Event object) directly to the observer.
3. **Encapsulating Complex Update Semantics (ChangeManager):** When dependency relationships are complex, an intermediary object can maintain them. A `ChangeManager` minimizes the work required to update observers and prevents redundant update loops (e.g., in a directed acyclic graph of dependencies).

```text
┌────────────────────┐     subjects ┌────────────────────────────────┐ observers    ┌─────────────────┐
│     Subject        │ ◄─────────── │ ChangeManager                  │ ───────────► │    Observer     │
├────────────────────┤              ├────────────────────────────────┤              ├─────────────────┤
│ Attach(Observer o)┐│              │ + Register(Subject, Observer)  │              │ Update(Subject) │ 
│ Detach(Observer)  ││ ───────────► │ + Unregister(Subject, Observer)│              └─────────────────┘
│ Notify()┐         ││ chman        │ + Notify()                     │
└─────────│─────────│┘              ├────────────────────────────────┤
          │         │               │ Subject-Observer mapping       │
  chman->Notify()   │               └────────────────────────────────┘
                    │                               ▲
       chman->Register(this, o)                     │
                                       ┌────────────┴───────────┐
                                       │                        │
                            ┌─────────────────────┐   ┌───────────────────┐
                            │ SimpleChangeManager │   │ DAGChangeManager  │
                            ├─────────────────────┤   ├───────────────────┤
                            │ ...                 │   │ ...               │
                            │ Notify()┐           │   │ Notify()┐         │
                            └─────────│───────────┘   └─────────│─────────┘
                                      │                         │
                      for all s in subjects:               mark all observers to update
                          for all o in s.observers:        update all marked observers
                              o->Update(s)    
```
*Diagram 4: Introducing a ChangeManager to mediate complex update semantics.*


**☕ Java Note: Modern ChangeManagers (Event Buses)**
> In modern Java ecosystems, the `ChangeManager` concept has evolved into "Event Buses" or "Application Contexts". Frameworks like Guava (`EventBus`) or Spring (`ApplicationEventPublisher`) act as powerful, decoupled intermediaries, removing the need for subjects to maintain subscriber lists at all.
```java
// Spring Framework ChangeManager equivalent
@Component
public class Publisher {
    @Autowired ApplicationEventPublisher publisher;
    public void doWork() {
        publisher.publishEvent(new CustomEvent(this, "Work Done"));
    }
}
```

---

## Known Uses & Java API Usage
* **Java Beans:** `java.beans.PropertyChangeListener` and `PropertyChangeSupport` represent the standard way to observe property mutations in Java objects.
* **Reactive Extensions (RxJava / Project Reactor):** The `Observable` and `Observer` (or `Flux` and `Subscriber`) pairs represent a highly evolved, asynchronous implementation of the Observer pattern used for handling data streams.
* **Java 9 Flow API:** `java.util.concurrent.Flow` introduces standard Reactive Streams interfaces (`Publisher`, `Subscriber`, `Subscription`) directly into the JDK, effectively superseding the deprecated `java.util.Observer`.
* **Spring Framework:** The `ApplicationListener` interface and `@EventListener` annotations allow components to automatically receive notifications when an `ApplicationEvent` is published.

---

## Related Patterns
* **Mediator:** By encapsulating complex update semantics, the `ChangeManager` acts as a Mediator between subjects and observers.
* **Singleton:** The `ChangeManager` may use the Singleton pattern to make it globally accessible.