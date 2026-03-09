package puzzlers.effectivejava.ch4.item18;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * <h2>Favor Composition Over Inheritance</h2>
 *
 * <p>Inheritance is a powerful tool for code reuse, but it can lead to fragile software
 * because it violates encapsulation. A subclass depends on the implementation details
 * of its superclass, which may change and break the subclass (the fragile base class problem).</p>
 *
 *
 *
 * <h3>Core Principle</h3>
 * Use inheritance only when a genuine <b>"is-a"</b> relationship exists between the
 * subclass and the superclass. In all other cases, use <b>composition</b>: give your
 * new class a private field that references an instance of the existing class.
 *
 * <h3>Advantages of Composition & Forwarding</h3>
 * <ul>
 * <li><b>Robustness:</b> The resulting class does not depend on the implementation details
 * of the existing class. Even if the existing class acquires new methods, the wrapper remains solid.</li>
 * <li><b>Flexibility:</b> A single wrapper class can be used to instrument any implementation
 * of an interface (e.g., {@code InstrumentedSet} can wrap {@code HashSet}, {@code TreeSet}, etc.).</li>
 * <li><b>Encapsulation:</b> Does not expose the internal flaws or API quirks of the component class.</li>
 * <li><b>Power:</b> Allows the Decorator pattern, where you "decorate" an instance with
 * additional functionality.</li>
 * </ul>
 *
 * <h3>Limitations & Disadvantages</h3>
 * <ul>
 * <li><b>The SELF Problem:</b> Wrapper classes are not suited for callback frameworks because
 * the wrapped object passes a reference to itself ({@code this}), bypassing the wrapper.</li>
 * <li><b>Boilerplate:</b> Writing forwarding methods can be tedious (though reusable
 * forwarding classes or libraries like Guava mitigate this).</li>
 * <li><b>Memory/Performance:</b> Minimal impact in practice, but involves an additional
 * object indirection and method call.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item19 DesignForInheritance
 * @see puzzlers.effectivejava.ch8.item50 MakeDefensiveCopies
 */
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    /**
     * Fixes the "double counting" problem found in inheritance.
     * Unlike an inherited HashSet, this doesn't care if the superclass
     * implementation of addAll calls add().
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    /**
     * Client usage demonstrating the Decorator pattern.
     */
    public static void main(String[] args) {
        // We can wrap ANY Set implementation
        InstrumentedSet<String> s = new InstrumentedSet<>(new java.util.HashSet<>());

        s.addAll(java.util.List.of("Snap", "Crackle", "Pop"));

        // Correctly returns 3, not 6
        System.out.println("Add count: " + s.getAddCount());
    }
}

/**
 * Reusable forwarding class.
 * This class implements the interface and forwards all calls to the underlying instance.
 */
class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) { this.s = java.util.Objects.requireNonNull(s); }

    public void clear()               { s.clear();            }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty();   }
    public int size()                 { return s.size();      }
    public Iterator<E> iterator()     { return s.iterator();  }
    public boolean add(E e)           { return s.add(e);      }
    public boolean remove(Object o)   { return s.remove(o);   }
    public boolean containsAll(Collection<?> c) { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
    public boolean removeAll(Collection<?> c) { return s.removeAll(c); }
    public boolean retainAll(Collection<?> c) { return s.retainAll(c); }
    public Object[] toArray()          { return s.toArray();  }
    public <T> T[] toArray(T[] a)      { return s.toArray(a); }

    @Override public boolean equals(Object o) { return s.equals(o);  }
    @Override public int hashCode()    { return s.hashCode(); }
    @Override public String toString() { return s.toString(); }
}