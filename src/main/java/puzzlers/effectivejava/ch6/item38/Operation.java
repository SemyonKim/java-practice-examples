package puzzlers.effectivejava.ch6.item38;

import java.util.Arrays;
import java.util.Collection;

/**
 * <h2>Emulate extensible enums with interfaces</h2>
 *
 * <p>
 * <b>Core Principle:</b> While the {@code enum} language construct does not support
 * inheritance, extensibility can be emulated by defining an interface and having
 * enum types implement that interface. This allows APIs to accept the interface
 * type, enabling users to provide their own "extensions" to the base set of constants.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Extensibility:</b> Clients can add their own operations or codes by
 * implementing the same interface, effectively extending the API's capabilities.</li>
 * <li><b>API Flexibility:</b> Methods written to accept the interface can process
 * both base and extended enum constants interchangeably.</li>
 * <li><b>Type Safety:</b> Using bounded type tokens ({@code <T extends Enum<T> & Operation>})
 * ensures that the passed class is both an enum and an implementation of the interface.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Implementation Inheritance:</b> Code cannot be inherited between enums.
 * Logic must be duplicated, moved to {@code default} interface methods, or
 * encapsulated in a helper class/static method.</li>
 * <li><b>Complexity:</b> Bounded type token declarations can be complex and
 * harder to read than standard enum usage.</li>
 * <li><b>Collection Constraints:</b> When passing collections of the interface
 * rather than the enum class, you lose the ability to use specialized collections
 * like {@link java.util.EnumSet} or {@link java.util.EnumMap}.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch6.item34 EnumStrategy
 * @see puzzlers.effectivejava.ch5.item33 BoundedTypeTokens
 * @see puzzlers.effectivejava.ch5.item31 BoundedWildcards
 * @see puzzlers.effectivejava.ch6.item36 EnumSet
 */
public interface Operation {
    double apply(double x, double y);
}
    // --- Base Implementation ---

class Implementation {

    public enum BasicOperation implements Operation {
        PLUS("+") { public double apply(double x, double y) { return x + y; } },
        MINUS("-") { public double apply(double x, double y) { return x - y; } },
        TIMES("*") { public double apply(double x, double y) { return x * y; } },
        DIVIDE("/") { public double apply(double x, double y) { return x / y; } };

        private final String symbol;

        BasicOperation(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    // --- Extended Implementation ---

    public enum ExtendedOperation implements Operation {
        EXP("^") { public double apply(double x, double y) { return Math.pow(x, y); } },
        REMAINDER("%") { public double apply(double x, double y) { return x % y; } };

        private final String symbol;

        ExtendedOperation(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        double x = 4.0;
        double y = 2.0;

        System.out.println("Using Bounded Type Token (Class literal):");
        testWithClass(ExtendedOperation.class, x, y);

        System.out.println("\nUsing Bounded Wildcard (Collection):");
        testWithCollection(Arrays.asList(ExtendedOperation.values()), x, y);

        // Demonstrating flexibility: mixing base and extended operations
        System.out.println("\nUsing Mixed Operations:");
        testWithCollection(Arrays.asList(BasicOperation.PLUS, ExtendedOperation.EXP), x, y);
    }

    /**
     * Uses a bounded type token to ensure the class is both an Enum and an Operation.
     */
    private static <T extends Enum<T> & Operation> void testWithClass(
            Class<T> opEnumType, double x, double y) {
        for (Operation op : opEnumType.getEnumConstants()) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }

    /**
     * Uses a bounded wildcard to allow collections of any Operation implementation.
     */
    private static void testWithCollection(
            Collection<? extends Operation> opSet, double x, double y) {
        for (Operation op : opSet) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
}