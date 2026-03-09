package puzzlers.effectivejava.ch2.item6;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * <h2>Avoid creating unnecessary objects</h2>
 *
 * <p>
 * <b>Core Principle:</b> Reuse a single object instead of creating a new functionally
 * equivalent one each time it is needed. This is especially critical for immutable
 * objects and "expensive" objects that require significant resources to initialize.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Performance:</b> Significant speed improvements by avoiding expensive initialization (e.g., Regex compilation).</li>
 * <li><b>Memory Efficiency:</b> Reduces garbage collection (GC) pressure by minimizing short-lived objects.</li>
 * <li><b>Code Clarity:</b> Naming cached objects (like a static Pattern) makes the code more readable than raw literals.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Defensive Copying Overrides:</b> Do not reuse an object if a defensive copy is required (Item 50). The penalty for a security hole is worse than a performance hit.</li>
 * <li><b>Object Pooling:</b> Avoid maintaining your own object pools for lightweight objects; the JVM's GC is usually more efficient.</li>
 * <li><b>Lazy Initialization Complexity:</b> Avoid lazy initialization unless the performance gain is measurable (Item 67).</li>
 * </ul>
 *
 * <h3>Autoboxing Considerations</h3>
 * <ul>
 * <li><b>Performance Pitfall:</b> Mixing primitives and boxed primitives (e.g., {@code Long} vs {@code long}) can lead to millions of unintentional object creations.</li>
 * <li><b>Recommendation:</b> Prefer primitives to boxed primitives and be wary of unintentional autoboxing in loops.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch8.item50 DefensiveCopying
 * @see puzzlers.effectivejava.ch9.item61 PrimitivesVsBoxed
 * @see puzzlers.effectivejava.ch11.item83 LazyInitialization
 */
public class ObjectReuse {

    // 1. Caching an expensive object: Pattern compilation is heavy.
    // By making it static final, we compile the state machine only once.
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})"
                    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    /**
     * Reuses the pre-compiled Pattern instance.
     * Much faster than String.matches() which re-compiles the pattern every time.
     */
    public static boolean isRomanNumeral(String s) {
        return ROMAN.matcher(s).matches();
    }

    /**
     * Demonstrates the danger of autoboxing.
     * Declaring 'sum' as Long (boxed) instead of long (primitive)
     * creates ~2^31 unnecessary Long instances.
     */
    public static long calculateSum() {
        long sum = 0L; // Correct: primitive. Using 'Long' would be hideously slow.
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            sum += i;
        }
        return sum;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Example: String literal reuse
        String s1 = "bikini"; // Reuses instance from string constant pool
        String s2 = "bikini"; // Same instance as s1

        // Example: Factory vs Constructor
        // Boolean b = new Boolean("true"); // Deprecated and creates new object
        Boolean b = Boolean.valueOf("true"); // Better: Reuses Boolean.TRUE

        // Example: Regex reuse
        System.out.println("Is 'MCXI' Roman? " + isRomanNumeral("MCXI"));

        // Example: Map Views (Adapters)
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        Set<String> keys1 = map.keySet();
        Set<String> keys2 = map.keySet();
        // keys1 and keys2 are actually the same object (view)
        System.out.println("Are key sets the same object? " + (keys1 == keys2));

        // Example: Autoboxing performance
        long start = System.currentTimeMillis();
        calculateSum();
        System.out.println("Sum calculation took: " + (System.currentTimeMillis() - start) + "ms");
    }
}