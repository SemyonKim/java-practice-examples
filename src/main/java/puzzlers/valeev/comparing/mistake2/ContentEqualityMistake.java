package puzzlers.valeev.comparing.mistake2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assuming equals() Compares Content
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 56
 * <p>
 * Problem:
 * Why does calling .equals() on certain objects like arrays, StringBuilders, or
 * AtomicIntegers return false even when they represent the same data?
 * <p>
 * Expected behavior:
 * Developers often assume that .equals() is always overridden to compare the
 * internal state or "content" of an object rather than its memory address.
 * <p>
 * Actual behavior:
 * If a class does not explicitly override the equals() method, it inherits the
 * default implementation from java.lang.Object, which performs a reference
 * equality check (identical to the == operator).
 * <p>
 * Explanation:
 * - Arrays: Java arrays are objects that do not override equals(). Comparing two
 * different array instances with .equals() always checks if they are the same
 * instance in memory.
 * - StringBuilder/StringBuffer: These classes do not override equals(). To compare
 * them, one should use .compareTo() (since Java 11) or convert them to Strings.
 * - Atomic Variables: AtomicInteger and AtomicReference do not override equals()
 * because they are designed for concurrent state, not as value objects.
 * - Collection Wrappers: Collections.unmodifiableCollection() does not implement
 * List or Set interfaces. Therefore, comparing an unmodifiable wrapper of a
 * List to the List itself returns false to maintain the symmetry rule of equals().
 * - Streams: Streams cannot implement content-based equals() because they are
 * consumed upon traversal, making a consistent comparison impossible.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: Java Arrays
 * - Two separate arrays [1, 2, 3] are created at different heap locations.
 * - array1.equals(array2) calls Object.equals(), which sees different addresses -> false.
 * - Case: StringBuilder
 * - sb1 and sb2 both contain "test".
 * - sb1.equals(sb2) returns false because StringBuilder relies on reference identity.
 * - Case: Unmodifiable Collection
 * - A List is wrapped in unmodifiableCollection().
 * - list.equals(wrapper) returns false because the wrapper is not an instance of List.
 * <p>
 * Fixes:
 * - Use Arrays.equals(a, b) or Arrays.deepEquals(a, b) for arrays.
 * - For StringBuilder, use sb1.compareTo(sb2) == 0 or sb1.toString().equals(sb2.toString()).
 * - For Atomic variables, compare the retrieved values: a1.get() == a2.get().
 * - Use unmodifiableList() or unmodifiableSet() if you need to preserve equality behavior.
 * <p>
 * Lesson:
 * - Never assume .equals() compares content without checking the API documentation.
 * - Standard JDK classes like arrays and buffers are common traps for this mistake.
 * - Be wary of "wrapper" classes that might hide the underlying type's equality logic.
 * <p>
 * Output:
 * - new int[]{1}.equals(new int[]{1}) -> false
 * - new AtomicInteger(1).equals(new AtomicInteger(1)) -> false
 * - unmodifiableCollection(list).equals(list) -> false
 */
public class ContentEqualityMistake {

    public static void main(String[] args) {
        // Array Example
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {1, 2, 3};
        System.out.println("--- Array Comparison ---");
        System.out.println("arr1.equals(arr2): " + arr1.equals(arr2)); // false
        System.out.println("Arrays.equals(arr1, arr2): " + Arrays.equals(arr1, arr2)); // true

        // StringBuilder Example
        StringBuilder sb1 = new StringBuilder("Java");
        StringBuilder sb2 = new StringBuilder("Java");
        System.out.println("\n--- StringBuilder Comparison ---");
        System.out.println("sb1.equals(sb2): " + sb1.equals(sb2)); // false
        System.out.println("sb1.compareTo(sb2) == 0: " + (sb1.compareTo(sb2) == 0)); // true

        // AtomicInteger Example
        AtomicInteger v1 = new AtomicInteger(1);
        AtomicInteger v2 = new AtomicInteger(1);
        System.out.println("\n--- Atomic Comparison ---");
        System.out.println("v1.equals(v2): " + v1.equals(v2)); // false
        System.out.println("v1.get() == v2.get(): " + (v1.get() == v2.get())); // true

        // Unmodifiable Collection Example
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        Collection<Integer> unmodifiable = Collections.unmodifiableCollection(list1);
        System.out.println("\n--- Unmodifiable Collection Comparison ---");
        System.out.println("list1.equals(unmodifiable): " + list1.equals(unmodifiable)); // false

        List<Integer> unmodifiableList = Collections.unmodifiableList(list1);
        System.out.println("list1.equals(unmodifiableList): " + list1.equals(unmodifiableList)); // true
    }
}