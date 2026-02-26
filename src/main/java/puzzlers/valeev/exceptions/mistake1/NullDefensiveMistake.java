package puzzlers.valeev.exceptions.mistake1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Avoiding Nulls and Defensive Checks
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 41, Section 1
 * <p>
 * Problem:
 * NullPointerExceptions (NPEs) often occur late, far from the source of the null value.
 * This "delayed failure" makes debugging difficult and puts the program in an invalid state.
 * <p>
 * Expected behavior:
 * Developers expect methods to receive valid data or fail immediately (fail-fast) if
 * an invalid null is provided. They also expect collections and "container" types
 * (Optional, Stream) to be non-null.
 * <p>
 * Actual behavior:
 * - Silent Corruption: Storing a null in a field without checking it leads to NPEs
 * much later during dereferencing.
 * - Unboxing Trap: Using {@code Boolean} as a 3-state logic (true, false, null) causes
 * a hidden NPE when the JVM tries to auto-unbox a null into a primitive {@code boolean}.
 * - Legacy IO: Methods like {@code File.list()} return {@code null} on errors,
 * causing crashes in for-each loops.
 * <p>
 *
 * <p>
 * Explanation:
 * - Defensive Sanitization: Using {@code Objects.requireNonNull()} at the entry point
 * of public methods/constructors ensures the stack trace points directly to the caller
 * who provided the null.
 * - Collection Integrity: Java 10's {@code List.copyOf()} provides an unmodifiable,
 * null-hostile copy that is performance-optimized to avoid redundant copying if the
 * input is already a result of {@code copyOf()}.
 * - The "Empty" Rule: Collections, Arrays, Streams, and Optionals have a built-in
 * state for "no data" (empty). Using {@code null} for these types adds a redundant
 * and dangerous second state for "no data."
 * <p>
 * Ways to Avoid:
 * - Use {@code Objects.requireNonNull(param)} for mandatory fields.
 * - Return empty collections ({@code Collections.emptyList()}) or empty Optionals
 * instead of {@code null}.
 * - Use NIO {@code Files.list()} which throws {@code IOException} instead of the
 * null-returning {@code File.list()}.
 * - Replace 3-state {@code Boolean} with a clear, named {@code Enum}.
 * - Use {@code List.copyOf(data)} to enforce non-nullability and immutability efficiently.
 * <p>
 * Lesson:
 * - Null is not a substitute for "Empty" or "Error."
 * - Fail fast at the boundaries to keep the core logic clean.
 * - Enums provide better semantic meaning and type safety than null-prone wrappers.
 */
public class NullDefensiveMistake {

    // --- Example 1: Fail-Fast Constructor ---
    public static class Person {
        private final String firstName;
        private final String lastName;

        public Person(String firstName, String lastName) {
            // Immediate NPE if null, rather than storing it for later failure
            this.firstName = Objects.requireNonNull(firstName, "First name is required");
            this.lastName = Objects.requireNonNull(lastName, "Last name is required");
        }
    }

    // --- Example 2: The Collection Copy Strategy ---
    public void processData(Collection<String> data) {
        // Java 10+: Efficiently ensures no nulls and provides immutability
        Collection<String> safeData = List.copyOf(data);
        safeData.forEach(System.out::println);
    }

    // --- Example 3: IO vs NIO (NPE Prevention) ---
    public void listFilesLegacy(String path) {
        String[] files = new File(path).list();
        // DANGER: If path is invalid, 'files' is null, and this loop throws NPE
        for (String file : files) {
            System.out.println(file);
        }
    }

    public void listFilesNIO(String path) {
        // SAFE: Throws IOException which can be caught and handled
        try (Stream<Path> stream = Files.list(Path.of(path))) {
            stream.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Failed to list files: " + e.getMessage());
        }
    }

    // --- Example 4: The Boolean/Enum Trap ---
    enum ItemAction {
        MOVE_TO_WISH_LIST,
        REMOVE_FROM_BASKET,
        CANCEL
    }

    public void handleItemRemoval(ItemAction action) {
        //
        switch (action) {
            case MOVE_TO_WISH_LIST -> System.out.println("Moving...");
            case REMOVE_FROM_BASKET -> System.out.println("Removing...");
            case CANCEL -> System.out.println("Operation aborted.");
        }
    }

    public static void main(String[] args) {
        try {
            new Person(null, "Doe");
        } catch (NullPointerException e) {
            System.out.println("Caught fast-fail: " + e.getMessage());
        }
    }
}