package puzzlers.effectivejava.ch7.item46;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.*;

/**
 * <h2>Prefer side-effect-free functions in streams</h2>
 *
 * <p>
 * <b>Core Principle:</b> The Streams API is not just a library; it is a paradigm based on
 * functional programming. To use it correctly, computations must be structured as a
 * sequence of transformations where each stage is a <b>pure function</b>. A pure function
 * depends only on its input and does not update any mutable state or rely on external state.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Readability:</b> Pure pipelines are declarative; they describe *what* is happening
 * rather than *how* state is being manipulated.</li>
 * <li><b>Parallelizability:</b> Functions without side effects can be executed in parallel
 * safely without complex synchronization (Item 48).</li>
 * <li><b>Maintainability:</b> Side-effect-free code is easier to test and reason about
 * because there is no "spooky action at a distance" involving shared mutable state.</li>
 * </ul>
 *
 * <h3>Limitations / Common Pitfalls</h3>
 * <ul>
 * <li><b>The "forEach" Trap:</b> Using {@code forEach} to perform computation (e.g., updating a map)
 * is a "bad smell." {@code forEach} should only be used to report results.</li>
 * <li><b>Collector Complexity:</b> The {@code Collectors} API is vast and can be
 * intimidating, though most benefits come from a few key methods.</li>
 * <li><b>The "forEach" Limitation:</b> {@code forEach} is explicitly iterative and
 * not amenable to parallelization compared to proper reductions/collections.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item42 Lambdas
 * @see puzzlers.effectivejava.ch7.item45 UseStreamsJudiciously
 * @see puzzlers.effectivejava.ch7.item48 ParallelStreamsJudiciously
 * @see puzzlers.effectivejava.ch9.item59 KnowLibrary
 */
public class StreamParadigm {

    /**
     * BAD: Iterative code masquerading as streams.
     * Mutates external state ('freq') within a forEach block.
     */
    public void badFrequencyTable(List<String> words) {
        Map<String, Long> freq = new HashMap<>();
        words.forEach(word -> {
            freq.merge(word.toLowerCase(), 1L, Long::sum);
        });
    }

    /**
     * PREFERRED: Functional approach using a Collector.
     * The reduction strategy is encapsulated, and the pipeline is pure.
     */
    public Map<String, Long> goodFrequencyTable(Stream<String> words) {
        return words.collect(groupingBy(String::toLowerCase, counting()));
    }

    /**
     * Demonstrates using toList and Comparators with streams.
     */
    public List<String> getTopTenWords(Map<String, Long> freq) {
        return freq.keySet().stream()
                .sorted(comparing(freq::get).reversed())
                .limit(10)
                .collect(toList());
    }

    /**
     * Demonstrates different toMap strategies.
     */
    public void mapCollectorsExample(Stream<Album> albums) {
        // 1. Simple toMap (fails on duplicate keys)
        // Map<Artist, Album> map = albums.collect(toMap(Album::artist, a -> a));

        // 2. toMap with a merge function (BinaryOperator)
        // Pick the best-selling album per artist
        Map<Artist, Album> topHits = albums.collect(
                toMap(Album::artist, e -> e, maxBy(comparing(Album::sales)))
        );

        // 3. toMap with last-write-wins policy
        // toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal)
    }

    /**
     * Demonstrates joining and complex grouping.
     */
    public String joiningExample(List<String> list) {
        return list.stream().collect(joining(", ", "[", "]"));
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        StreamParadigm example = new StreamParadigm();
        List<String> words = Arrays.asList("hello", "world", "hello", "streams");

        Map<String, Long> freq = example.goodFrequencyTable(words.stream());
        System.out.println("Frequency Map: " + freq);

        String joined = example.joiningExample(words);
        System.out.println("Joined Words: " + joined);
    }

    // Mock classes for demonstration
    static class Artist {
    }

    static class Album {
        Artist artist() {
            return new Artist();
        }

        int sales() {
            return 1000;
        }
    }
}