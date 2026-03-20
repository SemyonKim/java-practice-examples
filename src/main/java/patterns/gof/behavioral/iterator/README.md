# Iterator - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](IteratorDemonstration.java)**

## Intent & Core Problem
Provide a mechanism to access the elements of an aggregate object (like a list or collection) sequentially without exposing its underlying internal structure or representation.

> **Also Known As:** Cursor

**☕ Java Note: The Language Integration**
> In modern Java, the Iterator pattern is so deeply ingrained that it is part of the language syntax itself. The `java.lang.Iterable<T>` interface and the enhanced `for` loop (for-each) were introduced in Java 5 specifically to abstract away explicit iterator manipulation, making this pattern one of the most frequently used—and implicitly leveraged—patterns in the Java ecosystem.

---

## Motivation & Real-World Analogy
An aggregate object, such as a List, needs to allow clients to access its elements without revealing its internal architecture (e.g., whether it uses arrays, linked nodes, or trees). Furthermore, you might want to traverse the aggregate in various ways (forward, backward, filtered) depending on the application's needs. Bloating the core List interface with operations for every conceivable traversal method violates the Single Responsibility Principle. Additionally, you may need multiple independent traversals active on the same list simultaneously.

The Iterator pattern resolves this by extracting the responsibility for access and traversal from the list and placing it into a separate `Iterator` object.

The Iterator class defines an interface to access list elements, tracking the current element and knowing how to compute the subsequent one.

### Object Interaction Diagram

```text
      +-----------------+
      |     Client      |
      +-----------------+
        |             |
        |             V
        |    +-------------------+
        |    |   ListIterator    |
        |    +-------------------+
        |    | First()           |<-- Resets traversal
        |    | Next()            |<-- Advances traversal
        |    | IsDone()          |<-- Checks bounds
        |    | CurrentItem()     |<-- Returns element
        |    +-------------------+
        |    | index             |<-- Internal state
        |    +-------------------+
        |             |
        V             V
+---------------------------+
|           List            |
+---------------------------+
| Count()                   |
| Append(Element)           |
| Remove(Element)           |
+---------------------------+
```
*Diagram 1: Interaction between a Client, a List, and its Iterator. The Iterator maintains its own `index` state, allowing multiple iterators to traverse the same List concurrently.*

**☕ Java Note: Separation of Concerns**
> By separating the traversal logic from the collection logic, Java Collections can offer uniform iteration over complex data structures like `HashMap` (via `entrySet().iterator()`) or `TreeSet` without the client ever needing to understand hashing algorithms or Red-Black trees.

To prevent the client from tightly coupling to a specific iterator implementation (like `ListIterator`), we use a factory method on the aggregate to instantiate the correct iterator.

### Iterator Instantiation Diagram

```text
+-----------+               +---------------------------------------+
|   Client  |-------------->|                 List                  |
+-----------+               +---------------------------------------+
                            | CreateIterator() -------------------+ |
                            +-------------------------------------|-+
                                                                  |
                                                                  V
                                                    return new ListIterator(this)
                                                                  |
                                                                  V
                            +---------------------------------------+
                            |            ListIterator               |
                            +---------------------------------------+
```
*Diagram 2: Polymorphic instantiation of an iterator. The Client only knows it receives an `Iterator`, while the `List` handles the concrete instantiation.*

---

## Applicability
Use the Iterator pattern when:
* You need to access the contents of an aggregate object without exposing its internal representation.
* You want to support multiple concurrent or distinct traversals of aggregate objects.
* You want to provide a uniform interface for traversing different aggregate structures (enabling polymorphic iteration).

---

## Structure & Participants

```text
+--------------+      +-------------------+      +-----------------+
|  Aggregate   |<-----|      Client       |----->|    Iterator     |
+--------------+      +-------------------+      +-----------------+ 
| CreateIter() |                                 | First()         |
+------+-------+                                 | Next()          |   
       ^                                         | IsDone()        |
       |                                         | CurrentItem()   |
       |                                         +--------+--------+
       |                                                  ^
       |                                                  |
       |                                                  |
+------+------------+                            +--------+----------+         
| ConcreteAggregate |<---------------------------| ConcreteIterator  |        
+-------------------+                            +-------------------+       
| CreateIter() -----+---+                        | First()           |       
+-------------------+   |                        | Next()            |
                        |                        | IsDone()          |
                        V                        | CurrentItem()     |
         return new ConcreteIterator(this)       +-------------------+                     
```
*Diagram 3: The general class structure of the Iterator pattern. It showcases the decoupling of the `Aggregate` interfaces from their concrete implementations, bridging them via the generic `Iterator` interface.*

### Participants
1.  **Iterator:** Defines an interface for accessing and traversing elements.
2.  **ConcreteIterator:** Implements the Iterator interface and keeps track of the current position in the traversal.
3.  **Aggregate:** Defines an interface for creating an Iterator object.
4.  **ConcreteAggregate:** Implements the Iterator creation interface to return an instance of the proper `ConcreteIterator`.

**☕ Java Note: GoF Mapping to Java**
> In Java, the participants map natively to standard libraries:
> * **Iterator** -> `java.util.Iterator<E>`
> * **Aggregate** -> `java.lang.Iterable<T>` or `java.util.Collection<E>`
> * **ConcreteAggregate** -> e.g., `ArrayList`, `HashSet`
> * **ConcreteIterator** -> e.g., `ArrayList.Itr`, `HashMap.KeyIterator` (Usually private inner classes).

---

## Collaborations
* A `ConcreteIterator` tracks the current object in the aggregate and can compute the succeeding object.
* The `Client` requests an iterator from the `Aggregate` and uses it to traverse the collection.

---

## Consequences & Trade-offs
1.  **Supports variations in traversal:** Complex aggregates can be traversed in multiple ways (e.g., inorder, preorder, postorder for trees). You can change the traversal algorithm simply by replacing the iterator instance.
2.  **Simplifies the Aggregate interface:** Moving the traversal interface to the Iterator removes bloat from the Aggregate's interface.
3.  **More than one traversal can be pending:** Because an iterator stores its own traversal state (like an index or a cursor node), you can iterate over the same collection simultaneously using different iterator instances.

---

## Implementation Hints & Modern Java Context

### 1. Internal vs. External Iterators
* **External Iterators (GoF standard):** The client controls the iteration by explicitly requesting the next element (e.g., `while(iterator.hasNext())`). This is highly flexible but requires more boilerplate.
* **Internal Iterators:** The iterator controls the traversal, and the client hands it an operation to perform on each element.

**☕ Java Note: Internal Iteration via Lambdas**
> Java 8 popularized internal iterators with the `forEach` method and the Streams API, accepting `Consumer<T>` functions.
```java
// External Iteration
Iterator<String> it = names.iterator();
while (it.hasNext()) { System.out.println(it.next()); }

// Internal Iteration (Modern Java)
names.forEach(System.out::println);
```

### 2. Robust Iterators
A significant danger with iterators is modifying the underlying aggregate while a traversal is active, which can lead to missed elements or out-of-bounds errors.

**☕ Java Note: Fail-Fast vs. Fail-Safe**
> Modern Java addresses this through "fail-fast" iterators (which throw `ConcurrentModificationException` if the collection is structurally modified during iteration) and "fail-safe" iterators (like those in `CopyOnWriteArrayList` or `ConcurrentHashMap` which iterate over a snapshot or tolerate concurrent modifications).

### 3. Encapsulation via Inner Classes
To grant the `ConcreteIterator` efficient access to the `ConcreteAggregate`'s internal data without exposing it publicly, modern Object-Oriented languages utilize internal class constructs.

**☕ Java Note: Private Inner Classes**
> Java heavily utilizes non-static inner classes for Iterators. An inner class holds an implicit reference to its enclosing instance, allowing it to directly access `private` fields (like an underlying array) seamlessly.
```java
public class CustomList<E> implements Iterable<E> {
    private E[] elements; // Private array
    
    @Override
    public Iterator<E> iterator() {
        return new Itr(); // Factory method
    }
    
    // Inner class has direct access to outer's 'elements'
    private class Itr implements Iterator<E> {
        int cursor = 0;
        public boolean hasNext() { return cursor < elements.length; }
        public E next() { return elements[cursor++]; }
    }
}
```

---

## Known Uses & Java API Usage
* **Java Collections Framework:** Every collection implementing `java.util.Collection` provides an `iterator()` method.
* **Java IO/NIO:** `java.nio.file.DirectoryStream` acts as an `Iterable` to iterate over entries in a directory securely.
* **JDBC (Java Database Connectivity):** The `ResultSet` acts as an iterator over database rows (using `next()`), maintaining a database cursor.
* **Spring Framework:** `ItemReader<T>` in Spring Batch functions similarly to an external iterator, sequentially returning domain objects from a data source.

---

## Related Patterns
* **Composite:** Iterators are frequently used to traverse recursive Composite structures.
* **Factory Method:** Polymorphic iterators rely on Factory Methods to instantiate the appropriate Iterator subclasses.
* **Memento:** Often used in conjunction with an Iterator. An iterator can use a Memento to capture the state of an iteration, enabling it to roll back or save traversal state internally.