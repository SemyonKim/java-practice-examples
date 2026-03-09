package puzzlers.effectivejava.ch9.item66;

/**
 * <h2>Use native methods judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> The Java Native Interface (JNI) allows Java to call code written in
 * languages like C or C++. While once necessary for performance or OS access, modern JVMs
 * and evolved Java APIs have made native methods rarely necessary and often risky.
 * </p>
 *
 * <h3>Legitimate Use Cases</h3>
 * <ul>
 * <li><b>Platform-Specific Facilities:</b> Accessing OS features not yet in the Java API
 * (though Java 9's Process API and others have filled many gaps).</li>
 * <li><b>Legacy Libraries:</b> Interfacing with existing, high-quality native code or
 * legacy data where no Java equivalent exists.</li>
 * <li><b>Extreme Performance:</b> In very rare cases involving specialized math (e.g., GMP
 * for multiprecision arithmetic) where Java cannot match highly tuned native libraries.</li>
 * </ul>
 *
 * <h3>Disadvantages</h3>
 * <ul>
 * <li><b>Safety:</b> Native languages are not memory-safe. A single buffer overflow in C
 * can corrupt the entire JVM memory space.</li>
 * <li><b>Portability:</b> Native code is platform-dependent; you must compile and
 * distribute separate binaries for every OS/architecture.</li>
 * <li><b>Debugging & Maintenance:</b> Native code is harder to debug and requires
 * "glue code" that is tedious to write and difficult to read.</li>
 * <li><b>Garbage Collection:</b> The GC cannot track or reclaim native memory,
 * leading to potential leaks if not managed manually.</li>
 * </ul>
 *
 * <h3>Best Practices</h3>
 * <ul>
 * <li><b>Minimize Surface Area:</b> Use as little native code as possible; keep the
 * logic in Java and use native methods only for the specific low-level call.</li>
 * <li><b>Test Rigorously:</b> Because native bugs are catastrophic, thorough testing
 * is mandatory.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item8 AvoidFinalizersAndCleaners
 * @see puzzlers.effectivejava.ch8.item50 DefensiveCopies
 */
public class NativeMethods {

    /**
     * Conceptual example of a native method declaration.
     * Note: This requires a corresponding C/C++ implementation and
     * System.loadLibrary() call.
     */
    public native void performPlatformSpecificTask();

    /**
     * BETTER: Use modern Java APIs instead of native code where possible.
     * Example: Prior to Java 9, managing OS processes often required JNI.
     */
    public void modernProcessManagement() {
        // Use the Java 9 Process API instead of writing C code
        ProcessHandle currentProcess = ProcessHandle.current();
        System.out.println("Current PID: " + currentProcess.pid());
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        NativeMethods demo = new NativeMethods();

        System.out.println("Checking OS details via Java Standard API...");
        demo.modernProcessManagement();

        System.out.println("\nReminder: Only use native methods if:");
        System.out.println("1. No Java API exists (like ProcessHandle or java.math).");
        System.out.println("2. Performance gains are proven via benchmarks (e.g., using GMP).");
        System.out.println("3. You are prepared to manage memory and portability manually.");
    }
}