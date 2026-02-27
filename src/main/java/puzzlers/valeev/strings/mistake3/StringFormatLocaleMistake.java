package puzzlers.valeev.strings.mistake3;

import java.util.Locale;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Using String.format with the Default Locale
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 47
 * <p>
 * Problem:
 * Why does using String.format() or the formatted() method often lead to
 * NumberFormatExceptions or silent data corruption when parsing formatted numbers?
 * <p>
 * Expected behavior:
 * Developers expect that a number formatted by the system should be readable by
 * the system's own parsing methods (like Double.parseDouble()).
 * <p>
 * Actual behavior:
 * String.format() is locale-sensitive. In locales like German or French, the
 * decimal separator is a comma (,) instead of a period (.). However,
 * Double.parseDouble() is locale-independent and always expects a period,
 * leading to crashes or misinterpretation.
 * <p>
 * Explanation:
 * - Locale-Sensitive Formatting: String.format("%.2f", value) uses Locale.getDefault().
 * - Decimal Separators: US/UK use ".", while much of Europe uses ",".
 * - Grouping Separators: US uses "," for thousands, while German uses ".".
 * - Parsing Disconnect: Double.parseDouble() and Long.parseLong() are hardcoded
 * to follow the "English-like" (Computer) format. They do not understand commas.
 * - Silent Corruption: If a German locale formats 12345 as "12.345" (using a
 * period as a grouping separator), parseDouble("12.345") will successfully
 * return 12.345—incorrectly treating the thousands separator as a decimal point.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: The Swing Input Trap
 * - 1. System in German locale formats Math.PI to "3,14".
 * - 2. UI displays "3,14" in an input box.
 * - 3. User clicks OK; the string "3,14" is passed to Double.parseDouble().
 * - 4. Double.parseDouble("3,14") throws a NumberFormatException.
 * - Case: The Grouping Error
 * - 1. NumberFormat.getInstance(Locale.GERMAN).format(12345) yields "12.345".
 * - 2. Double.parseDouble("12.345") interprets this as 12.345 (twelve and a bit).
 * - 3. The value is now 1000x smaller than intended.
 * <p>
 * Fixes:
 * - Technical Data: Always use String.format(Locale.ROOT, ...) for machine-readable
 * strings (JSON, logs, database queries).
 * - User-Facing Data: Use NumberFormat for both formatting and parsing to ensure
 * the same locale logic is applied to both ends.
 * - Explicit Intent: If using String.format() for the UI, explicitly pass
 * Locale.getDefault() to make the dependency obvious.
 * <p>
 * Lesson:
 * - String.format() is for human presentation; parseDouble() is for data interchange.
 * - Never mix the two without explicitly defining the Locale.
 * - Default locales are "hidden global variables" that change behavior based on
 * the environment.
 * <p>
 * Output:
 * - format(1.23) [US] -> 1.23
 * - format(1.23) [Germany] -> 1,23
 * - parseDouble("1,23") -> NumberFormatException
 */
public class StringFormatLocaleMistake {

    public static void main(String[] args) {
        double value = 12345.67;

        // Demonstration of the Locale Trap
        System.out.println("--- Locale-Dependent Formatting ---");
        System.out.println("US Locale: " + String.format(Locale.US, "%.2f", value));
        System.out.println("German Locale: " + String.format(Locale.GERMAN, "%.2f", value));

        // The "Time Bomb"
        String formattedInGerman = String.format(Locale.GERMAN, "%.2f", value); // "12345,67"
        try {
            System.out.println("\nAttempting to parse '" + formattedInGerman + "' with Double.parseDouble()...");
            Double.parseDouble(formattedInGerman);
        } catch (NumberFormatException e) {
            System.err.println("FAILED: Double.parseDouble() crashed because of the comma.");
        }

        // The Grouping Separator Danger
        System.out.println("\n--- Silent Corruption Example ---");
        String groupedGerman = NumberFormat.getInstance(Locale.GERMAN).format(12345); // "12.345"
        double corruptedValue = Double.parseDouble(groupedGerman); // interprets as 12.345
        System.out.println("Original: 12345");
        System.out.println("Parsed from German format '12.345': " + corruptedValue);

        // The Correct Approach (Locale.ROOT for technical strings)
        System.out.println("\n--- The Fix (Locale.ROOT) ---");
        String safeTechnicalString = String.format(Locale.ROOT, "%.2f", value);
        System.out.println("Safe for parsing: " + safeTechnicalString);
        System.out.println("Parsed successfully: " + Double.parseDouble(safeTechnicalString));

        // The Correct Approach (NumberFormat for localized UI)
        System.out.println("\n--- The Fix (Consistent NumberFormat) ---");
        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
            String uiString = nf.format(value);
            double parsedBack = nf.parse(uiString).doubleValue();
            System.out.println("Formatted for German UI: " + uiString);
            System.out.println("Parsed back safely using same Locale: " + parsedBack);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}