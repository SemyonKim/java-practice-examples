package puzzlers.effectivejava.ch9.item63;

/**
 * <h2>Beware the performance of string concatenation</h2>
 *
 * <p>
 * <b>Core Principle:</b> While the string concatenation operator (+) is convenient, it
 * does not scale. Because {@code String} is immutable, concatenating {@code n} strings using
 * the {@code +} operator in a loop requires {@code O(n^2)} time. For repeated concatenation,
 * always use {@code StringBuilder}.
 * </p>
 *
 * <h3>Advantages of StringBuilder</h3>
 * <ul>
 * <li><b>Linear Time Complexity:</b> Unlike the quadratic time of the {@code +} operator,
 * {@code StringBuilder.append} performs in {@code O(n)} time.</li>
 * <li><b>Reduced Object Creation:</b> It modifies an internal buffer instead of creating
 * a new {@code String} object for every single concatenation.</li>
 * <li><b>Preallocation:</b> If the final size is roughly known, you can pre-size the
 * {@code StringBuilder} to avoid internal array copies as the buffer grows.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Verbosity:</b> {@code StringBuilder} is more verbose than a simple {@code +}
 * for one-off concatenations.</li>
 * <li><b>Thread Safety:</b> {@code StringBuilder} is not synchronized. If thread safety
 * is required across multiple threads (rare for local string building), {@code StringBuffer}
 * is the legacy alternative.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see java.lang.StringBuilder
 */
public class StringConcatenation {

    private static final int LINE_WIDTH = 80;

    /**
     * INAPPROPRIATE: Performs abysmally for large N.
     * Each iteration creates a new String, copying all previous content.
     */
    public String slowStatement(int numItems) {
        String result = "";
        for (int i = 0; i < numItems; i++) {
            result += lineForItem(i); // Quadratic time!
        }
        return result;
    }

    /**
     * CORRECT: Performs in linear time.
     * Uses a mutable buffer to collect fragments.
     */
    public String fastStatement(int numItems) {
        // Pre-allocating the size for maximum efficiency
        StringBuilder b = new StringBuilder(numItems * LINE_WIDTH);
        for (int i = 0; i < numItems; i++) {
            b.append(lineForItem(i));
        }
        return b.toString();
    }

    private String lineForItem(int i) {
        return "Item " + i + ": Description of billing item details...\n";
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        StringConcatenation demo = new StringConcatenation();
        int count = 5000;

        // 1. Slow approach
        long start = System.currentTimeMillis();
        demo.slowStatement(count);
        System.out.println("String (+) time: " + (System.currentTimeMillis() - start) + "ms");

        // 2. Fast approach
        start = System.currentTimeMillis();
        demo.fastStatement(count);
        System.out.println("StringBuilder time: " + (System.currentTimeMillis() - start) + "ms");
    }
}