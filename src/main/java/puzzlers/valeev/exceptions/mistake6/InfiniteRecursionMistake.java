package puzzlers.valeev.exceptions.mistake6;

import java.util.*;

/**
 * Infinite recursion
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 44, Section 2
 * <p>
 * Problem:
 * Unlike finite recursion which eventually reaches a termination condition, infinite
 * recursion occurs when a program state repeats or the termination condition is never
 * met. This leads to an inevitable {@code StackOverflowError} regardless of stack size.
 * <p>
 * Expected behavior:
 * Developers expect recursive calls to eventually resolve or delegate work to another
 * object, eventually returning a result.
 * <p>
 * Actual behavior:
 * - Accidental Self-Invocation: Omitting a qualifier (like {@code delegate.}) causes
 * a method to call itself indefinitely.
 * - Cyclic Data Structures: Traversing a structure that contains a cycle (e.g., a list
 * containing itself or directory loops) leads to infinite depth.
 * - Invalid Program State: In tools like IDEs, analyzing "broken" code (e.g.,
 * {@code class A extends B; class B extends A}) can trigger infinite loops if
 * cycles aren't tracked.
 * <p>
 * Explanation:
 * - Infinite recursion occurs when the program returns to an identical state (same
 * parameters and relevant field values) while consuming more stack space.
 * - Simply converting these to loops doesn't fix the logic; it just changes a
 * {@code StackOverflowError} into an infinite loop that freezes the CPU.
 * <p>
 * Ways to Avoid:
 * - Check Qualifiers: Ensure delegation calls actually use the delegate object
 * (e.g., {@code delegate.onPressed()}) rather than the implicit {@code this}.
 * - Cycle Detection: When traversing graphs or trees that might contain cycles,
 * maintain a {@code Set} of "visited" nodes to terminate the recursion early.
 * - Avoid Self-Containing Collections: Adding a collection to itself is legal in
 * some contexts but breaks {@code hashCode()} and {@code equals()}.
 * - Use Robust Libraries: For filesystem traversal, use {@code java.nio.file.Files.walk()}
 * which has built-in protection for symbolic link cycles.
 * <p>
 * Lesson:
 * - Always assume data structures might be cyclic unless strictly proven otherwise.
 * - A {@code StackOverflowError} is often a symptom of a logic bug, not just
 * "too much data."
 */
public class InfiniteRecursionMistake {

    // --- Example 1: The Delegation Bug ---
    interface Button {
        void onPressed();
    }

    static class ButtonWrapper implements Button {
        private final Button delegate;
        private boolean enabled = true;

        ButtonWrapper(Button delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onPressed() {
            if (!enabled) return;
            // MISTAKE: Missing "delegate." prefix.
            // This calls ButtonWrapper.onPressed() recursively.
            onPressed();
        }
    }

    // --- Example 2: Cyclic Data Structures (Standard Library) ---
    public void demonstrateCollectionCycle() {
        List<Object> list1 = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();

        list1.add(list2);
        list2.add(list1);

        try {
            // list1.toString() has limited cycle protection,
            // but complex cycles still crash.
            System.out.println(list1.hashCode());
        } catch (StackOverflowError e) {
            System.out.println("Cycle detected via StackOverflow!");
        }
    }

    // --- Example 3: Safe Traversal with Cycle Detection ---
    static class Node {
        String name;
        List<Node> neighbors = new ArrayList<>();

        void safeTraverse(Set<Node> visited) {
            // Check if we have been here before
            if (!visited.add(this)) {
                return;
            }

            System.out.println("Processing: " + name);
            for (Node neighbor : neighbors) {
                neighbor.safeTraverse(visited);
            }
        }
    }

    // --- Example 4: The "Broken Code" Analysis Case ---
    // Simulating an IDE analyzing: class A extends B; class B extends A;
    static void analyzeHierarchy(String className, Set<String> seen) {
        if (!seen.add(className)) {
            System.out.println("Error: Circular inheritance detected at " + className);
            return;
        }

        // Mocking finding the superclass
        String superName = className.equals("A") ? "B" : "A";
        analyzeHierarchy(superName, seen);
    }

    public static void main(String[] args) {
        InfiniteRecursionMistake m = new InfiniteRecursionMistake();

        System.out.println("Analyzing hierarchy...");
        InfiniteRecursionMistake.analyzeHierarchy("A", new HashSet<>());

        System.out.println("\nTesting collection cycle...");
        m.demonstrateCollectionCycle();
    }
}