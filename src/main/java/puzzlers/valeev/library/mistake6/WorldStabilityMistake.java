package puzzlers.valeev.library.mistake6;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Assuming the world is stable
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 92
 * <p>
 * Problem:
 * Why does code that checks a condition (like file existence or current time)
 * fail intermittently even when the condition was just verified?
 * <p>
 * Expected behavior:
 * Developers often assume that if a property of the external world (filesystem,
 * system clock, hardware state) is read, it will remain constant for the
 * duration of the method's execution.
 * <p>
 * Actual behavior:
 * - The external world is volatile. Another process can delete a file, or the
 * system clock can tick over a minute boundary between two lines of code.
 * - This leads to "Time-of-Check to Time-of-Use" (TOCTOU) vulnerabilities and
 * "flaky tests" that fail sporadically.
 * <p>
 *
 * <p>
 * Explanation:
 * - Local Stability: Local variables and parameters are safe; they only change
 * when you explicitly assign them.
 * - External Volatility: Filesystems and system time are shared resources.
 * - The Midnight Trap: A test checking the current hour might query {@code now()}
 * at 11:59:59.999 and then query it again for the expected value at 12:00:00.000,
 * causing a mismatch.
 * - Filesystem Races: {@code Files.exists()} followed by {@code Files.size()}
 * is not atomic. The file could be replaced by a directory in between.
 * <p>
 * Step-by-step (The Flaky Backup Test):
 * - Method {@code createBackupFolder} is called; it captures {@code LocalDateTime.now()}.
 * - The test then captures {@code LocalDateTime.now()} again to build the "expected" string.
 * - If the clock rolls over a minute between these two calls, the test fails.
 * - This is a "flaky test": it passes 99.9% of the time but fails randomly.
 * <p>
 * Fixes:
 * - Capture Once: Query the system clock or external state exactly once and
 * store it in a local variable for all subsequent logic.
 * - Atomic Operations: Use {@code Files.readAttributes()} to get size, type,
 * and timestamps in a single system call rather than multiple individual checks.
 * - Boundary Checks: When reading files based on size, use {@code readNBytes(max)}
 * and check if the stream still has data rather than trusting a previous size check.
 * - Dependency Injection: In tests, pass the "current time" as a parameter or
 * use a mockable {@code Clock} to ensure consistency.
 * <p>
 * Lesson:
 * - The external world is not a snapshot; it is a live stream.
 * - Between any two lines of code, the "truth" of the environment can change.
 * <p>
 * Output:
 * - Flaky Test Result: [Success or Failure depending on execution timing]
 * - Atomic Size: [Consistent file size or -1 if not a regular file]
 */
public class WorldStabilityMistake {

    public static void main(String[] args) throws IOException {
        System.out.println("--- Flaky Time Analysis ---");
        simulateFlakyTest();

        System.out.println("\n--- Filesystem TOCTOU Analysis ---");
        Path path = Path.of("test.txt");
        System.out.println("Robust size check: " + getFileSizeRobust(path));
    }

    /**
     * Demonstrates a flaky test where time shifts between check and expectation.
     */
    static void simulateFlakyTest() {
        // Assume this happens inside the logic being tested
        LocalDateTime actualTime = LocalDateTime.now();

        // Simulating a tiny delay or a very unlucky timing at the edge of a minute
        // In a real flaky test, this delay is just the CPU execution time.

        // This is what the test does to "verify" the result
        LocalDateTime expectedTime = LocalDateTime.now();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        String actualStr = actualTime.format(fmt);
        String expectedStr = expectedTime.format(fmt);

        if (!actualStr.equals(expectedStr)) {
            System.out.println("FLAKY TEST FAILED: Actual=" + actualStr + ", Expected=" + expectedStr);
        } else {
            System.out.println("Test Passed (this time).");
        }
    }

    /**
     * Faulty way: Two separate queries to the OS.
     */
    static long getFileSizeFragile(Path path) throws IOException {
        if (!Files.isRegularFile(path)) return -1;
        // RACE CONDITION: File could be changed/deleted here
        return Files.size(path);
    }

    /**
     * Correct way: Use readAttributes for an atomic snapshot.
     */
    static long getFileSizeRobust(Path path) throws IOException {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            if (!attrs.isRegularFile()) {
                return -1;
            }
            return attrs.size();
        } catch (IOException e) {
            return -1; // File disappeared or is inaccessible
        }
    }

    /**
     * Robust reading: Don't trust size(), just try to read the limit.
     */
    static byte[] readFileSafely(Path path) throws IOException {
        final int maxSize = 1024 * 1024;
        try (InputStream is = Files.newInputStream(path)) {
            byte[] data = is.readNBytes(maxSize);
            // Check if there is more data left in the stream
            if (data.length == maxSize && is.read() != -1) {
                throw new UnsupportedOperationException("File grew too large during reading!");
            }
            return data;
        }
    }
}