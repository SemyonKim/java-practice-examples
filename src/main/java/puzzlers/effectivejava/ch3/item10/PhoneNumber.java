package puzzlers.effectivejava.ch3.item10;

import java.util.Objects;

/**
 * <h2>Obey the general contract when overriding equals</h2>
 *
 * <p>
 * <b>Core Principle:</b> Override {@code equals} only when a class has a notion of
 * logical equality that differs from object identity. When doing so, you must adhere
 * to the five requirements: Reflexivity, Symmetry, Transitivity, Consistency, and Non-nullity.
 * </p>
 *
 * <h3>The Equals Contract</h3>
 * <ul>
 * <li><b>Reflexive:</b> {@code x.equals(x)} must be true.</li>
 * <li><b>Symmetric:</b> {@code x.equals(y)} if and only if {@code y.equals(x)}.</li>
 * <li><b>Transitive:</b> If {@code x.equals(y)} and {@code y.equals(z)}, then {@code x.equals(z)}.</li>
 * <li><b>Consistent:</b> Multiple invocations return the same result if no data is modified.</li>
 * <li><b>Non-nullity:</b> {@code x.equals(null)} must always be false.</li>
 * </ul>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Logical Equality:</b> Allows value-based comparisons rather than reference-based.</li>
 * <li><b>Collection Compatibility:</b> Enables objects to work correctly as {@code Map} keys or {@code Set} elements.</li>
 * <li><b>Canonical Forms:</b> For complex objects, comparing canonical forms can optimize performance.</li>
 * </ul>
 *
 * <h3>Limitations / Warnings</h3>
 * <ul>
 * <li><b>Inheritance Conflict:</b> There is no way to extend an instantiable class and add a value
 * component while preserving the {@code equals} contract. Use <b>composition</b> instead.</li>
 * <li><b>Unreliable Resources:</b> Never write an {@code equals} method that depends on
 * unreliable resources (e.g., {@code java.net.URL}'s dependence on IP addresses).</li>
 * <li><b>Liskov Substitution Principle:</b> Using {@code getClass()} instead of {@code instanceof}
 * breaks the ability for subtypes to function as the parent type in collections.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch3.item11 HashCode
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item18 Composition
 * @see puzzlers.effectivejava.ch6.item40 OverrideAnnotation
 */
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = (short) rangeCheck(areaCode, 999, "area code");
        this.prefix   = (short) rangeCheck(prefix, 999, "prefix");
        this.lineNum  = (short) rangeCheck(lineNum, 9999, "line num");
    }

    private static int rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return val;
    }

    /**
     * High-quality equals recipe:
     * 1. Use == for performance optimization.
     * 2. Use instanceof for type check (and null check).
     * 3. Cast to correct type.
     * 4. Compare significant fields (primitives with ==, floats/doubles with compare()).
     */
    @Override
    public boolean equals(Object o) {
        // 1. Reference check
        if (o == this)
            return true;

        // 2. Type check (returns false if o is null)
        if (!(o instanceof PhoneNumber))
            return false;

        // 3. Cast
        PhoneNumber pn = (PhoneNumber) o;

        // 4. Field comparison (compare most likely to differ first)
        return pn.lineNum == lineNum &&
                pn.prefix == prefix &&
                pn.areaCode == areaCode;
    }

    // Always override hashCode when overriding equals! (Item 11)
    @Override
    public int hashCode() {
        return Objects.hash(areaCode, prefix, lineNum);
    }

    // --- Client Usage ---
    public static void main(String[] args) {
        PhoneNumber n1 = new PhoneNumber(707, 867, 5309);
        PhoneNumber n2 = new PhoneNumber(707, 867, 5309);

        // Logical equality test
        System.out.println("n1.equals(n2): " + n1.equals(n2)); // true
    }
}

/**
 * Example of avoiding inheritance issues via COMPOSITION.
 */
class ColorPoint {
    private final Point point;
    private final String color;

    public ColorPoint(int x, int y, String color) {
        this.point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    /** Returns the point-view of this color point. */
    public Point asPoint() { return point; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return false;
        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}

class Point {
    private final int x, y;
    public Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}