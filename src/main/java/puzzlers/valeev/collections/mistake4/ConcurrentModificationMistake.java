package puzzlers.valeev.collections.mistake4;

import java.util.*;

/**
 * Concurrent Modification During Iteration
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 76
 * <p>
 * Problem:
 * Why does modifying a non-concurrent Java collection while iterating over it with a
 * for-each loop result in a ConcurrentModificationException?
 * <p>
 * Expected behavior:
 * Developers often expect the loop to either include the new element, ignore the change,
 * or at least complete the iteration over the original elements.
 * <p>
 * Actual behavior:
 * The iterator detects that the collection's structure has changed since the iteration
 * started and throws a ConcurrentModificationException (fail-fast behavior).
 * <p>
 * Explanation:
 * - Fail-Fast Mechanism: Java collections maintain a {@code modCount} field. Every time the
 * collection is structurally modified (add/remove), {@code modCount} is incremented.
 * - Iterator State: When an iterator is created, it captures the current {@code modCount}
 * as {@code expectedModCount}.
 * - The Check: The {@code next()} method compares {@code modCount} to {@code expectedModCount}.
 * If they differ, it throws the exception.
 * - For-Each vs. Indexed: The for-each loop is syntactic sugar for an iterator. An indexed
 * for-loop (using {@code list.get(i)}) does not use an iterator and therefore does not
 * perform this check, though it is more "fragile" regarding index shifts.
 *
 * <p>
 * Step-by-step (CME via Addition):
 * - Iterator points before "a"; list (modCount = 0), iterator (expectedModCount = 0).
 * - {@code next()} called: Returns "a".
 * - {@code next()} called: Returns "b".
 * - {@code list.add("x")} called: list (modCount = 1).
 * - Next {@code next()} call: detects {@code modCount (1) != expectedModCount (0)} and throws exception.
 * <p>
 * The "Silent" Removal Case:
 * - If an element is removed such that the {@code cursor} position equals the new {@code size()},
 * the {@code hasNext()} check returns {@code false} before the next {@code next()} (the check) is called.
 * - In a list of 3 elements, removing an element after the 2nd iteration makes {@code size = 2}.
 * - Since 2 elements were already processed, the iterator thinks it is done.
 * <p>
 * Step-by-step (Evading CME via Removal):
 * - Iterator created: list (modCount = 0, size = 3), iterator (cursor = 0).
 * - {@code next()} called: returns "a", cursor = 1.
 * - {@code next()} called: returns "b", cursor = 2.
 * - {@code list.remove("a")} called: list (modCount = 1, size = 2).
 * - {@code hasNext()} check: checks if {@code cursor (2) != size (2)}. It is false.
 * - Loop terminates silently without calling {@code next()} and checking {@code modCount}.
 * <p>
 * Fixes:
 * - Copying: Iterate over a copy of the collection: {@code for (E e : new ArrayList<>(list))}.
 * - Iterator.remove(): Use the iterator's own remove method, which updates {@code expectedModCount}.
 * - Concurrent Collections: Use {@code CopyOnWriteArrayList}, which uses a snapshot for iterators.
 * - Caution: Do not blindly replace indexed loops with for-each loops if the collection is modified inside.
 * <p>
 * Lesson:
 * - ConcurrentModificationException is a safety feature, not a bug. It warns you that your
 * iteration logic is inconsistent with your modifications.
 * - Do not rely on the "silent" removal behavior, as it is implementation-dependent and fragile.
 * <p>
 * Output:
 * - For-each add: Throws ConcurrentModificationException
 * - Indexed loop add: Successfully adds and prints "x"
 * - For-each remove end: Terminates early without exception
 */
public class ConcurrentModificationMistake {

    public static void main(String[] args) {
        // --- Case 1: For-each (Iterator) Add ---
        try {
            List<String> list = new ArrayList<>(List.of("a", "b", "c"));
            System.out.println("Starting for-each add demonstration...");
            for (String s : list) {
                if (s.equals("b")) list.add("x");
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Caught: ConcurrentModificationException during add");
        }

        // --- Case 2: Indexed Loop Add ---
        List<String> indexedList = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("\nStarting indexed loop add (no exception):");
        for (int i = 0; i < indexedList.size(); i++) {
            String s = indexedList.get(i);
            if (s.equals("b")) indexedList.add("x");
            System.out.print(s + " ");
        }
        System.out.println("\nFinal list: " + indexedList);

        // --- Case 3: The Silent Removal Exception ---
        List<String> removeList = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("\nStarting silent removal (remove 'a' when at 'b'):");
        for (String s : removeList) {
            System.out.print(s + " ");
            if (s.equals("b")) {
                removeList.remove("a");
            }
        }
        System.out.println("\nLoop finished without exception, but missed 'c'.");
    }
}