package puzzlers.bloch.libraryadvanced.puzzle4;

import java.io.IOException;
import java.io.InputStream;

/**
 * Beer Blast: The Hung Process Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 82
 * <p>
 * Problem:
 * Why does the "Master" process hang indefinitely and never print the
 * exit value, even though the "Slave" process is simple and should
 * terminate quickly?
 * <p>
 * Explanation:
 * - When a subprocess is created via {@code Runtime.exec}, the operating
 * system provides a limited buffer (pipe) for the process's standard output
 * and error streams.
 * - If the subprocess writes more data than the buffer can hold (e.g., 99
 * verses of a song), the subprocess will **block** and wait for the parent
 * process to read that data.
 * - If the parent process (the Master) calls {@code waitFor()} without
 * reading the subprocess's output, it waits for the subprocess to exit.
 * - We reach a **deadlock**: the Slave waits for the Master to drain the
 * buffer, and the Master waits for the Slave to finish.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Subprocess Execution:** The Slave starts singing and filling the
 * {@code InputStream} of the Master.
 * 2. **Buffer Overflow:** The OS buffer fills up. The Slave's {@code println}
 * call hangs.
 * 3. **The Wait:** The Master calls {@code process.waitFor()}, going to sleep.
 * 4. **Deadlock:** Both processes are now waiting for each other to act.
 * <p>
 * The Fix (Draining the Stream):
 * - To ensure the Slave can always finish, the Master must actively "drain"
 * the input stream.
 * - This must often be done in a **separate thread** to prevent the Master
 * from blocking on the read operation itself if it has other tasks.
 * <p>
 * Lesson:
 * - Never assume a subprocess will run to completion if it produces output.
 * - Always drain the output and error streams of a subprocess created
 * via {@code Runtime.exec} or {@code ProcessBuilder}.
 * - Since Java 1.8 (and improved in Java 9+), {@code ProcessBuilder} {@link BeerBlastModern} makes
 * redirection to files or {@code inheritIO()} much easier to manage.
 * <p>
 * Output:
 * // Program hangs indefinitely (deadlock)
 */
public class BeerBlast {
    static final String COMMAND = "java BeerBlast slave";

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("slave")) {
            for (int i = 99; i > 0; i--) {
                System.out.println(i + " bottles of beer on the wall");
                System.out.println(i + " bottles of beer");
                System.out.println("You take one down, pass it around,");
                System.out.println((i - 1) + " bottles of beer on the wall");
                System.out.println();
            }
        } else {
            // Master: Starts the subprocess
            Process process = Runtime.getRuntime().exec(COMMAND);
            //'exec(java.lang.String)' is deprecated since version 18

            // FIX: Draining the stream prevents the buffer from clogging
            // drainInBackground(process.getInputStream());

            // BUG: Without draining, this call will wait forever
            int exitValue = process.waitFor();
            System.out.println("exit value = " + exitValue);
        }
    }

    /**
     * Drains the InputStream in a background thread to prevent
     * the subprocess from blocking on its output buffer.
     */
    static void drainInBackground(final InputStream is) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Read until the end of the stream
                    while (is.read() >= 0);
                } catch (IOException e) {
                    // Exit on error
                }
            }
        }).start();
    }
}