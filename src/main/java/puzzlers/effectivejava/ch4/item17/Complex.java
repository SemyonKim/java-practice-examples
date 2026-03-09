package puzzlers.effectivejava.ch4.item17;

import java.math.BigInteger;

/**
 * <h2>Minimize Mutability</h2>
 *
 * <p>An immutable class is simply a class whose instances cannot be modified.
 * All information contained in each instance is fixed for the lifetime of the object,
 * so no changes can ever be observed. Examples include {@code String}, boxed primitives,
 * {@code BigInteger}, and {@code BigDecimal}.</p>
 *
 * <h3>Core Principle</h3>
 * Classes should be immutable unless there is a very good reason to make them mutable.
 * If a class must be mutable, limit its state space as much as possible. Declare every
 * field <b>private final</b> unless there is a compelling reason to do otherwise.
 *
 * <h3>The Five Rules for Immutability</h3>
 * <ol>
 * <li><b>Don't provide mutators:</b> No methods should modify the object's state.</li>
 * <li><b>Ensure the class can't be extended:</b> Use {@code final} or private constructors with static factories.</li>
 * <li><b>Make all fields final:</b> Ensures correct behavior across threads without synchronization.</li>
 * <li><b>Make all fields private:</b> Prevents clients from modifying mutable objects referred to by fields.</li>
 * <li><b>Ensure exclusive access to mutable components:</b> Use defensive copies (Item 50) for any mutable internal fields.</li>
 * </ol>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Simplicity & Safety:</b> Inherently thread-safe; requires no synchronization.</li>
 * <li><b>Shareability:</b> Can be shared freely; no need for defensive copies or clone methods.</li>
 * <li><b>Internal Sharing:</b> Instances can share internal parts (e.g., {@code BigInteger} magnitude arrays).</li>
 * <li><b>Failure Atomicity:</b> Objects never exist in a temporary inconsistent state.</li>
 * <li><b>Static Factories:</b> Allows caching frequently requested instances to reduce memory footprint.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Performance:</b> Requires a separate object for each distinct value, which can be costly for large objects.</li>
 * <li><b>Multistep Operations:</b> May generate many intermediate objects (use a mutable <b>companion class</b> like {@code StringBuilder} if necessary).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch3.item11 OverrideHashCode
 * @see puzzlers.effectivejava.ch4.item15 MinimizeAccessibility
 * @see puzzlers.effectivejava.ch8.item50 MakeDefensiveCopies
 * @see puzzlers.effectivejava.ch11.item83 UseLazyInitialization
 * @see puzzlers.effectivejava.ch12.item88 ReadObjectDefensively
 */
public class Complex {
    private final double re;
    private final double im;

    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);
    public static final Complex I = new Complex(0, 1);

    /**
     * Private constructor ensures the class is effectively final.
     * It cannot be subclassed because there is no accessible constructor.
     */
    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Static factory method provides flexibility for caching and
     * performance tuning in future releases.
     */
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }

    public double realPart()      { return re; }
    public double imaginaryPart() { return im; }

    /**
     * Functional approach: Methods return the result of a function
     * applied to the operand without modifying it.
     */
    public Complex plus(Complex c) {
        return Complex.valueOf(re + c.re, im + c.im);
    }

    public Complex minus(Complex c) {
        return Complex.valueOf(re - c.re, im - c.im);
    }

    public Complex times(Complex c) {
        return Complex.valueOf(re * c.re - im * c.im,
                re * c.im + im * c.re);
    }

    public Complex dividedBy(Complex c) {
        double tmp = c.re * c.re + c.im * c.im;
        return Complex.valueOf((re * c.re + im * c.im) / tmp,
                (im * c.re - re * c.im) / tmp);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Complex)) return false;
        Complex c = (Complex) o;
        return Double.compare(c.re, re) == 0 &&
                Double.compare(c.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }

    @Override
    public String toString() {
        return "(" + re + " + " + im + "i)";
    }

    /**
     * Defensive copy check for non-final immutable classes (like BigInteger).
     */
    public static BigInteger safeInstance(BigInteger val) {
        return val.getClass() == BigInteger.class ?
                val : new BigInteger(val.toByteArray());
    }

    /**
     * Client usage demonstrating immutability and the functional approach.
     */
    public static void main(String[] args) {
        Complex a = Complex.valueOf(1.0, 2.0);
        Complex b = Complex.valueOf(3.0, 4.0);

        // a and b remain unchanged; sum is a new instance
        Complex sum = a.plus(b);

        System.out.println("a: " + a);
        System.out.println("Sum: " + sum);

        // Demonstrating safety with untrusted BigInteger subclasses
        BigInteger safe = safeInstance(BigInteger.TEN);
    }
}