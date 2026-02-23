package puzzlers.bloch.classy.puzzle2;

import java.util.Calendar;

/**
 * The Static Initialization Paradox
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 49
 * <p>
 * Problem:
 * What happens when a static field is initialized by a constructor
 * that depends on another static field defined later in the same class?
 * <p>
 * Explanation:
 * - Java initializes static fields in the order they appear in the source code.
 * - When {@code Elvis} is loaded, the JVM first attempts to initialize {@code INSTANCE}.
 * - This triggers the {@code Elvis()} constructor.
 * - Inside the constructor, the code calculates {@code CURRENT_YEAR - 1930}.
 * - **The Catch:** At this exact moment, {@code CURRENT_YEAR} has not yet
 * been initialized by the JVM. It currently holds its default value for integers: {@code 0}.
 * - Therefore, {@code beltSize} is calculated as {@code 0 - 1930 = -1930}.
 * - Only *after* the {@code INSTANCE} and constructor are finished does
 * the JVM move on to initialize {@code CURRENT_YEAR} to 2026.
 * <p>
 * The "Unexpected" Result:
 * - You might expect the output to be the King's current age (e.g., 96 in 2026).
 * - Instead, it prints: **-1930**.
 * <p>
 * Step-by-step:
 * 1. JVM starts initializing class {@code Elvis}.
 * 2. {@code INSTANCE} field is processed; it calls {@code new Elvis()}.
 * 3. Constructor {@code Elvis()} runs.
 * 4. It reads {@code CURRENT_YEAR}. Since the line defining it hasn't
 * been reached, it uses the default value of {@code 0}.
 * 5. {@code beltSize} is set to $-1930$.
 * 6. Constructor finishes, {@code INSTANCE} is assigned.
 * 7. JVM finally processes {@code CURRENT_YEAR} and sets it to 2026.
 * <p>
 * Lesson:
 * - Order of declaration matters immensely for static fields.
 * - Be extremely careful with "circular" dependencies where a constructor
 * of a class relies on static state of that same class.
 * - To fix this, move the {@code CURRENT_YEAR} declaration above the
 * {@code INSTANCE} declaration.
 * <p>
 * Output:
 * // -1930
 */
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private final int beltSize;
    private static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private Elvis() {
        beltSize = CURRENT_YEAR - 1930;
    }
    public int beltSize() {
        return beltSize;
    }
    public static void main(String[] args) {
        System.out.println(INSTANCE.beltSize());
    }
}