package puzzlers.valeev.comparing.mistake5;

import java.util.*;

/**
 * Wrong hashCode() with Array Fields
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 61
 * <p>
 * Problem:
 * Why do two objects that are equal according to .equals() end up as duplicate
 * entries in a HashSet or as separate keys in a HashMap?
 * <p>
 * Expected behavior:
 * The hashCode() contract requires that if a.equals(b) is true, then
 * a.hashCode() must equal b.hashCode(). Developers expect Objects.hash()
 * to handle array contents just as it handles other objects.
 * <p>
 * Actual behavior:
 * Objects.hash() calls the .hashCode() method of each argument. For arrays,
 * the default .hashCode() is inherited from Object, which computes a hash
 * based on the memory address (reference identity), not the array's content.
 * <p>
 * Explanation:
 * - Objects.hash() limitations: This utility method is excellent for most
 * fields because it handles nulls and uses the prime multiplier 31. However,
 * it treats arrays as single objects. It doesn't "peek" inside them.
 * - Java Records: Records automatically generate equals() and hashCode().
 * However, they also use reference equality/hashing for array components.
 * This makes records containing arrays behave unexpectedly in collections.
 * - Manual Math: Before Java 7, the standard was: result = 31 * result + field.hashCode().
 * While Objects.hash() simplifies this, it fails specifically for arrays.
 * <p>
 * Step-by-step (The Mechanics):
 * - Two instances, h1 and h2, are created with identical int[] {1, 2}.
 * - equals() is called: Arrays.equals(h1.data, h2.data) returns true.
 * - hashCode() is called: Objects.hash(h1.data) returns the address-based hash of the first array.
 * - Because the hash codes differ, the HashSet places them in different buckets
 * and never even checks if they are equal, leading to duplicates.
 * <p>
 * Fixes:
 * - Wrap array fields in Arrays.hashCode() or Arrays.deepHashCode() when
 * passing them to Objects.hash().
 * - Use IDE generation (IntelliJ/Eclipse) for equals/hashCode; most IDEs
 * detect array fields and generate content-based logic automatically.
 * - Replace arrays with List implementations (like ArrayList) where possible,
 * as Lists implement content-based equality and hashing by default.
 * <p>
 * Lesson:
 * - Arrays are the "special needs" children of the Java type system. They
 * almost never behave like standard Objects regarding equality.
 * - If a Record contains an array, you must manually override equals()
 * and hashCode() to maintain the contract.
 * <p>
 * Output:
 * - h1.equals(h2): true
 * - h1.hashCode() == h2.hashCode(): false
 * - HashSet size: 2 (expected 1)
 */
public class ArrayHashCodeMistake {

    // A standard class demonstrating the mistake
    static class DataHolder {
        final int[] intData;

        DataHolder(int[] intData) {
            this.intData = intData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataHolder that = (DataHolder) o;
            return Arrays.equals(intData, that.intData);
        }

        @Override
        public int hashCode() {
            // BUG: This uses reference-based hashing for the array
            return Objects.hash(intData);
        }

        // FIX:
        // public int hashCode() {
        //     return Objects.hash(Arrays.hashCode(intData));
        // }
    }

    // A record demonstrating the same issue
    record ArrayRecord(int[] data) {}

    public static void main(String[] args) {
        int[] rawData1 = {1, 2, 3};
        int[] rawData2 = {1, 2, 3};

        DataHolder h1 = new DataHolder(rawData1);
        DataHolder h2 = new DataHolder(rawData2);

        System.out.println("--- Class with Array Field ---");
        System.out.println("h1.equals(h2): " + h1.equals(h2));
        System.out.println("h1.hashCode() == h2.hashCode(): " + (h1.hashCode() == h2.hashCode()));

        Set<DataHolder> set = new HashSet<>();
        set.add(h1);
        set.add(h2);
        System.out.println("HashSet size: " + set.size()); // Prints 2, but should be 1

        System.out.println("\n--- Java Record with Array ---");
        ArrayRecord r1 = new ArrayRecord(rawData1);
        ArrayRecord r2 = new ArrayRecord(rawData2);

        // Even records fail with arrays!
        System.out.println("r1.equals(r2): " + r1.equals(r2)); // false (uses == for array)
        System.out.println("r1.hashCode() == r2.hashCode(): " + (r1.hashCode() == r2.hashCode()));
    }
}