package puzzlers.effectivejava.ch7.item42;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import static java.util.Comparator.comparingInt;

/**
 * <h2>Prefer lambdas to anonymous classes</h2>
 *
 * <p>
 * <b>Core Principle:</b> For interfaces with a single abstract method (functional interfaces),
 * use lambda expressions to create function objects. They are more concise, readable, and
 * facilitate functional programming patterns.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Conciseness:</b> Eliminates the boilerplate associated with anonymous classes,
 * making the intent of the code immediately obvious.</li>
 * <li><b>Type Inference:</b> The compiler usually deduces types for lambda parameters
 * from context, further reducing visual clutter.</li>
 * <li><b>Enum Evolution:</b> Allows for constant-specific behavior in enums via
 * function object fields instead of constant-specific class bodies.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Functional Interfaces Only:</b> Lambdas cannot be used for abstract classes
 * or interfaces with multiple abstract methods; anonymous classes are still required there.</li>
 * <li><b>Self-Reference:</b> In a lambda, {@code this} refers to the enclosing instance.
 * If a function object needs to refer to itself, an anonymous class must be used.</li>
 * <li><b>Readability Constraints:</b> Lambdas lack names and documentation. If a computation
 * exceeds three lines or isn't self-explanatory, it should be refactored into a method.</li>
 * <li><b>Static Context in Enums:</b> Lambdas in enum constructors cannot access
 * enum instance members, as they are evaluated in a static context.</li>
 * <li><b>Serialization:</b> Like anonymous classes, lambdas should rarely, if ever,
 * be serialized. Use private static nested classes for serializable function objects.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item14 Comparable
 * @see puzzlers.effectivejava.ch4.item24 NestedClasses
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch6.item34 Enums
 * @see puzzlers.effectivejava.ch7.item43 MethodReferences
 * @see puzzlers.effectivejava.ch7.item44 StandardFunctionalInterfaces
 */
public class LambdaComparison {

    /**
     * Demonstrates the evolution from Anonymous Classes to Lambdas.
     */
    public void sortStrings(List<String> words) {
        // Obsolete: Anonymous class instance
        Collections.sort(words, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        });

        // Preferred: Lambda expression
        Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));

        // Even better: Comparator construction method (Item 43)
        words.sort(comparingInt(String::length));
    }

    /**
     * Enum using function object fields (lambdas) instead of
     * constant-specific class bodies.
     */
    public enum Operation {
        PLUS ("+", (x, y) -> x + y),
        MINUS ("-", (x, y) -> x - y),
        TIMES ("*", (x, y) -> x * y),
        DIVIDE("/", (x, y) -> x / y);

        private final String symbol;
        private final DoubleBinaryOperator op;

        Operation(String symbol, DoubleBinaryOperator op) {
            this.symbol = symbol;
            this.op = op;
        }

        @Override public String toString() { return symbol; }

        public double apply(double x, double y) {
            return op.applyAsDouble(x, y);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        List<String> words = new ArrayList<>(List.of("apple", "pie", "banana"));

        LambdaComparison demo = new LambdaComparison();
        demo.sortStrings(words);

        System.out.println("Sorted words: " + words);

        double x = 10.0;
        double y = 5.0;
        for (Operation op : Operation.values()) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
}