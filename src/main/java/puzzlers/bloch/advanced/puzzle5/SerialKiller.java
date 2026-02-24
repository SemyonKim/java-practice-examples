package puzzlers.bloch.advanced.puzzle5;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Serial Killer: The Deserialization Hazard
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 91
 * <p>
 * Problem:
 * Why does a class that works perfectly in normal operation throw a
 * {@code NullPointerException} or behave inconsistently only after
 * being serialized and then deserialized?
 * <p>
 * Explanation:
 * - When an object graph is being deserialized, the {@code ObjectInputStream}
 * creates objects in the order they appear in the stream.
 * - If a {@code HashSet} is part of that stream, its {@code readObject}
 * method will attempt to re-insert all its elements to rebuild the internal
 * hash table.
 * - To insert an element, the {@code HashSet} must call that element's
 * {@code hashCode()} and {@code equals()} methods.
 * - **The Trap:** If the element is the very object currently being
 * deserialized, its fields may still be {@code null} or uninitialized
 * because its own {@code readObject} hasn't finished yet.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Serialization:** A {@code SuperClass} object containing a {@code HashSet}
 * (which in turn contains the {@code SuperClass} object) is saved to a stream.
 * 2. **Deserialization Starts:** {@code ObjectInputStream} starts reading
 * the {@code SuperClass} object.
 * 3. **Collection Reconstruction:** The {@code HashSet} inside {@code SuperClass}
 * begins its own {@code readObject} process.
 * 4. **Premature Method Call:** The {@code HashSet} calls {@code element.hashCode()}.
 * Since {@code element} is the {@code SuperClass} instance, it executes its
 * {@code hashCode()} method while its instance fields are still null.
 * 5. **Failure:** The {@code hashCode()} method tries to access a field,
 * resulting in a {@code NullPointerException}.
 * <p>
 * The Fixes:
 * 1. **Avoid Circularities:** Redesign the data structure to avoid having
 * a collection contain its own owner.
 * 2. **Custom readObject:** Implement a custom {@code readObject} in the
 * owner class that populates the {@code HashSet} *after* {@code defaultReadObject()}
 * has finished initializing the fields.
 * 3. **Check for Null:** Make {@code hashCode()} and {@code equals()}
 * resilient to uninitialized (null) states, though this is a "band-aid" fix.
 * <p>
 * Lesson:
 * - An object is not "fully born" until its {@code readObject} method returns.
 * - Never allow {@code this} to escape during construction or
 * deserialization, especially into hash-based collections that will
 * immediately call methods on it.
 */
public class SerialKiller implements Serializable {
    private final Set<SerialKiller> set = new HashSet<>();
    private String name;

    public SerialKiller(String name) {
        this.name = name;
        set.add(this); // Circular reference
    }

    @Override
    public int hashCode() {
        return name.hashCode(); // Potential NPE during deserialization!
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SerialKiller)) return false;
        return name.equals(((SerialKiller) obj).name);
    }

    // FIX: Manual reconstruction of the set after name is initialized
    /*
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        // If 'set' was transient, you would manually add 'this' back here
    }
    */
}