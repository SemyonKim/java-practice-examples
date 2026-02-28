package puzzlers.valeev.library.mistake2;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * Violating the Stream API Contract: Stream.reduce(identity, accumulator)
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 86
 * <p>
 * Problem:
 * Why does a Stream pipeline produce correct results in sequential mode but
 * completely incorrect results when .parallel() is added?
 * <p>
 * Expected behavior:
 * Developers expect that if a stream works sequentially, it is "safe" to
 * parallelize. They often use reduction operations that seem logical but
 * violate the mathematical constraints of the API.
 * <p>
 * Actual behavior:
 * Parallel streams split data into chunks, process them independently, and
 * combine results. If the accumulator is not associative or lacks a proper
 * identity, the final merged result will be garbage.
 * <p>
 * Explanation:
 * - Reduction Contract: The {@code Stream.reduce(identity, accumulator)} method
 * requires the accumulator.apply(x, y) (shortly apply(x,y)) to be:
 * - Associative: apply(x, apply(y, z)) = apply(apply(x,y), z)
 * - Identity-compliant: apply(x,i) = x and apply(i,x) = x, where i - identity
 * - The HashCode Trap: Calculating a List hashCode using {@code h = 31*h + val}
 * is NOT associative. In parallel, the "identity" (1) is applied to every
 * chunk, multiplying the error.
 * - Stateful Mapping: The {@code map()} operation requires stateless functions.
 * Using a variable to store the "previous" element to create pairs (pairMap)
 * fails in parallel because elements are processed in out-of-order chunks.
 * <p>
 * Step-by-step (The Parallel Failure):
 * - Task: Calculate hash for ["a", "b", "c", "d"] where identity = 1.
 * - Sequential: (((1 * 31 + a) * 31 + b) * 31 + c) ... -> Correct.
 * - Parallel (2 threads):
 * - Thread 1: (1 * 31 + a) and (1 * 31 + b) then combine.
 * - Thread 2: (1 * 31 + c) and (1 * 31 + d) then combine.
 * - Final Merge: The identity '1' has been factored into the calculation
 * multiple times unnecessarily, leading to a massive, incorrect integer.
 * <p>
 * Fixes:
 * - forEachOrdered: If processing must be left-to-right, use {@code forEachOrdered}.
 * Note that this often negates the performance benefits of parallel streams.
 * - Classic Loops: If the algorithm is inherently sequential (like the
 * List.hashCode algorithm), a standard for-each loop is more readable and
 * less error-prone.
 * - Third-Party Libraries: Use libraries like StreamEx for complex
 * operations like {@code pairMap} that are difficult to implement correctly
 * for parallel streams manually.
 * - Testing: Always test utility methods that consume streams with
 * {@code .parallel()} to ensure contract compliance.
 * <p>
 * Lesson:
 * - The Stream API is a functional abstraction with strict mathematical rules.
 * - Violating statelessness or associativity is a "silent" bug that might only
 * appear when moving to high-core-count production environments.
 * <p>
 * Output:
 * - Sequential Hash: 3910595
 * - Parallel Hash: [Random Incorrect Large Number] e.g. 131168
 * - PairMap (Sequential): a->b, b->c, c->d
 */
public class StreamContractMistake {

    public static void main(String[] args) {
        List<String> list = List.of("a", "b", "c", "d");

        // --- Case 1: The HashCode Violation ---
        int sequentialHash = list.stream()
                .mapToInt(Objects::hashCode)
                .reduce(1, (x, y) -> x * 31 + y);
        System.out.println("Sequential Hash: " + sequentialHash);

        int parallelHash = list.parallelStream()
                .mapToInt(Objects::hashCode)
                .reduce(1, (x, y) -> x * 31 + y);
        System.out.println("Parallel Hash (Wrong): " + parallelHash);

        // --- Case 2: The forEachOrdered Workaround ---
        System.out.print("Sequential via forEachOrdered: ");
        hashCodeOf(list.parallelStream());

        // --- Case 3: Stateful pairMap Violation ---
        System.out.println("\nPairMap (Sequential):");
        Stream<String> input = Stream.of("a", "b", "c", "d");
        pairMap(input, (left, right) -> left + "->" + right)
                .forEach(System.out::println);
    }

    static void hashCodeOf(Stream<String> stream) {
        var op = new IntConsumer() {
            int acc = 1;
            public void accept(int value) {
                acc = acc * 31 + value;
            }
        };
        stream.mapToInt(Objects::hashCode).forEachOrdered(op);
        System.out.println(op.acc);
    }

    static <T, R> Stream<R> pairMap(Stream<T> input, BiFunction<T, T, R> mapper) {
        var op = new Function<T, R>() {
            boolean started = false;
            T prev = null;

            public R apply(T t) {
                R result = started ? mapper.apply(prev, t) : null;
                started = true;
                prev = t;
                return result;
            }
        };
        // Violation: 'op' is stateful! This will break in .parallel()
        return input.map(op).filter(Objects::nonNull);
    }
}