package puzzlers.effectivejava.ch7.item43;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h2>Prefer method references to lambdas</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use method references where they result in shorter, clearer code.
 * If a lambda is too complex, extract its logic into a named method and use a
 * method reference to that new method.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Succinctness:</b> Eliminates parameter boilerplate (e.g., {@code Integer::sum}
 * instead of {@code (count, incr) -> count + incr}).</li>
 * <li><b>Readability:</b> Directs the reader's attention to the name of the operation
 * rather than the mechanics of passing arguments.</li>
 * <li><b>Maintainability:</b> Encourages refactoring complex lambdas into documented,
 * named methods.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Parameter Documentation:</b> If the lambda's parameter names provide useful
 * context/documentation, a lambda may be more readable than a method reference.</li>
 * <li><b>Class Name Verbosity:</b> If the method is in a class with a very long name
 * and resides in the same scope, the lambda might actually be shorter (e.g.,
 * {@code () -> action()} vs {@code LongClassName::action}).</li>
 * <li><b>Clarity in Identity:</b> For simple operations like the identity function,
 * {@code x -> x} is often clearer than {@code Function.identity()}.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item42 Lambdas
 * @see puzzlers.effectivejava.ch7.item44 StandardFunctionalInterfaces
 * @see puzzlers.effectivejava.ch7.item45 Streams
 */
public class MethodReferenceDemo {

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        String key = "effective-java";

        // 1. Static Method Reference
        // Lambda: (count, incr) -> Integer.sum(count, incr)
        map.merge(key, 1, Integer::sum);

        // 2. Bound Instance Method Reference
        // The receiver (Instant.now()) is specified in the reference
        Instant then = Instant.now();
        Function<Instant, Boolean> isAfterThen = then::isAfter;

        // 3. Unbound Instance Method Reference
        // The receiver is the first parameter of the functional interface
        // Lambda: str -> str.toLowerCase()
        Function<String, String> toLower = String::toLowerCase;

        // 4. Class Constructor Reference
        // Lambda: () -> new TreeMap<>()
        Supplier<Map<String, Integer>> mapFactory = TreeMap::new;

        // 5. Array Constructor Reference
        // Lambda: len -> new int[len]
        Function<Integer, int[]> arrayFactory = int[]::new;

        clientUsage(map, key);
    }

    /**
     * Demonstrates how method references improve readability in a practical context.
     */
    private static void clientUsage(Map<String, Integer> map, String key) {
        // Using a method reference to a custom private method
        // This is the "out" if a lambda gets too complex
        map.merge(key, 1, MethodReferenceDemo::customIncrement);

        System.out.println("Key count for '" + key + "': " + map.get(key));
    }

    private static Integer customIncrement(Integer current, Integer increment) {
        // In a real scenario, this could be complex logic
        return current + increment;
    }
}