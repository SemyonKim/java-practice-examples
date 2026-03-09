package puzzlers.effectivejava.ch4.item15;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <h2>Minimize the accessibility of classes and members</h2>
 *
 * <p>
 * <b>Core Principle:</b> Make each class or member as inaccessible as possible.
 * Information hiding (encapsulation) decouples components, allowing them to be
 * developed, tested, and optimized in isolation.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Decoupling:</b> Components can be developed in parallel and modified
 * without affecting others.</li>
 * <li><b>Ease of Maintenance:</b> Faster debugging and lower risk of harming
 * existing clients when replacing components.</li>
 * <li><b>Performance Tuning:</b> Allows optimization of specific components
 * without impacting the correctness of the overall system.</li>
 * <li><b>Software Reuse:</b> Loosely coupled components are easier to extract
 * and use in different contexts.</li>
 * </ul>
 *
 * <h3>Limitations / Warnings</h3>
 * <ul>
 * <li><b>API Lock-in:</b> Public and protected members of a public class are
 * part of the exported API and must be supported forever to maintain compatibility.</li>
 * <li><b>Overriding Restriction:</b> A method in a subclass cannot have a more
 * restrictive access level than the method it overrides in the superclass (Liskov
 * Substitution Principle).</li>
 * <li><b>The Mutable Array Trap:</b> Public static final arrays are a security
 * hole because array contents are always mutable, even if the reference is final.</li>
 * <li><b>Testing Temptation:</b> Raising access for testing is acceptable only
 * up to "package-private"; it should never leak into the public API.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item16 Accessors
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item24 NestedClasses
 * @see puzzlers.effectivejava.ch12.item86 Serialization
 */
public class AccessControlExample {

    // 1. Constants: Public static final is okay for primitives/immutable objects
    public static final int MAX_RETRIES = 10;

    // 2. The Array Trap: Potential security hole if this was public
    private static final String[] PRIVATE_VALUES = {"A", "B", "C"};

    // Fix 1: Provide an unmodifiable list
    public static final List<String> VALUES =
            Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

    // Fix 2: Provide a copy (defensive copying)
    public static final String[] values() {
        return PRIVATE_VALUES.clone();
    }

    // 3. Private member: Default to this level
    private String internalSecret;

    // 4. Package-private: Minimum level for testing or internal package cooperation
    void internalHelper() {
        System.out.println("Assisting within the package...");
    }

    /**
     * Client usage method showcasing proper access patterns.
     */
    public static void clientUsage() {
        // Accessing constants is fine
        System.out.println("Max Retries: " + AccessControlExample.MAX_RETRIES);

        // Interaction with the safe list
        System.out.println("Available Values: " + AccessControlExample.VALUES);

        // Defensive copy: modifying the returned array does not harm the original
        String[] myCopy = AccessControlExample.values();
        myCopy[0] = "MODIFIED";
        System.out.println("Original still safe: " + AccessControlExample.values()[0]);
    }
}

/**
 * Top-level package-private class.
 * This is part of the <b>implementation</b>, not the API.
 */
class PackagePrivateImplementation {
    void performTask() {
        System.out.println("Doing work that the outside world doesn't need to see.");
    }
}