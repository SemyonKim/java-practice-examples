package puzzlers.valeev.comparing.mistake6;

import java.util.*;

/**
 * Mismatch between equals() and hashCode()
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 62
 * <p>
 * Problem:
 * Why do objects that are logically equal (like case-insensitive strings or
 * unordered pairs) sometimes "disappear" or fail to be found in a HashSet or
 * a Set.of() collection?
 * <p>
 * Expected behavior:
 * If a.equals(b) is true, then a and b must be treated as the same key in
 * any hash-based collection, requiring them to have identical hash codes.
 * <p>
 * Actual behavior:
 * Custom equals() implementations often implement logic (like symmetry or
 * case-insensitivity) that the corresponding hashCode() fails to mirror.
 * This leads to objects being placed in different hash buckets despite
 * being "equal."
 * <p>
 * Explanation:
 * - Unordered Fields: If equals() returns true for (f1=1, f2=2) and (f1=2, f2=1),
 * using Objects.hash(f1, f2) is wrong because it is order-dependent
 * (31 * f1 + f2 != 31 * f2 + f1).
 * - Case Insensitivity: String.equalsIgnoreCase() is complex. Some characters,
 * like the Greek sigma (σ vs ς), have different lowercase forms but the same
 * uppercase form (Σ). Therefore, equalsIgnoreCase() returns true, but
 * .toLowerCase().hashCode() returns different values.
 * - Set.of() Behavior: Modern Java's Set.of() is optimized. For 1 or 2
 * elements, it may use simple equality checks. Once it reaches 3 or more
 * elements, it switches to hash-based lookups, causing "magic" disappearances
 * if the hashCode contract is violated.
 * <p>
 * Step-by-step (The Mechanics):
 * - Create CaseInsensitiveString("σ") and CaseInsensitiveString("ς").
 * - equalsIgnoreCase() returns true because both map to 'Σ'.
 * - hashCode() uses .toLowerCase(), which returns different values for σ and ς.
 * - Set.of(s1, "a", "b").contains(s2) looks in the bucket for s2's hash.
 * - Since s1 was stored in a different bucket, the set reports false.
 * <p>
 * Fixes:
 * - Symmetry in Hashing: For unordered fields, use addition (f1 + f2) or
 * XOR (f1 ^ f2) which are commutative, or normalize fields in the constructor.
 * - Precise Case Normalization: For equalsIgnoreCase() compatibility, use
 * string.toUpperCase(Locale.ROOT).toLowerCase(Locale.ROOT).hashCode().
 * - Key Consistency: Ensure the "sequence of keys" used in equals() matches
 * those used in hashCode().
 * <p>
 * Lesson:
 * - If you override equals(), you MUST override hashCode().
 * - Avoid complex logic in equals() that cannot be easily mirrored in hashCode().
 * - Normalization at construction time (e.g., sorting fields or forcing
 * lowercase) is almost always safer than custom comparison logic.
 * <p>
 * Output:
 * - s1.equals(s2): true
 * - Set.of(s1).contains(s2): true
 * - Set.of(s1, "a", "b").contains(s2): false
 */
public class HashCodeMismatchMistake {

    static class TwoFields {
        int f1, f2;

        TwoFields(int f1, int f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TwoFields that = (TwoFields) o;
            // Symmetric: (1,2) equals (2,1)
            return (f1 == that.f1 && f2 == that.f2) || (f1 == that.f2 && f2 == that.f1);
        }

        @Override
        public int hashCode() {
            // BUG: Objects.hash(1, 2) != Objects.hash(2, 1)
            return Objects.hash(f1, f2);
            // FIX: return f1 + f2;
        }
    }

    static class CaseInsensitiveString {
        private final String string;

        CaseInsensitiveString(String string) {
            this.string = string;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CaseInsensitiveString)) return false;
            return string.equalsIgnoreCase(((CaseInsensitiveString) o).string);
        }

        @Override
        public int hashCode() {
            // BUG: "σ".toLowerCase() != "ς".toLowerCase()
            return string.toLowerCase(Locale.ROOT).hashCode();
        }
    }

    public static void main(String[] args) {
        // Example 1: Unordered fields
        TwoFields t1 = new TwoFields(1, 2);
        TwoFields t2 = new TwoFields(2, 1);
        System.out.println("TwoFields equal: " + t1.equals(t2));
        System.out.println("TwoFields hash equal: " + (t1.hashCode() == t2.hashCode()));

        // Example 2: Case Insensitivity & Greek Sigma
        CaseInsensitiveString s1 = new CaseInsensitiveString("ς");
        CaseInsensitiveString s2 = new CaseInsensitiveString("σ");

        System.out.println("\n--- CaseInsensitiveString (Sigma) ---");
        System.out.println("s1.equals(s2): " + s1.equals(s2)); // true

        // Small sets might work by coincidence
        System.out.println("Set.of(s1).contains(s2): " + Set.of(s1).contains(s2));

        // Larger sets rely on hashCode() and will fail
        try {
            Set<Object> largerSet = Set.of(s1, "a", "b");
            System.out.println("Set.of(s1, a, b).contains(s2): " + largerSet.contains(s2)); // false
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}