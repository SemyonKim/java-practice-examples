package puzzlers.effectivejava.ch6.item34;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toMap;

/**
 * <h2>Use enums instead of int constants</h2>
 *
 * <p>
 * <b>Core Principle:</b> Java enums are full-fledged classes (instance-controlled)
 * rather than mere integers. Use them whenever you need a fixed set of constants
 * known at compile time to gain type safety, expressiveness, and maintainability.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Compile-time Type Safety:</b> Prevents passing incorrect types (e.g., passing an Orange to an Apple method).</li>
 * <li><b>Namespace Integrity:</b> Identically named constants in different enums coexist without prefixes (e.g., {@code Planet.MERCURY} vs {@code Element.MERCURY}).</li>
 * <li><b>Binary Compatibility:</b> Constants can be added or reordered without recompiling clients; values are not hard-coded into client bytecode.</li>
 * <li><b>Rich Functionality:</b> Enums can have fields, methods, implement interfaces, and provide custom string representations.</li>
 * <li><b>Constant-Specific Behavior:</b> Allows associating different code with each constant via abstract methods or the Strategy Enum pattern.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Performance Overhead:</b> There is a minor space and time cost for loading and initializing enum types, though it is rarely significant in practice.</li>
 * <li><b>Set Rigidity:</b> Enums are for fixed sets of constants; if the set needs to be extended by clients, an interface-based approach is required (see Item 38).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item3 Singletons
 * @see puzzlers.effectivejava.ch3.item14 Comparable
 * @see puzzlers.effectivejava.ch4.item15 Accessibility
 * @see puzzlers.effectivejava.ch4.item16 Accessors
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item24 NestedClasses
 * @see puzzlers.effectivejava.ch8.item55 Optionals
 */
public class EnumExamples {

    // --- 1. Rich Enum with Data and Behavior ---
    public enum Planet {
        EARTH(5.975e+24, 6.378e6),
        MARS(6.419e+23, 3.393e6);

        private final double mass;   // In kilograms
        private final double radius; // In meters
        private final double surfaceGravity;

        private static final double G = 6.67300E-11;

        Planet(double mass, double radius) {
            this.mass = mass;
            this.radius = radius;
            this.surfaceGravity = G * mass / (radius * radius);
        }

        public double surfaceGravity() {
            return surfaceGravity;
        }

        public double surfaceWeight(double mass) {
            return mass * surfaceGravity;
        }
    }

    // --- 2. Constant-Specific Method Implementations ---
    public enum Operation {
        PLUS("+") {
            @Override public double apply(double x, double y) { return x + y; }
        },
        MINUS("-") {
            @Override public double apply(double x, double y) { return x - y; }
        };

        private final String symbol;
        Operation(String symbol) { this.symbol = symbol; }

        @Override public String toString() { return symbol; }
        public abstract double apply(double x, double y);

        // Map for fromString lookup
        private static final Map<String, Operation> stringToEnum =
                Stream.of(values()).collect(toMap(Object::toString, e -> e));

        public static Optional<Operation> fromString(String symbol) {
            return Optional.ofNullable(stringToEnum.get(symbol));
        }
    }

    // --- 3. The Strategy Enum Pattern ---
    enum PayrollDay {
        MONDAY, FRIDAY,
        SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);

        private final PayType payType;

        PayrollDay(PayType payType) { this.payType = payType; }
        PayrollDay() { this(PayType.WEEKDAY); } // Default constructor

        int pay(int minutesWorked, int payRate) {
            return payType.pay(minutesWorked, payRate);
        }

        // Nested Strategy Enum
        private enum PayType {
            WEEKDAY {
                int overtimePay(int mins, int payRate) {
                    return mins <= MINS_PER_SHIFT ? 0 : (mins - MINS_PER_SHIFT) * payRate / 2;
                }
            },
            WEEKEND {
                int overtimePay(int mins, int payRate) {
                    return mins * payRate / 2;
                }
            };

            abstract int overtimePay(int mins, int payRate);
            private static final int MINS_PER_SHIFT = 8 * 60;

            int pay(int minsWorked, int payRate) {
                int basePay = minsWorked * payRate;
                return basePay + overtimePay(minsWorked, payRate);
            }
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Using Planet data
        double weightOnEarth = 185.0;
        double mass = weightOnEarth / Planet.EARTH.surfaceGravity();
        System.out.printf("Weight on %s is %f%n", Planet.MARS, Planet.MARS.surfaceWeight(mass));

        // Using Operation behavior
        double x = 2.0;
        double y = 4.0;
        for (Operation op : Operation.values()) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }

        // Using fromString
        Optional<Operation> op = Operation.fromString("+");
        op.ifPresent(o -> System.out.println("Found operation: " + o));

        // Using Strategy Enum
        int pay = PayrollDay.SATURDAY.pay(480, 20);
        System.out.println("Saturday pay (weekend rate): " + pay);
    }
}