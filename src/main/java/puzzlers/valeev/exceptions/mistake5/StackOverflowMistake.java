package puzzlers.valeev.exceptions.mistake5;

import java.util.*;

/**
 * Deep but finite recursion
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 44, Section 1
 * <p>
 * Problem:
 * Java does not natively support Tail Call Optimization (TCO). Recursive methods that
 * work fine with small datasets can trigger a {@code StackOverflowError} when processing
 * large inputs (e.g., 10,000+ elements) because every call adds a new frame to the
 * thread's stack.
 * <p>
 * Expected behavior:
 * Developers coming from functional languages (or using Kotlin/Scala) might expect
 * "tail-recursive" calls to be optimized into loops, allowing for infinite or
 * very deep recursion without increasing memory consumption.
 * <p>
 * Actual behavior:
 * - Stack Limits: The JVM stack size is fixed (often 1MB). Deep recursion exhausts
 * this space quickly.
 * - No Native TCO: Unlike Kotlin's {@code tailrec}, the standard Java compiler (javac)
 * does not convert tail recursion into loops.
 * - Manual Overhead: To handle deep structures, developers must manually refactor
 * code to use iterative loops and heap-based data structures.
 * <p>
 * Explanation:
 * - Tail Recursion: A specific form of recursion where the recursive call is the
 * absolute last action. These can be mechanically transformed into {@code while(true)} loops.
 * - Non-Tail Recursion: When a method needs to perform work *after* the recursive
 * call returns (e.g., tree traversal), it cannot be a simple loop. It requires
 * an explicit {@code Deque} (stack) on the heap to store state.
 * <p>
 * Ways to Avoid:
 * - Replace Tail Recursion with Iteration: Use IDE refactoring tools
 * to convert tail-recursive methods into {@code while} loops.
 * - Use Heap-Based Stacks: For complex recursion (like depth-first search), use
 * {@code ArrayDeque} to simulate the stack. The heap is significantly larger
 * than the thread stack.
 * - Prefer Library Methods: Use established libraries for graph/tree traversal
 * that are already optimized to avoid stack overflows.
 * - Safety Margin: If recursion depth might exceed ~1,000 calls, refactor to
 * iterative logic to be safe.
 * <p>
 * Lesson:
 * - The stack is for small, short-lived frames; the heap is for large data.
 * - Java favors iteration over recursion for deep processing.
 */
public class StackOverflowMistake {

    // --- Example 1: Tail Recursion (Dangerous in Java) ---
    // This will crash with StackOverflowError on a very long string.
    static int indexOfRecursive(String s, int start, char c) {
        if (start >= s.length()) return -1;
        if (s.charAt(start) == c) return start;
        return indexOfRecursive(s, start + 1, c);
    }

    // --- Example 2: Iterative Replacement (Safe) ---
    // Mechanically rewritten to use a loop. Limited only by string length/CPU.
    static int indexOfIterative(String s, int start, char c) {
        while (true) {
            if (start >= s.length()) return -1;
            if (s.charAt(start) == c) return start;
            start = start + 1;
        }
    }

    // --- Example 3: Non-Tail Recursion (Tree Search) ---
    record Node(String name, Node... children) {
        // Simple but risky for very deep trees
        Node findRecursive(String name) {
            if (name().equals(name)) return this;
            for (Node child : children()) {
                Node node = child.findRecursive(name);
                if (node != null) return node;
            }
            return null;
        }

        // --- Example 4: Heap-Based Iteration (Safe for Deep Trees) ---
        // Uses ArrayDeque to emulate the stack, moving memory pressure to the heap.
        Node findIterative(String name) {
            Deque<Node> stack = new ArrayDeque<>();
            stack.push(this);

            while (!stack.isEmpty()) {
                Node node = stack.pop();
                if (node.name().equals(name)) return node;

                // Add children to the stack to visit them later
                if (node.children() != null) {
                    for (int i = node.children().length - 1; i >= 0; i--) {
                        stack.push(node.children()[i]);
                    }
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        // Simulation of a deep tree
        Node root = new Node("root",
                new Node("child1", new Node("grandchild")),
                new Node("child2")
        );

        System.out.println("Found: " + root.findIterative("grandchild"));
    }
}