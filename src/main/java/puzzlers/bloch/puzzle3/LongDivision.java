package puzzlers.bloch.puzzle3;

/**
 * Long Division
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   Suppose we want to compute how many milliseconds are in a day,
 *   starting from the total number of microseconds in a day.
 *   A naive calculation might use integer division:
 * <p>
 *     long microsPerDay = 24 * 60 * 60 * 1_000_000;
 *     long millisPerDay = microsPerDay / 1000;
 * <p>
 * Expected behavior:
 *   Since 1 millisecond = 1000 microseconds, we expect an exact result:
 *   86,400,000 milliseconds in a day.
 * <p>
 * Actual behavior:
 *   - If we use `int` instead of `long`, the multiplication overflows.
 *     Example: `24 * 60 * 60 * 1_000_000` exceeds the range of `int`.
 *   - The result becomes negative or incorrect due to overflow.
 *   - With `long`, the calculation works correctly.
 * <p>
 * Lesson:
 *   - Integer division truncates, but here the bigger pitfall is overflow.
 *   - Always use `long` when dealing with large time unit conversions.
 * <p>
 * Key takeaway:
 *   Be mindful of numeric ranges. Use `long` for time calculations to avoid overflow.
 */
public class LongDivision {

    public static void main(String[] args) {
        // Using int (overflow!)
        int microsPerDayInt = 24 * 60 * 60 * 1_000_000;
        int millisPerDayInt = microsPerDayInt / 1000;
        System.out.println("int microsPerDay: " + microsPerDayInt);
        System.out.println("int millisPerDay: " + millisPerDayInt);

        // Using long (correct)
        long microsPerDayLong = 24L * 60 * 60 * 1_000_000;
        long millisPerDayLong = microsPerDayLong / 1000;
        System.out.println("long microsPerDay: " + microsPerDayLong);
        System.out.println("long millisPerDay: " + millisPerDayLong);
    }
}