package puzzlers.effectivejava.ch8.item49;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * <h2>Check parameters for validity</h2>
 *
 * <p>
 * <b>Core Principle:</b> Most methods and constructors have restrictions on parameter values.
 * These should be documented clearly and enforced with checks at the beginning of the
 * method body to "fail fast." Detecting errors immediately prevents silent failures
 * and corrupted object states.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Fail-Fast:</b> Errors are caught before the method performs any computation,
 * making the source of the bug easier to identify.</li>
 * <li><b>Failure Atomicity:</b> Prevents a method from partially executing and leaving
 * an object in a compromised or inconsistent state.</li>
 * <li><b>Robustness:</b> Explicit checks on parameters to be stored (like in constructors)
 * prevent "time-bomb" bugs that only explode when the stored value is used later.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Overhead:</b> While usually negligible, explicit checks can occasionally be
 * expensive or impractical (e.g., checking if every element in a massive list is non-null).</li>
 * <li><b>Redundancy:</b> In some cases, the check is performed implicitly during computation
 * (e.g., {@code Collections.sort} implicitly checks if elements are comparable).</li>
 * <li><b>Assertion Dependency:</b> Assertions for non-public methods only work if the
 * {@code -ea} flag is enabled at runtime.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch10.item72 StandardExceptions
 * @see puzzlers.effectivejava.ch10.item73 ExceptionTranslation
 * @see puzzlers.effectivejava.ch10.item74 DocumentingExceptions
 * @see puzzlers.effectivejava.ch10.item76 FailureAtomicity
 */
public class ParameterValidator {

    private final String strategy;

    /**
     * Public constructor using Objects.requireNonNull for immediate validation.
     * @param strategy The strategy name; must be non-null.
     * @throws NullPointerException if strategy is null.
     */
    public ParameterValidator(String strategy) {
        // Returns the input, allowing inline assignment and validation
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    /**
     * Returns a BigInteger whose value is (this mod m).
     * @param val the value to be operated on
     * @param m the modulus, which must be positive
     * @return val mod m
     * @throws ArithmeticException if m is less than or equal to 0
     * @throws NullPointerException if m is null
     */
    public BigInteger mod(BigInteger val, BigInteger m) {
        // Explicit check for restriction described in documentation
        if (m.signum() <= 0) {
            throw new ArithmeticException("Modulus <= 0: " + m);
        }
        return val.mod(m);
    }

    /**
     * Nonpublic helper method using assertions for internal validity checks.
     * Assertions are appropriate here because the author controls the calling code.
     */
    private static void sort(long[] a, int offset, int length) {
        assert a != null;
        assert offset >= 0 && offset <= a.length;
        assert length >= 0 && length <= a.length - offset;

        Arrays.sort(a, offset, offset + length);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // 1. Valid use case
        ParameterValidator validator = new ParameterValidator("Linear");
        System.out.println("Validator created with strategy: " + validator.strategy);

        // 2. Failing fast with NullPointerException
        try {
            new ParameterValidator(null);
        } catch (NullPointerException e) {
            System.err.println("Caught expected NPE: " + e.getMessage());
        }

        // 3. Explicit validity check for domain-specific restriction
        try {
            validator.mod(BigInteger.TEN, BigInteger.valueOf(-1));
        } catch (ArithmeticException e) {
            System.err.println("Caught expected exception: " + e.getMessage());
        }

        // 4. Assertion example (Note: requires -ea flag to be visible)
        long[] numbers = {5, 3, 8, 1};
        sort(numbers, 0, 4);
        System.out.println("Sorted array: " + Arrays.toString(numbers));
    }
}