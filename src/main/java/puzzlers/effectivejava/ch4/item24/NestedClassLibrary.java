package puzzlers.effectivejava.ch4.item24;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * <h2>Favor static member classes over nonstatic</h2>
 *
 * <p>
 * <b>Core Principle:</b> If a nested class does not require access to an enclosing
 * instance, always declare it as {@code static}. Nonstatic member classes (inner classes)
 * maintain an implicit reference to the outer instance, wasting space and risking memory leaks.
 * </p>
 *
 * <h3>Advantages of Static Member Classes</h3>
 * <ul>
 * <li><b>Efficiency:</b> No hidden reference to the outer instance means less memory overhead and faster construction.</li>
 * <li><b>Memory Safety:</b> Prevents the outer instance from being "pinned" in memory, avoiding memory leaks (Item 7).</li>
 * <li><b>Independence:</b> Can exist in isolation from an instance of the enclosing class.</li>
 * </ul>
 *
 * <h3>Advantages of Nonstatic Member Classes (Inner Classes)</h3>
 * <ul>
 * <li><b>Adapter Pattern:</b> Ideal for defining views (e.g., {@code Map.keySet}) or iterators that
 * naturally belong to a specific instance of the outer class.</li>
 * <li><b>Implicit Context:</b> Allows direct access to the outer instance's methods and fields using {@code Outer.this}.</li>
 * </ul>
 *
 * <h3>Limitations & Evolution of Anonymous Classes</h3>
 * <ul>
 * <li><b>Pre-Java 16:</b> Anonymous classes could not contain static members (except constant variables).</li>
 * <li><b>Java 16+ & JDK 25:</b> These restrictions are lifted. Anonymous and local classes
 * can now declare static members (fields, methods, records).</li>
 * <li><b>Readability:</b> Anonymous classes must be kept short (approx. 10 lines) or they harm maintainability.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item7 MemoryLeaks
 * @see puzzlers.effectivejava.ch4.item20 InterfacesVsAbstractClasses
 * @see puzzlers.effectivejava.ch7.item42 LambdasOverAnonymous
 */
public class NestedClassLibrary {

    // 1. Static Member Class - Best as a public helper
    public static class Operation {
        public static final String PLUS = "+";
        public void execute() { System.out.println("Executing Static Operation"); }
    }

    // 2. Private Static Member Class - Represents a component
    private static class Entry {
        Object key, value;
        Entry(Object k, Object v) { key = k; value = v; }
    }

    // 3. Nonstatic Member Class - Ideal for Iterators/Adapters
    public class MyCollection extends AbstractSet<String> {
        @Override
        public Iterator<String> iterator() {
            return new MyIterator(); // Automatically associated with 'this' instance
        }

        @Override
        public int size() { return 0; }

        private class MyIterator implements Iterator<String> {
            @Override public boolean hasNext() { return false; }
            @Override public String next() { return null; }
        }
    }

    // --- Client Usage & Feature Demonstration ---

    public static void main(String[] args) {
        // Static member classes can be instantiated without the outer class
        NestedClassLibrary.Operation op = new NestedClassLibrary.Operation();
        op.execute();

        // Nonstatic member classes REQUIRE an outer instance
        NestedClassLibrary library = new NestedClassLibrary();
        MyCollection collection = library.new MyCollection();

        // 4. Anonymous Class Demonstration
        Runnable r = new Runnable() {
            // NOTE: In JDK 25, static members in anonymous classes are perfectly legal
            static final int VERSION = 25;
            static void printVersion() { System.out.println("JDK " + VERSION); }

            @Override
            public void run() {
                printVersion();
                System.out.println("Running anonymous class logic...");
            }
        };
        r.run();

        // 5. Local Class Demonstration
        class LocalLogger {
            void log(String msg) { System.out.println("Local: " + msg); }
        }
        new LocalLogger().log("Finished demonstration.");
    }
}