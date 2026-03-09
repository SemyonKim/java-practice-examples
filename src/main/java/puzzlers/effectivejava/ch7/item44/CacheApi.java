package puzzlers.effectivejava.ch7.item44;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * <h2>Favor the use of standard functional interfaces</h2>
 *
 * <p>
 * <b>Core Principle:</b> Instead of creating custom functional interfaces, use the
 * standard ones provided in {@code java.util.function}. This reduces the conceptual
 * surface area of your API and provides interoperability through standard default methods.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Interoperability:</b> Standard interfaces like {@code Predicate} come with
 * useful default methods (e.g., {@code and}, {@code or}, {@code negate}) that custom
 * interfaces lack.</li>
 * <li><b>Reduced Learning Curve:</b> Programmers already know the standard interfaces,
 * making your API easier to digest.</li>
 * <li><b>Performance:</b> Standard libraries provide primitive variants (e.g., {@code IntPredicate})
 * to avoid the heavy performance cost of auto-boxing.</li>
 * </ul>
 *
 * <h3>Disadvantages / When to Write Your Own</h3>
 * <ul>
 * <li><b>Missing Parameters:</b> If you need an interface with more than two arguments
 * (e.g., a Tri-Predicate), you must write your own.</li>
 * <li><b>Lack of Descriptive Name:</b> Sometimes a custom name provides vital documentation
 * (e.g., {@code Comparator} vs. {@code ToIntBiFunction}).</li>
 * <li><b>Specific Contract:</b> If the interface requires a strict general contract
 * that the standard interface doesn't imply.</li>
 * <li><b>Custom Default Methods:</b> If the interface requires specialized logic
 * beyond what the standard library provides.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item21 DesignInterfacesForPosterity
 * @see puzzlers.effectivejava.ch7.item42 Lambdas
 * @see puzzlers.effectivejava.ch8.item52 UseOverloadingJudiciously
 * @see puzzlers.effectivejava.ch9.item61 PreferPrimitivesToBoxed
 */
public class CacheApi {

    /**
     * UNNECESSARY: A custom functional interface that mirrors BiPredicate.
     * Avoid this unless a very specific contract is needed.
     */
    @FunctionalInterface
    public interface EldestEntryRemovalFunction<K, V> {
        boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
    }

    /**
     * PREFERRED: Using a standard BiPredicate.
     * This is interoperable and familiar to Java developers.
     */
    public static <K, V> Map<K, V> createCache(int capacity,
                                               BiPredicate<Map<K, V>, Map.Entry<K, V>> removalPolicy) {

        return new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return removalPolicy.test(this, eldest);
            }
        };
    }

    // --- Basic Functional Interface Categories ---
    // 1. Operator: T -> T (Unary) or (T, T) -> T (Binary)
    // 2. Predicate: T -> boolean
    // 3. Function: T -> R
    // 4. Supplier: () -> T
    // 5. Consumer: T -> void

    // --- Client Usage ---

    public static void main(String[] args) {
        // Using standard Predicate with default method 'negate'
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        // Using our Cache API with a standard BiPredicate lambda
        Map<String, String> cache = createCache(100,
                (map, eldest) -> map.size() > 100
        );

        cache.put("Key1", "Value1");

        System.out.println("Cache initialized with capacity logic using BiPredicate.");
        System.out.println("Is 'Hello' not empty? " + isNotEmpty.test("Hello"));
    }
}