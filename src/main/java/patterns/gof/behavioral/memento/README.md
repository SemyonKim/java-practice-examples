# Memento - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](MementoDemonstration.java)**

## Intent & Core Problem
Capture and externalize an object's internal state without violating encapsulation, allowing the object to be restored to this exact state at a later time.

> **Also Known As:** Token

**☕ Java Note:**
> In modern Java, the Memento pattern is frequently implemented using `record` classes for immutable state snapshots or nested classes to maintain strict encapsulation boundaries. It provides a robust alternative to `java.io.Serializable` when you need fine-grained, memory-efficient undo/redo mechanisms without exposing internal variables.

---

## Motivation & Real-World Analogy
It is often necessary to record an object's internal state to implement checkpoints or undo mechanisms, allowing users to back out of tentative operations or recover from errors. However, objects typically encapsulate their state, making it inaccessible to external entities. Exposing this state directly would violate encapsulation, compromising the application's reliability and extensibility.

Consider a graphical editor connecting visual objects (e.g., a line connecting two rectangles). When a user moves a rectangle, the line stretches to maintain the connection. If the user undoes the move, the rectangles and the line must revert to their exact previous positions. The editor usually encapsulates these actions in Command objects (like a `MoveCommand`).

The `MoveCommand` needs to undo its effects, requiring it to store the original positions. However, the exact state mechanisms are internal to the layout solver (e.g., `ConstraintSolver`). Allowing `MoveCommand` to directly access and store the `ConstraintSolver`'s internal variables breaks encapsulation.

To solve this, the `ConstraintSolver` can create a **Memento**—a specialized object storing a snapshot of its internal state.

**Diagram 1: Application Structure using Memento**
```text
  ┌───────────────────┐               ┌────────────────────┐
  │    MoveCommand    │               │  ConstraintSolver  │
  ├───────────────────┤               ├────────────────────┤
  │ state             │ ────────────> │ Solve()            │
  │ Execute()         │               │ CreateMemento()    │
  │ Unexecute()       │               │ SetMemento(state)  │
  └───────────────────┘               └────────────────────┘
                                                │
                                                │
                                                ▼
                                       ┌───────────────────┐
                                       │   SolverState     │
                                       └───────────────────┘
```
*Description:* 
1. *The `MoveCommand` requests a memento from the `ConstraintSolver` as a side-effect of the `Execute()` operation.*
2. *The `ConstraintSolver` creates and returns a memento, an instance of a class `SolverState` in this case. A `SolverState` memento contains data structures that describe the current state of the `ConstraintSolver`'s internal equations and variables.*
3. *Later when the user undoes the move operation, the editor gives the `SolverState` back to the `ConstraintSolver`.*
4. *Based on the information in the `SolverState`, the `ConstraintSolver` changes its internal structures to return its equations and variables to their exact previous state.*

**☕ Java Supplement: The Interface Segregation Approach**
> In Java, a common way to enforce encapsulation with Mementos is using a marker interface (e.g., `public interface SolverState {}`). The `MoveCommand` only knows about the empty interface, while the `ConstraintSolver` knows about the concrete implementation.
> ```java
> public interface SolverState { /* Narrow interface for Caretaker */ }
> 
> public class ConstraintSolver {
>     private record Snapshot(int x, int y) implements SolverState {} // Wide interface for Originator
>     
>     public SolverState createMemento() { return new Snapshot(this.x, this.y); }
>     public void restore(SolverState state) { 
>         Snapshot snap = (Snapshot) state; 
>         // restore logic... 
>     }
> }
> ```

---

## Applicability
Use the Memento pattern when:
* A snapshot of (some portion of) an object's state must be saved so it can be restored later.
* Providing a direct interface to obtain this state would expose implementation details and break the object's encapsulation.

---

## Structure & Participants

**Diagram 3: Common Memento Structure**
```text
  ┌────────────────────────┐ memento           ┌────────────────────────┐
  │       Caretaker        │<>---------------> │        Memento         │
  ├────────────────────────┤                   ├────────────────────────┤
  │                        │                   │ state                  │
  └────────────────────────┘                   ├────────────────────────┤
                                               │ GetState()             │
                                               │ SetState()             │
                                               └────────────────────────┘
                                                            ^
                                                            │
  ┌────────────────────────┐                                │
  │       Originator       │--------------------------------+
  ├────────────────────────┤                                
  │ state                  │                                
  ├────────────────────────┤                                
  │ SetMemento(Memento m) -│---> state = m.GetState() 
  │ CreateMemento() -------│---> return new Memento(state)
  └────────────────────────┘
```
*Description: The `Originator` creates and consumes `Memento` objects. The `Caretaker` stores the `Memento` but cannot interact with its internal state (`GetState`/`SetState` are hidden from the Caretaker).*

### Participants
1. **Memento** (`SolverState`):
   * Stores internal state of the `Originator` object.
   * Protects against access by objects other than the originator. Mementos effectively have two interfaces: a *narrow interface* for the Caretaker (allowing it to be passed around), and a *wide interface* for the Originator (allowing access to the data needed to restore state).
2. **Originator** (`ConstraintSolver`):
   * Creates a memento capturing a snapshot of its current internal state.
   * Uses the memento to restore its internal state.
3. **Caretaker** (`MoveCommand`):
   * Responsible for the memento's safekeeping.
   * Never operates on or examines the contents of a memento.

---

## Collaborations

**Diagram 4: Memento Sequence Diagram**
```text
    aCaretaker                    anOriginator                  aMemento
        │                              │                            │
        │       CreateMemento()        │                            │
        ├─────────────────────────────►│                            │
        │                              │──┐ new Memento()           │
        │                              │  ├────────────────────────►│
        │                              │◄─┘                         │
        │                              │                            │
        │                              │     SetState(...)          │
        │                              │───────────────────────────►│
        │◄─────────────────────────────│                            │
        │                              │                            │
        │                              │                            │
        │                              │                            │
     (modifies                         │                            │
    originator)                        │                            │
        │                              │                            │
        │                              │                            │
        │                              │                            │
        │       SetMemento(aMemento)   │                            │
        ├─────────────────────────────►│                            │
        │                              │     GetState()             │
        │                              │───────────────────────────►│
        │                              │◄───────────────────────────│
        │                              │                            │
        ▼                              ▼                            ▼
```
*Description: The sequence illustrates the `Caretaker` requesting a `Memento`, performing operations that change the `Originator`'s state, and finally rolling back those changes by passing the `Memento` back to the `Originator`.*

---

## Consequences (Trade-offs)
* **Preserves Encapsulation Boundaries:** It shields other objects from potentially complex Originator internals, maintaining high cohesion and loose coupling.
* **Simplifies the Originator:** The Originator delegates the storage burden to the Caretaker, rather than managing multiple versions of its state internally.
* **High Storage Costs:** Mementos might incur significant overhead if the Originator must copy large amounts of data, or if clients create/return mementos frequently.
* **Hidden Costs in Caretaking:** A Caretaker is responsible for deleting Mementos. However, it usually has no idea how much state is stored inside. Lightweight Caretakers might accidentally consume vast amounts of memory.

**☕ Java Memory & Performance Note:**
> To mitigate high storage costs in Java, consider using **incremental mementos** (storing only deltas/changes) or applying the **Flyweight** pattern if multiple mementos share identical state properties. Additionally, be cautious with large Memento collections (like deep Undo stacks); they can lead to memory leaks or excessive Garbage Collection overhead if not bounded (e.g., using a fixed-size `Deque`).

---

## Implementation Hints & Modern Java Context
1.  **Language Support (Nested Classes):** The GoF book mentions C++ `friend` classes to allow the Originator access to the Memento's wide interface while keeping it hidden from the Caretaker. In Java, this is perfectly handled by **Static Nested Classes** or package-private classes.
2.  **Immutability:** A standard best practice in modern Java is to make Mementos completely immutable. Java 16+ `record` types are excellent for this, guaranteeing that once a snapshot is created, it cannot be tampered with.

**☕ Java Implementation Example:**
> ```java
> public class Originator {
>     private String state;
>     
>     // The Caretaker only sees this empty interface
>     public interface Memento {} 
>     
>     // The Originator knows the concrete Record
>     private record MementoSnapshot(String savedState) implements Memento {}
>     
>     public Memento save() { return new MementoSnapshot(state); }
>     public void restore(Memento m) { this.state = ((MementoSnapshot) m).savedState(); }
> }
> ```

---

## Known Uses & Java API Usage
* **Java UI Frameworks (Swing):** The `javax.swing.undo.UndoManager` and `UndoableEdit` act as a heavily specialized Command/Memento hybrid, capturing the state of UI components (like text areas) before and after mutations.
* **JavaServer Faces (JSF):** The `StateHelper` and component state-saving mechanisms (`saveState()` / `restoreState()`) heavily rely on the Memento structure to preserve view states across HTTP requests without exposing the raw component hierarchies.
* **Serialization:** While `java.io.Serializable` can technically be used to implement Mementos (by deeply cloning an object into a byte array), it's generally discouraged for localized undo/redo due to performance overhead and reflection-based security constraints.

---

## Related Patterns
* **Command:** Commands can use Mementos to maintain state for undoable operations.
* **Iterator:** Mementos can be used for iteration (storing the current traversal state of a collection without breaking its encapsulation).