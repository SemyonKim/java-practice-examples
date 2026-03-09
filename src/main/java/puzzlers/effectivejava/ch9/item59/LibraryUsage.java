package puzzlers.effectivejava.ch9.item59;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <h2>Know and use the libraries</h2>
 *
 * <p>
 * <b>Core Principle:</b> Avoid reinventing the wheel. By using standard libraries, you leverage
 * the expertise of specialists, benefit from continuous performance improvements, and keep
 * your code in the mainstream of the developer community.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Expert Design:</b> Library code is written, reviewed, and tested by experts. For example,
 * {@code Random.nextInt(int)} correctly handles edge cases in two's complement arithmetic that
 * ad hoc implementations often miss.</li>
 * <li><b>Performance:</b> Standard libraries are highly optimized and frequently rewritten to
 * improve speed. {@code ThreadLocalRandom} is significantly faster and higher quality than {@code Random}.</li>
 * <li><b>Maintenance & Readability:</b> Using standard APIs makes code more readable and maintainable
 * for other developers who are already familiar with those libraries.</li>
 * <li><b>Evolution:</b> Libraries gain new functionality over time (e.g., {@code InputStream.transferTo}
 * in Java 9), allowing you to solve complex tasks with minimal code.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Specialized Needs:</b> While libraries cover most use cases, highly specialized requirements
 * might not be met by the standard platform.</li>
 * <li><b>Functional Gaps:</b> There will always be "holes" in any finite set of libraries. In such
 * cases, high-quality third-party libraries like Google's Guava should be the next choice before
 * manual implementation.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item45 Streams
 * @see puzzlers.effectivejava.ch11.item80 ExecutorService
 * @see puzzlers.effectivejava.ch11.item81 ConcurrencyUtilities
 */
public class LibraryUsage {

    /**
     * Common but DEEPLY FLAWED implementation of a random number generator.
     * Flaws:
     * 1. Short repetition periods if n is a small power of two.
     * 2. Distribution bias (numbers in the lower half returned more frequently).
     * 3. Catastrophic failure: Math.abs(Integer.MIN_VALUE) is still negative!
     */
    static int flawedRandom(int n, Random rnd) {
        // This can return a negative number if nextInt() is Integer.MIN_VALUE
        return Math.abs(rnd.nextInt()) % n;
    }

    /**
     * Preferred way to generate random integers as of Java 7.
     * Faster, better quality, and thread-safe without contention.
     */
    public int getCorrectRandom(int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }

    /**
     * Demonstrates using Java 9+ library methods to simplify I/O tasks.
     * This replaces a complex loop with a single library call.
     */
    public void printUrlContent(String urlString) throws IOException {
        try (InputStream in = new URL(urlString).openStream()) {
            // transferTo added in Java 9 simplifies stream copying significantly
            in.transferTo(System.out);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        LibraryUsage demo = new LibraryUsage();
        int n = 2 * (Integer.MAX_VALUE / 3);
        Random rnd = new Random();

        // 1. Demonstrate the flaw in the ad hoc implementation
        int low = 0;
        for (int i = 0; i < 1_000_000; i++) {
            // Using a try-catch because flawedRandom can return negative values
            // leading to potential IndexOutOfBounds in real-world apps.
            if (flawedRandom(n, rnd) < n / 2) {
                low++;
            }
        }
        System.out.println("Flawed distribution count (should be ~500k): " + low);

        // 2. Using the modern library approach
        int correctLow = 0;
        for (int i = 0; i < 1_000_000; i++) {
            if (demo.getCorrectRandom(n) < n / 2) {
                correctLow++;
            }
        }
        System.out.println("Library distribution count (Correct): " + correctLow);

        // 3. Example of using library functionality for URLs (Java 9+)
        try {
            System.out.println("\nFetching URL content via library:");
            // Using a placeholder; in a real scenario, use args[0]
            // demo.printUrlContent("https://www.google.com");
        } catch (Exception e) {
            System.err.println("URL Error: " + e.getMessage());
        }
    }
}