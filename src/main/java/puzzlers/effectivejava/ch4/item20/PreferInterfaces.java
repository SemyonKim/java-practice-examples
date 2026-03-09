package puzzlers.effectivejava.ch4.item20;

import java.util.*;

/**
 * <h2>Prefer interfaces to abstract classes</h2>
 *
 * <p><b>Core Principle:</b> Interfaces are the superior mechanism for defining types that allow multiple
 * implementations. They provide flexibility by allowing nonhierarchical type frameworks, enabling mixins,
 * and avoiding the "single inheritance" restriction of abstract classes. Use <b>Skeletal Implementations</b>
 * (Abstract classes implementing the interface) to provide the implementation assistance of abstract
 * classes without the structural constraints.</p>
 *
 * <h3>Advantages:</h3>
 * <ul>
 * <li><b>Hierarchy Flexibility:</b> Existing classes can be easily retrofitted to implement new interfaces
 * without disrupting their position in the class hierarchy.</li>
 * <li><b>Mixin Support:</b> Interfaces are ideal for defining "mixins"—optional behaviors (like {@link Comparable})
 * that a class can "mix in" to its primary type.</li>
 * <li><b>Nonhierarchical Frameworks:</b> Allows for types that don't fit into a rigid tree, such as a
 * {@code SingerSongwriter} extending both {@code Singer} and {@code Songwriter}.</li>
 * <li><b>Safe Enhancements:</b> Interfaces enable the Wrapper Class idiom (Item 18), providing a safer
 * alternative to inheritance for adding functionality.</li>
 * <li><b>Skeletal Implementations:</b> You can combine the power of interfaces with the ease of
 * abstract classes by providing an "AbstractInterface" (e.g., {@link AbstractList}) to help implementors.</li>
 * </ul>
 *
 * <h3>Disadvantages / Limitations:</h3>
 * <ul>
 * <li><b>Object Method Restrictions:</b> Default methods cannot override {@code Object} methods
 * like {@code equals}, {@code hashCode}, or {@code toString}.</li>
 * <li><b>State Management:</b> Interfaces cannot contain instance fields or nonpublic static members
 * (except private static methods).</li>
 * <li><b>External Control:</b> You cannot add default methods to an interface you do not own.</li>
 * <li><b>Skeletal Evolution:</b> While interfaces are easier to implement, abstract classes are
 * historically easier to evolve (though default methods have significantly closed this gap).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item18 FavorCompositionOverInheritance
 * @see puzzlers.effectivejava.ch4.item19 DesignForInheritanceOrProhibitIt
 */
public class PreferInterfaces {

    // 1. Nonhierarchical type framework example
    public interface Singer {
        void sing();
    }

    public interface Songwriter {
        void compose();
    }

    /**
     * Interfaces allow combining multiple types into a new one.
     */
    public interface SingerSongwriter extends Singer, Songwriter {
        void strum();
    }

    // 2. Skeletal Implementation example (The Template Method Pattern)
    public interface SimpleEntry<K, V> {
        K getKey();
        V getValue();
        V setValue(V value);
        // equals, hashCode, and toString are specified in the contract
    }

    /**
     * Skeletal implementation provides the implementation assistance.
     * It handles the Object methods that interfaces cannot.
     */
    public abstract static class AbstractSimpleEntry<K, V> implements SimpleEntry<K, V> {
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof SimpleEntry)) return false;
            SimpleEntry<?, ?> e = (SimpleEntry<?, ?>) o;
            return Objects.equals(e.getKey(), getKey()) &&
                    Objects.equals(e.getValue(), getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }
    }

    /**
     * Client usage demonstrating the "Adapter" pattern via skeletal implementation.
     */
    public static void clientUsage() {
        // Example of using a skeletal implementation (AbstractList) to adapt an array to a List
        int[] numbers = {1, 2, 3, 4, 5};

        List<Integer> list = new AbstractList<>() {
            @Override
            public Integer get(int index) {
                return numbers[index]; // Autoboxing
            }

            @Override
            public int size() {
                return numbers.length;
            }
        };

        System.out.println("Adapted List: " + list);

        // Example of a class implementing multiple interfaces (Singer and Songwriter)
        class Musician implements SingerSongwriter {
            public void sing() { System.out.println("Singing..."); }
            public void compose() { System.out.println("Composing..."); }
            public void strum() { System.out.println("Strumming..."); }
        }

        Musician m = new Musician();
        m.sing();
        m.strum();
    }
}