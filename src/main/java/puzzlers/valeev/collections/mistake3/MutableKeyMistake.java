package puzzlers.valeev.collections.mistake3;

import java.util.*;
import java.util.stream.*;

/**
 * Using Mutable Objects as Keys
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 74
 * <p>
 * Problem:
 * Why does changing an object after it has been added to a HashSet or HashMap
 * make it "disappear" even though it is still physically inside the collection?
 * <p>
 * Expected behavior:
 * Developers expect that if an object instance is present in a set,
 * set.contains(object) should return true regardless of internal state changes.
 * <p>
 * Actual behavior:
 * Mutating the object changes its hashCode. Since the collection uses the
 * hashCode to find the storage bucket, it searches the wrong bucket and
 * reports that the object is missing. In tree-ified buckets, it may also
 * fail due to inconsistent compareTo() results.
 * <p>
 * Explanation:
 * - Hash Inconsistency: Collections like HashSet calculate the bucket index
 * based on the hash code at the time of insertion. If the hash code
 * changes later, the lookup logic points to a different (empty) bucket.
 * - Tree-ification (JEP 180): When collisions occur, HashMaps may use a
 * red-black tree. If a key is modified and its compareTo() result changes,
 * the binary search within that bucket fails.
 * - Legacy Risks: Classes like java.util.Date and BitSet are mutable and
 * dangerous to use as keys.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: List as a Key
 * - Add an empty ArrayList to a HashSet.
 * - Modify the list (e.g., list.add("Value")).
 * - The list's hashCode changes.
 * - set.contains(list) calculates the new hash, looks in a different bucket,
 * and returns false.
 * <p>
 * Fixes:
 * - Avoid Mutables: Do not use mutable objects as map keys or set elements.
 * - IdentityHashMap: Use IdentityHashMap if you must map by reference
 * rather than by content-based equality.
 * - Use Modern APIs: Replace java.util.Date with immutable java.time classes.
 * - Validation: Ensure compareTo() is consistent with equals() to avoid
 * tree-search failures during collisions.
 * <p>
 * Lesson:
 * - Map keys and Set elements must be stable. If an object's equals/hashCode
 * contract depends on mutable state, it should not be used as a key.
 * - Testing with -XX:hashCode=2 can help identify code that relies on
 * unstable identity hash distributions.
 * <p>
 * Output:
 * - contains(list) after mutation: false
 * - contains(sb) before mutation: true
 * - contains(sb) after mutation: false
 */
public class MutableKeyMistake {

    public static void main(String[] args) {
        // --- Demonstration with List ---
        Set<List<String>> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        set.add(list);
        list.add("Value");
        System.out.println("Set contains modified list: " + set.contains(list));

        // --- Demonstration with StringBuilder (Tree-ification case) ---
        Set<StringBuilder> sbSet = new HashSet<>();

        // Create 40 StringBuilder objects in the same bucket to force a red-black tree
        for (int count = 0; count < 40; count++) {
            sbSet.add(firstBucketStringBuilder());
        }

        int[] hashCodes = sbSet.stream()
                .mapToInt(Object::hashCode)
                .sorted().toArray();

        // Create one more SB with an exact hash collision
        StringBuilder sb = Stream.generate(StringBuilder::new)
                .filter(b -> Arrays.binarySearch(hashCodes, b.hashCode()) >= 0)
                .findFirst()
                .get();

        sbSet.add(sb);
        System.out.println("SB set contains sb before mutation: " + sbSet.contains(sb));

        sb.append("b"); // Update content; compareTo result changes
        System.out.println("SB set contains sb after mutation: " + sbSet.contains(sb));
    }

    /**
     * Relies on OpenJDK implementation to produce objects targeting bucket #0.
     */
    static StringBuilder firstBucketStringBuilder() {
        while (true) {
            StringBuilder sb = new StringBuilder("a");
            int hc = sb.hashCode();
            if (((hc ^ (hc >>> 16)) & 0x3F) == 0) {
                return sb;
            }
        }
    }
}