package puzzlers.effectivejava.ch8.item52;

import java.util.*;

/**
 * <h2>Part 1: Use overloading judiciously</h2>
 * {@link OverloadingNuances Part 2}
 *
 * <p>
 * <b>Core Principle:</b> Selection among overloaded methods is <b>static</b> (chosen at
 * compile-time based on the compile-time type of parameters), while selection among
 * overridden methods is <b>dynamic</b> (chosen at runtime based on the actual object
 * type). To avoid confusing behavior, avoid exporting two overloadings with the same
 * number of parameters unless the parameter types are "radically different."
 * </p>
 *
 * <h3>Advantages of Judicious Use</h3>
 * <ul>
 * <li><b>Predictability:</b> By avoiding confusing overloadings, you ensure that
 * developers aren't surprised by which method is actually executed.</li>
 * <li><b>API Clarity:</b> Using distinct names (e.g., {@code writeInt}, {@code writeLong})
 * instead of overloading {@code write} makes the API more self-documenting.</li>
 * <li><b>Safety with Lambdas:</b> Avoiding overloading with different functional
 * interfaces in the same position prevents "inexact method reference" compilation errors.</li>
 * </ul>
 *
 * <h3>Limitations & Pitfalls</h3>
 * <ul>
 * <li><b>Static Dispatch:</b> Overloading does not support polymorphism. Passing a
 * {@code HashSet} as a {@code Collection} variable to an overloaded method will
 * always trigger the {@code Collection} variant.</li>
 * <li><b>Autoboxing/Generics Confusion:</b> Types that were once "radically different"
 * (like {@code int} and {@code Object}) now clash due to autoboxing (e.g.,
 * {@code List.remove(int)} vs. {@code List.remove(E)}).</li>
 * <li><b>Constructor Constraints:</b> Since constructors cannot have different names,
 * overloading is inevitable; use static factories (Item 1) to mitigate this.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch8.item53 Varargs
 * @see puzzlers.effectivejava.ch9.item68 NamingConventions
 */
public class CollectionClassifier {

    // --- The "Broken" Overloading Way ---

    public static String classifyOverloaded(Set<?> s) { return "Set"; }
    public static String classifyOverloaded(List<?> lst) { return "List"; }
    public static String classifyOverloaded(Collection<?> c) { return "Unknown Collection"; }

    // --- The Correct "Instanceof" Way (Single Method) ---

    public static String classify(Collection<?> c) {
        return c instanceof Set ? "Set" :
                c instanceof List ? "List" : "Unknown Collection";
    }

    // --- Overriding (Dynamic Dispatch) for comparison ---

    static class Wine { String name() { return "wine"; } }
    static class SparklingWine extends Wine { @Override String name() { return "sparkling wine"; } }

    // --- Client Usage ---

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<Integer>(),
                new HashMap<String, String>().values()
        };

        System.out.println("--- Broken Overloading (Static Dispatch) ---");
        for (Collection<?> c : collections) {
            // This prints "Unknown Collection" 3 times because the compile-time
            // type is always Collection<?>.
            System.out.println(classifyOverloaded(c));
        }

        System.out.println("\n--- Correct Method (Explicit Check) ---");
        for (Collection<?> c : collections) {
            System.out.println(classify(c));
        }

        System.out.println("\n--- Overriding (Dynamic Dispatch) ---");
        List<Wine> wines = List.of(new Wine(), new SparklingWine());
        for (Wine w : wines) {
            // This works as expected (prints wine, then sparkling wine)
            // because overriding is determined at runtime.
            System.out.println(w.name());
        }

        System.out.println("\n--- The List.remove(i) Autoboxing Trap ---");
        List<Integer> list = new ArrayList<>();
        for (int i = -3; i < 3; i++) list.add(i); // [-3, -2, -1, 0, 1, 2]

        for (int i = 0; i < 3; i++) {
            // list.remove(i) uses remove(int index), not remove(Object element)!
            list.remove((Integer) i); // Correct: Cast forces remove(E)
        }
        System.out.println("List after proper removals: " + list);
    }
}