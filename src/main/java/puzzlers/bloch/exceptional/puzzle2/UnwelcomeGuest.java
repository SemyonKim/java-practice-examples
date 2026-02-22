package puzzlers.bloch.exceptional.puzzle2;

/**
 * The Definite Unassignment Paradox
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 38
 * <p>
 * Example:
 *     private static final long GUEST_USER_ID = -1;
 *     private static final long USER_ID; // blank final
 *     static {
 *         try {
 *             USER_ID = getUserIdFromEnvironment();
 *         } catch (IdUnavailableException e){
 *             USER_ID = GUEST_USER_ID;
 *             System.out.println("Logging in as guest");
 *         }
 *     }
 * <p>
 * Problem:
 * Why does the compiler reject a blank final assignment inside a
 * try-catch block, even if it's logically impossible to assign it twice?
 * <p>
 * Explanation:
 * - JLS 16 (Definite Assignment): A blank final variable must be
 * definitely unassigned (DU) at the point of assignment.
 * - In the provided code, the compiler worries that `USER_ID` might have
 * been assigned in the `try` block *before* the exception was thrown.
 * - Even though we know `getUserIdFromEnvironment()` fails before
 * assigning the value, the compiler's analysis is not that granular.
 * It sees a potential path where an assignment occurs and then a
 * catch block attempts a second assignment.
 * <p>
 * Step-by-step:
 * 1. The compiler enters the `static` block.
 * 2. It sees `USER_ID = ...` in the try block.
 * 3. It sees `USER_ID = ...` in the catch block.
 * 4. Because it cannot guarantee the first assignment didn't happen
 * successfully before an exception occurred, it flags "variable
 * USER_ID might already have been assigned."
 * <p>
 * Lesson:
 * - Avoid using complex control flow (try-catch) to initialize
 * blank final fields.
 * - The compiler's "Definite Unassignment" analysis is conservative
 * and will reject code that a human can see is safe.
 * <p>
 * Improvement:
 * Initialize the field at the point of declaration using a helper method.
 */
public class UnwelcomeGuest {
    private static final long GUEST_USER_ID = -1;

    // Improvement: Direct initialization via a helper method
    private static final long USER_ID = initializeUserId();

    private static long initializeUserId() {
        try {
            return getUserIdFromEnvironment();
        } catch (IdUnavailableException e) {
            System.out.println("Logging in as guest");
            return GUEST_USER_ID;
        }
    }

    private static long getUserIdFromEnvironment() throws IdUnavailableException {
        throw new IdUnavailableException();
    }

    public static void main(String[] args) {
        System.out.println("User ID: " + USER_ID);
    }
}

class IdUnavailableException extends Exception {}