package puzzlers.valeev.comparing.mistake9;

import java.util.*;

/**
 * Failing to Represent an Object as a Sequence of Keys
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 67
 * <p>
 * Problem:
 * Why do custom compareTo implementations frequently trigger
 * IllegalArgumentException: "Comparison method violates its general contract!"?
 * <p>
 * Expected behavior:
 * Developers often try to implement "shortcuts" or special-case logic (like
 * "put secure connections first" or "return 0 if a field is null") to simplify
 * the comparison.
 * <p>
 * Actual behavior:
 * These shortcuts often violate symmetry or transitivity. For instance, if
 * a.compareTo(b) is -1, then b.compareTo(a) MUST be 1. If your logic returns
 * -1 for both, the contract is broken.
 * <p>
 * Explanation:
 * - The Sequence of Keys: A robust comparison should treat an object as a
 * ordered tuple of keys (K_1, K_2, ..., K_n). You compare K_1; if
 * equal, move to K_2, and so on.
 * - Special Cases as Keys: A condition like "is secure" isn't a special rule;
 * it is simply the first key (K_1) in the sequence, represented as a boolean.
 * - The "Don't Care" Trap: Returning 0 because you "don't care" about the
 * order of two unequal objects is dangerous. If A = B and B = C,
 * then A must equal C. If your "don't care" logic breaks this,
 * sorting algorithms like TimSort will fail.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: The SecureConnection Bug
 * - Comparison logic: `if (this instanceof SecureConnection) return -1;`
 * - When comparing two SecureConnections (S_1 and S_2):
 * - S_1.compareTo(S_2) returns -1.
 * - S_2.compareTo(S_1) also returns -1.
 * - This is a total violation of symmetry (sgn(x.compareTo(y)) == -sgn(y.compareTo(x))).
 * <p>
 * Fixes:
 * - Use JDK Combinators: `Comparator.comparing(...).thenComparing(...)`.
 * - Use Guava ComparisonChain: `ComparisonChain.start().compare(...).result()`.
 * - Explicit Key Sequence: Explicitly compare boolean flags or nullity
 * status as the first elements in your comparison chain.
 * <p>
 * Lesson:
 * - Never write "naked" if-statements in compareTo if you can avoid it.
 * - Every "special rule" is just another key in the sequence.
 * - If two objects are not equal, the comparator must provide a consistent
 * deterministic order, even if that order seems arbitrary.
 * <p>
 * Output:
 * - u1.compareTo(u2): 0
 * - u1.compareTo(u3): 0
 * - u2.compareTo(u3): -1 (Transitivity Failure: u2 != u3 but both "equal" u1)
 */
public class ComparisonSequenceMistake {

    // Incorrect: Fails on symmetry when both are SecureConnection
    static class Connection implements Comparable<Connection> {
        final String url;
        Connection(String url) { this.url = url; }

        @Override
        public int compareTo(Connection that) {
            if (this instanceof SecureConnection) return -1;
            if (that instanceof SecureConnection) return 1;
            return this.url.compareTo(that.url);
        }
    }

    static class SecureConnection extends Connection {
        SecureConnection(String url) { super(url); }
    }

    // Incorrect: Fails on transitivity with nulls
    record User(String name) implements Comparable<User> {
        public int compareTo(User o) {
            if (name != null && o.name != null) {
                return name.compareTo(o.name);
            }
            return 0; // BUG: returns 0 if either is null
        }
    }

    public static void main(String[] args) {
        // Example 1: Symmetry Failure
        Connection s1 = new SecureConnection("a.com");
        Connection s2 = new SecureConnection("b.com");
        System.out.println("--- Symmetry Failure ---");
        System.out.println("s1 vs s2: " + s1.compareTo(s2));
        System.out.println("s2 vs s1: " + s2.compareTo(s1)); // Both are -1!

        // Example 2: Transitivity Failure
        User u1 = new User(null);
        User u2 = new User("Mary");
        User u3 = new User("Bill");
        System.out.println("\n--- Transitivity Failure ---");
        System.out.println("u1 vs u2: " + u1.compareTo(u2)); // 0
        System.out.println("u1 vs u3: " + u1.compareTo(u3)); // 0
        System.out.println("u2 vs u3: " + u2.compareTo(u3)); // Non-zero! (Violation)

        // Example 3: Corrected via Key Sequence
        System.out.println("\n--- Corrected Logic (User) ---");
        Comparator<User> userComparator = Comparator.comparing(
                User::name,
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        System.out.println("u1 vs u2 (Fixed): " + userComparator.compare(u1, u2));
        System.out.println("u2 vs u1 (Fixed): " + userComparator.compare(u2, u1));
    }
}