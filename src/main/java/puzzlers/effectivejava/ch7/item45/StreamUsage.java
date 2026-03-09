package puzzlers.effectivejava.ch7.item45;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * <h2>Use streams judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> While the Streams API is powerful and versatile, it should be used only
 * where it actually improves code clarity. Overusing streams can lead to "code smell" where
 * pipelines become unreadable, unmaintainable, and sometimes less performant.
 * </p>
 *
 * <h3>Advantages of Streams</h3>
 * <ul>
 * <li><b>Conciseness:</b> Can transform complex iterative logic into a single, fluent expression.</li>
 * <li><b>Declarative Style:</b> Focuses on *what* should be done (filtering, mapping, collecting)
 * rather than *how* to loop over elements.</li>
 * <li><b>Versatility:</b> Easily handles common patterns like uniform transformations,
 * filtering, searching, and grouping.</li>
 * <li><b>Lazy Evaluation:</b> Postpones computation until a terminal operation is invoked,
 * enabling the processing of infinite streams.</li>
 * </ul>
 *
 * <h3>Limitations and Disadvantages</h3>
 * <ul>
 * <li><b>Readability Hazards:</b> Overly complex pipelines ("Stream Overuse") are harder
 * to read than simple loops, especially for those not expert in functional programming.</li>
 * <li><b>Variable Access:</b> Lambdas can only access final or effectively final local variables
 * and cannot modify them.</li>
 * <li><b>Control Flow:</b> You cannot return from an enclosing method, break, or continue
 * from within a stream pipeline.</li>
 * <li><b>Primitive Char Support:</b> Java lacks a {@code Stream<char>}; {@code String.chars()}
 * returns an {@code IntStream}, which can lead to confusing results and requires explicit casting.</li>
 * <li><b>Lost Context:</b> Accessing original values from early stages of a pipeline after
 * they have been mapped is difficult without creating "Pair" objects.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item42 Lambdas
 * @see puzzlers.effectivejava.ch7.item46 UseSideEffectFreeFunctions
 * @see puzzlers.effectivejava.ch7.item48 ParallelStreamsJudiciously
 */
public class StreamUsage {

    // --- Example 1: Anagrams (The Happy Medium) ---

    /**
     * PREFERRED: Tasteful use of streams.
     * Combines the strength of a Map for grouping with a stream for filtering/printing.
     */
    public static void printAnagrams(String path, int minGroupSize) throws IOException {
        Path dictionary = Paths.get(path);
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(StreamUsage::alphabetize))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(g.size() + ": " + g));
        }
    }

    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    // --- Example 2: Mersenne Primes (Infinite Streams) ---

    static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime);
    }

    /**
     * Demonstrates an infinite stream pipeline and the "inversion" workaround
     * to access original values (exponents) lost during mapping.
     */
    public static void printMersennePrimes(int limit) {
        primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(limit)
                .forEach(mp -> System.out.println(mp.bitLength() + ": " + mp));
    }

    // --- Example 3: Cartesian Product (Iterative vs Stream) ---

    /**
     * ITERATIVE: Often clearer for simple nested loops.
     */
    private static List<String> cartesianIterative(List<String> suits, List<String> ranks) {
        List<String> result = new ArrayList<>();
        for (String suit : suits)
            for (String rank : ranks)
                result.add(rank + " of " + suit);
        return result;
    }

    /**
     * STREAM-BASED: Concise, but uses nested lambdas (flatMap).
     * Use if your team is comfortable with functional style.
     */
    private static List<String> cartesianStream(List<String> suits, List<String> ranks) {
        return suits.stream()
                .flatMap(suit -> ranks.stream()
                        .map(rank -> rank + " of " + suit))
                .collect(toList());
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        System.out.println("--- Mersenne Primes (Exponent: Value) ---");
        printMersennePrimes(5);

        // Demonstrating the hazard of char streams
        System.out.print("\nChar stream hazard (Expected 'Hello'): ");
        "Hello".chars().forEach(System.out::print); // Prints integers

        System.out.print("\nChar stream fix: ");
        "Hello".chars().forEach(x -> System.out.print((char) x));
        System.out.println();
    }
}