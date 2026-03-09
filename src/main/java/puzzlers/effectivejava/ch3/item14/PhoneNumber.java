package puzzlers.effectivejava.ch3.item14;

import java.util.Comparator;
import static java.util.Comparator.*;

/**
 * <h2>Consider implementing Comparable</h2>
 *
 * <p>
 * <b>Core Principle:</b> By implementing the {@code Comparable} interface, a class
 * defines a "natural ordering" for its instances. This allows the class to
 * interoperate with generic algorithms (sorting, searching) and sorted
 * collections ({@code TreeSet}, {@code TreeMap}).
 * </p>
 *
 * <h3>The compareTo Contract</h3>
 * <ul>
 * <li><b>Symmetry:</b> The implementor must ensure that {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))} for all x and y.</li>
 * <li><b>Transitivity:</b> If {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)}, then {@code x.compareTo(z) > 0}.</li>
 * <li><b>Consistency:</b> If {@code x.compareTo(y) == 0}, then {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))} for all z.</li>
 * <li><b>Equals Alignment (Strongly Recommended):</b> {@code (x.compareTo(y) == 0) == (x.equals(y))}.
 * If violated, the class is "inconsistent with equals," which can lead to unexpected
 * behavior in sorted collections.</li>
 * </ul>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Tremendous Power:</b> Allows the use of {@code Arrays.sort(a)}, {@code Collections.max(c)},
 * and automatic sorting in {@code TreeSet}.</li>
 * <li><b>Searchability:</b> Enables efficient binary searching in sorted lists.</li>
 * <li><b>Type Safety:</b> {@code Comparable} is generic, so comparisons are
 * checked at compile time, eliminating the need for casting.</li>
 * </ul>
 *
 * <h3>Limitations / Warnings</h3>
 * <ul>
 * <li><b>Consistency Pitfall:</b> Sorted collections (like {@code TreeSet}) use
 * {@code compareTo} instead of {@code equals} for identity. For example,
 * {@code BigDecimal("1.0")} and {@code BigDecimal("1.00")} are equal in a
 * {@code TreeSet} but not in a {@code HashSet}.</li>
 * <li><b>Inheritance Issues:</b> Like {@code equals}, there is no way to extend
 * an instantiable class with a new value component while preserving the
 * {@code compareTo} contract (use composition instead).</li>
 * <li><b>Avoid Overflow:</b> Never use the "difference-based" idiom
 * ({@code return o1 - o2}) as it is prone to integer overflow and floating-point errors.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item10 Equals
 * @see puzzlers.effectivejava.ch3.item11 HashCode
 * @see puzzlers.effectivejava.ch6.item34 Enums
 */
public final class PhoneNumber implements Comparable<PhoneNumber> {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = (short) areaCode;
        this.prefix   = (short) prefix;
        this.lineNum  = (short) lineNum;
    }

    /**
     * Traditional implementation using static compare methods.
     * Starts with the most significant field.
     */
    /*
    @Override
    public int compareTo(PhoneNumber pn) {
        int result = Short.compare(areaCode, pn.areaCode);
        if (result == 0) {
            result = Short.compare(prefix, pn.prefix);
            if (result == 0)
                result = Short.compare(lineNum, pn.lineNum);
        }
        return result;
    }
    */

    /**
     * Modern implementation using Comparator construction methods.
     * Slightly slower (approx. 10%) but much more readable and maintainable.
     */
    private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);

    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }

    @Override
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        java.util.List<PhoneNumber> numbers = new java.util.ArrayList<>();
        numbers.add(new PhoneNumber(707, 867, 5309));
        numbers.add(new PhoneNumber(212, 555, 1212));
        numbers.add(new PhoneNumber(707, 123, 4567));

        // Sorting using natural ordering defined by compareTo
        java.util.Collections.sort(numbers);

        System.out.println("Sorted Phone Numbers:");
        numbers.forEach(System.out::println);

        // Usage in a sorted collection
        java.util.Set<PhoneNumber> sortedSet = new java.util.TreeSet<>(numbers);
        System.out.println("TreeSet automatically keeps order: " + sortedSet);
    }
}