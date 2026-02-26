package puzzlers.valeev.expressions.mistake4;

import java.util.*;

/**
 * Binding a Method Reference to the Wrong Method
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 13
 * <p>
 * Problem:
 * Method references are more compact than lambdas because they omit explicit parameters.
 * However, this brevity hides which overload or constructor is actually being called.
 * If a functional interface passes an argument you intended to ignore, the compiler
 * may bind the reference to an unexpected overload that accepts that argument.
 * <p>
 * Expected behavior:
 * - In {@code computeIfAbsent}, the developer expects to create a new, empty {@code StringBuilder}.
 * - In {@code Arrays.setAll}, the developer expects to initialize an array with empty {@code StringBuilder} objects.
 * <p>
 * Actual behavior:
 * - In {@code computeIfAbsent(key, StringBuilder::new)}, the {@code key} is passed to the
 * {@code StringBuilder(String)} constructor, prefixing all content with the filename.
 * - In {@code Arrays.setAll(array, StringBuilder::new)}, the array {@code index} is passed
 * to {@code StringBuilder(int capacity)}, potentially causing massive memory allocation (OOM)
 * for large arrays.
 * <p>
 * Explanation:
 * - Implicit Arguments: Many functional interfaces pass arguments (like the key in a Map
 * or the index in an array).
 * - Overload Resolution: Method references like {@code StringBuilder::new} are ambiguous
 * to the human eye but specific to the compiler. The compiler looks for a constructor
 * matching the functional interface's descriptor.
 * - Map.computeIfAbsent: Uses {@code Function<? super K, ? extends V>}. This function
 * takes the Key as an argument. Therefore, {@code StringBuilder::new} binds to
 * {@code new StringBuilder(String)}.
 * - Arrays.setAll: Uses {@code IntFunction<? extends T>}. This takes the index as an
 * argument. Therefore, {@code StringBuilder::new} binds to {@code new StringBuilder(int capacity)}.
 * <p>
 * Fixes:
 * - Use a lambda instead of a method reference when you need to explicitly ignore
 * parameters passed by the functional interface.
 * - Be especially cautious with {@code Map.computeIfAbsent()}, {@code Map.merge()},
 * and {@code Arrays.setAll()}.
 * <p>
 * Lesson:
 * - Method references are not always a "drop-in" replacement for lambdas.
 * - When a class has multiple constructors or overloaded methods, a lambda is often
 * safer because it makes the parameter handling explicit.
 */
public class MethodReferenceMistake {

    // --- Example 1: Map.computeIfAbsent Trap ---

    static class Storage {
        private final Map<String, StringBuilder> contents = new TreeMap<>();

        void addMistake(String fileName, String line) {
            // MISTAKE: StringBuilder::new binds to new StringBuilder(String fileName)
            contents.computeIfAbsent(fileName, StringBuilder::new)
                    .append(line).append("\n");
        }

        void addFixed(String fileName, String line) {
            // FIX: Use lambda to ignore the 'fileName' argument and call the default constructor
            contents.computeIfAbsent(fileName, s -> new StringBuilder())
                    .append(line).append("\n");
        }

        void dump() {
            contents.forEach((fileName, content) -> {
                System.out.println("File name: " + fileName);
                System.out.print("Content: " + content);
            });
        }
    }

    // --- Example 2: Arrays.setAll Memory Risk ---

    static void arrayExample() {
        int size = 10000;
        StringBuilder[] array = new StringBuilder[size];

        // MISTAKE: Binds to new StringBuilder(int capacity) using the array index.
        // For index 9999, it allocates a buffer for 9999 characters immediately.
        // Arrays.setAll(array, StringBuilder::new);

        // FIX: Use lambda to ensure the default capacity is used.
        Arrays.setAll(array, i -> new StringBuilder());
    }

    public static void main(String[] args) {
        System.out.println("--- Running Mistake Version ---");
        Storage storageMistake = new Storage();
        storageMistake.addMistake("users.txt", "admin");
        storageMistake.addMistake("users.txt", "guest");
        storageMistake.dump();

        System.out.println("\n--- Running Fixed Version ---");
        Storage storageFixed = new Storage();
        storageFixed.addFixed("users.txt", "admin");
        storageFixed.addFixed("users.txt", "guest");
        storageFixed.dump();
    }
}