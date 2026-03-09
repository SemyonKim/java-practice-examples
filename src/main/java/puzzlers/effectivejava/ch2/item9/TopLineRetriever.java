package puzzlers.effectivejava.ch2.item9;

import java.io.*;

/**
 * <h2>Prefer try-with-resources to try-finally</h2>
 *
 * <p>
 * <b>Core Principle:</b> Always use try-with-resources for any resource that implements
 * {@code AutoCloseable}. It is shorter, clearer, and preserves the primary exception
 * by suppressing secondary exceptions thrown during closure.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Readability:</b> Eliminates the "pyramid of doom" caused by nested try-finally
 * blocks when managing multiple resources.</li>
 * <li><b>Superior Diagnostics:</b> If an exception occurs in both the logic and the
 * auto-closure, the first exception is preserved, and the closure exception is
 * "suppressed" rather than lost.</li>
 * <li><b>Safety:</b> Makes it virtually impossible to forget to close a resource,
 * which was a common error even for seasoned developers using try-finally.</li>
 * <li><b>Conciseness:</b> Allows handling exceptions within the same block using
 * catch clauses without further nesting.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>AutoCloseable Requirement:</b> The resource must implement the {@code AutoCloseable}
 * interface (which is true for almost all Java library resources).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item8 AvoidFinalizersAndCleaners
 */
public class TopLineRetriever {

    private static final int BUFFER_SIZE = 8192;

    /**
     * try-with-resources on a single resource.
     * Shorter and more reliable than the old try-finally block.
     */
    static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

    /**
     * try-with-resources on multiple resources.
     * Note how both 'in' and 'out' are declared in the try-specifier.
     * Both are guaranteed to close correctly.
     */
    static void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }

    /**
     * try-with-resources with a catch clause.
     * Allows providing a default value without additional nesting levels.
     */
    static String firstLineOfFile(String path, String defaultVal) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            // Log the suppressed/actual exception here if needed
            return defaultVal;
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        String path = "example.txt";

        try {
            // Writing a dummy file for the test
            try (FileWriter writer = new FileWriter(path)) {
                writer.write("First Line\nSecond Line");
            }

            System.out.println("Result: " + firstLineOfFile(path));

            // Example of the version with a default value
            System.out.println("Missing File Result: " +
                    firstLineOfFile("non_existent.txt", "Default Content"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}