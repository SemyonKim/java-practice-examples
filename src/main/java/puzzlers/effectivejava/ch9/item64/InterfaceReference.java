package puzzlers.effectivejava.ch9.item64;

import java.math.BigInteger;
import java.util.*;

/**
 * <h2>Refer to objects by their interfaces</h2>
 *
 * <p>
 * <b>Core Principle:</b> If appropriate interface types exist, you should favor them
 * over classes for declaring parameters, return values, variables, and fields. The only
 * time you should refer to an object's class is when creating it with a constructor.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Flexibility:</b> You can switch implementations easily (e.g., from {@code HashSet}
 * to {@code LinkedHashSet}) by changing only the constructor call.</li>
 * <li><b>Maintainability:</b> Surrounding code remains oblivious to the underlying
 * implementation details, reducing the risk of breaking logic when optimizations are made.</li>
 * <li><b>Design Discipline:</b> "Keeps you honest" by ensuring you don't accidentally
 * depend on implementation-specific methods that aren't part of the general contract.</li>
 * </ul>
 *
 * <h3>Limitations & Exceptions</h3>
 * <ul>
 * <li><b>Value Classes:</b> Classes like {@code String} or {@code BigInteger} rarely
 * have interfaces and are usually referred to by their class name.</li>
 * <li><b>Class-based Frameworks:</b> If the framework uses abstract base classes
 * (like {@code java.io.OutputStream}) instead of interfaces, use the base class.</li>
 * <li><b>Extra Functionality:</b> If you strictly require a method not present in
 * the interface (e.g., {@code PriorityQueue.comparator()}), you must refer to the class,
 * though this is rare.</li>
 * <li><b>Specific Contracts:</b> If your code depends on a specific implementation's
 * contract (like {@code LinkedHashSet}'s iteration order), you cannot swap it for
 * a {@code HashSet} even though both implement {@code Set}.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch8.item51 InterfaceParameters
 * @see java.util.Set
 * @see java.util.List
 */
public class InterfaceReference {

    /**
     * GOOD: Refers to the object by its interface.
     * This is flexible; we could swap LinkedHashSet for HashSet or TreeSet
     * without changing any other code in this method.
     */
    public void goodUsage() {
        // Only mention the class at the point of creation
        Set<String> names = new LinkedHashSet<>();
        processNames(names);
    }

    /**
     * BAD: Refers to the object by its implementation class.
     * This ties the variable 'names' to a specific implementation unnecessarily.
     */
    public void badUsage() {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        processNames(names);
    }

    /**
     * Appropriate Class-based reference: Value classes.
     */
    public String valueClassExample(BigInteger value) {
        return value.toString();
    }

    private void processNames(Set<String> set) {
        // Logic works regardless of the specific Set implementation
        for (String s : set) {
            System.out.println(s);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        InterfaceReference demo = new InterfaceReference();

        // 1. Swapping implementations:
        // Suppose we started with HashMap for performance
        Map<String, Integer> map = new HashMap<>();

        // Later, we realize we need predictable iteration order.
        // We only change this line:
        map = new LinkedHashMap<>();

        map.put("First", 1);
        map.put("Second", 2);

        // The rest of the program doesn't care that the implementation changed
        for (String key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }

        // 2. Specialized implementation: EnumMap
        // Better performance if keys are enums, but the type remains 'Map'
        enum Status { OPEN, CLOSED }
        Map<Status, String> statusMap = new EnumMap<>(Status.class);
        statusMap.put(Status.OPEN, "Working");
        System.out.println("Status: " + statusMap.get(Status.OPEN));
    }
}