package puzzlers.effectivejava.ch3.item11;

import java.util.Objects;

/**
 * <h2>Always override hashCode when you override equals</h2>
 *
 * <p>
 * <b>Core Principle:</b> You must override {@code hashCode} in every class that
 * overrides {@code equals}. Equal objects must produce the same integer result
 * when {@code hashCode} is invoked.
 * </p>
 *
 * <h3>The HashCode Contract</h3>
 * <ul>
 * <li><b>Consistency:</b> Within a single execution, {@code hashCode} must consistently
 * return the same value provided the {@code equals} data hasn't changed.</li>
 * <li><b>Equality Alignment:</b> If {@code x.equals(y)}, then {@code x.hashCode() == y.hashCode()}.</li>
 * <li><b>Collision Awareness:</b> Unequal objects do not <i>require</i> distinct hash codes,
 * but distinct codes for unequal objects significantly improve hash table performance.</li>
 * </ul>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Collection Integrity:</b> Ensures {@code HashMap.get()} finds the object stored
 * by {@code HashMap.put()}.</li>
 * <li><b>Performance:</b> A good hash function distributes objects uniformly,
 * preventing hash tables from degenerating into linked lists ({@code O(n)} search time).</li>
 * <li><b>Caching:</b> For immutable objects, the hash code can be pre-calculated or
 * lazily initialized to speed up frequent map lookups.</li>
 * </ul>
 *
 * <h3>Limitations / Warnings</h3>
 * <ul>
 * <li><b>Exclude Non-Significant Fields:</b> Fields not used in {@code equals}
 * <i>must</i> be excluded from {@code hashCode} to avoid contract violations.</li>
 * <li><b>Performance vs. Quality:</b> Don't omit significant fields just for speed;
 * poor distribution can ruin performance globally.</li>
 * <li><b>No Specific Spec:</b> Do not specify the exact return value of {@code hashCode}
 * in your documentation so you retain the flexibility to improve the algorithm later.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item10 Equals
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch11.item83 LazyInitialization
 */
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;
    private int hashCode; // Cached hash code

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = (short) areaCode;
        this.prefix   = (short) prefix;
        this.lineNum  = (short) lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PhoneNumber)) return false;
        PhoneNumber pn = (PhoneNumber) o;
        return pn.lineNum == lineNum && pn.prefix == prefix && pn.areaCode == areaCode;
    }

    /**
     * Standard recipe implementation.
     * Use 31 because it's an odd prime and can be optimized by the JVM
     * to (i << 5) - i.
     */
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Short.hashCode(areaCode);
            result = 31 * result + Short.hashCode(prefix);
            result = 31 * result + Short.hashCode(lineNum);
            hashCode = result;
        }
        return result;
    }

    /**
     * Alternative: One-liner using java.util.Objects.
     * Note: Slower due to array creation and autoboxing.
     */
    public int hashCodeAlternative() {
        return Objects.hash(areaCode, prefix, lineNum);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        java.util.Map<PhoneNumber, String> m = new java.util.HashMap<>();
        PhoneNumber jenny = new PhoneNumber(707, 867, 5309);

        m.put(jenny, "Jenny");

        // Without overriding hashCode, this would return null even though the keys are 'equal'
        System.out.println("Lookup result: " + m.get(new PhoneNumber(707, 867, 5309)));
    }
}