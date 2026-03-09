package puzzlers.effectivejava.ch7.item48;

import java.math.BigInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;

/**
 * <h2>Use caution when making streams parallel</h2>
 *
 * <p>
 * <b>Core Principle:</b> Parallelizing a stream is a performance optimization that should
 * only be applied when you have evidence that it will preserve correctness (safety)
 * and significantly increase speed (liveness). It is most effective when the data source
 * is easily splittable and the terminal operation is a reduction.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Near-Linear Speedup:</b> Under the right conditions (e.g., machine learning,
 * heavy data processing), performance can scale proportionally with the number of CPU cores.</li>
 * <li><b>Ease of Use:</b> Parallelization can be toggled via a single method call: {@code parallel()}.</li>
 * <li><b>Locality of Reference:</b> Best performance is achieved with primitive arrays
 * where data is stored contiguously, minimizing cache misses.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Liveness Failures:</b> Pipelines using {@code Stream.iterate} or {@code limit()}
 * can cause CPU spikes or hang indefinitely when parallelized.</li>
 * <li><b>Safety Failures:</b> Non-associative, interfering, or stateful function objects
 * can lead to incorrect results or unpredictable behavior.</li>
 * <li><b>Overhead:</b> The cost of splitting streams and combining results can make
 * parallel streams slower than sequential ones for small datasets or complex operations.</li>
 * <li><b>Shared Pool Contention:</b> Most parallel streams use a common fork-join pool;
 * one misbehaving pipeline can degrade the entire system's performance.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item45 Streams
 * @see puzzlers.effectivejava.ch7.item46 FunctionalSideEffects
 */
public class ParallelStreamCaution {

    /**
     * BAD: Potential liveness failure.
     * Parallelizing iterate() and limit() is disastrous.
     * The algorithm struggles to predict which elements to process next.
     */
    public static void printMersennePrimesParallel() {
        // Warning: This may hang your CPU indefinitely!
        primes().parallel()
                .map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }

    private static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime);
    }

    /**
     * GOOD: Effective parallelization.
     * Uses LongStream.rangeClosed (easily splittable) and count() (efficient reduction).
     * Calculating pi(n) - the number of primes less than or equal to n.
     */
    public static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .parallel()
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        long n = 100_000; // Small n for demonstration

        System.out.println("Calculating primes up to " + n + "...");

        long start = System.currentTimeMillis();
        long count = pi(n);
        long end = System.currentTimeMillis();

        System.out.println("Found " + count + " primes in " + (end - start) + "ms");

        /*
         * Note: In a real scenario, you'd test both sequential and parallel versions
         * to verify that parallel() actually provides a benefit (Item 67).
         */
    }
}