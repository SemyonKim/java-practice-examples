package puzzlers.bloch.libraryadvanced.puzzle4;

/**
 * Modern Fix for The Beer Blast: The Hung Process Trap {@link BeerBlast}: ProcessBuilder
 * <p>
 * Instead of manually draining streams with background threads,
 * ProcessBuilder allows us to redirect the output directly to the
 * parent process or a file.
 */
public class BeerBlastModern {
    static final String[] COMMAND = {"java", "BeerBlastModern", "slave"};

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
            ProcessBuilder pb = new ProcessBuilder(COMMAND);

            // FIX OPTION A: Inherit IO
            // This redirects the slave's output directly to the Master's
            // console, so the OS buffer never fills up.
            pb.inheritIO();

            // FIX OPTION B: Redirect to a Null file (Discard output)
            // pb.redirectOutput(ProcessBuilder.Redirect.to(new File("/dev/null")));
            // Use "NUL" on Windows

            Process process = pb.start();

            // No deadlock: The output is being handled by the OS/Parent
            int exitValue = process.waitFor();
            System.out.println("exit value = " + exitValue);
        }
    }
}