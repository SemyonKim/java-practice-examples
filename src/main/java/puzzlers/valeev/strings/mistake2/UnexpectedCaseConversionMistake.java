package puzzlers.valeev.strings.mistake2;

import java.util.Locale;

/**
 * Unexpected Case Conversions
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 46
 * <p>
 * Problem:
 * Why do standard string case conversion methods sometimes break application logic,
 * especially when handling internal identifiers or running in specific locales?
 * <p>
 * Expected behavior:
 * Developers expect {@code toUpperCase()} and {@code toLowerCase()} to behave
 * consistently regardless of the user's geographical location or OS settings.
 * <p>
 * Actual behavior:
 * No-argument case conversion methods use the JVM's default locale. In certain
 * locales, such as Turkish or Azerbaijani, the mapping of 'i' and 'I' is non-standard.
 * Additionally, some conversions change the total length of the string.
 * <p>
 * Explanation:
 * - Locale Dependency: {@code String.toUpperCase()} implicitly calls {@code Locale.getDefault()}.
 * - The Turkish 'I' Problem: Turkish has dotted (İ, i) and dotless (I, ı) letters.
 * - In Turkish: "i".toUpperCase() becomes "İ" (Dotted I).
 * - In Turkish: "I".toLowerCase() becomes "ı" (Dotless i).
 * - This breaks checks like {@code "point".toUpperCase().equals("POINT")} because
 * the result is "POİNT".
 * - String Length Variance: In German, the letter 'ß' (Eszett) converts to "SS"
 * in uppercase. This increases the string length by 1, which can break code
 * relying on fixed-length buffers or index assumptions.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: Internal Identifier Comparison
 * - 1. Code defines {@code String type = "point"}.
 * - 2. JVM is started with {@code -Duser.language=tr}.
 * - 3. {@code type.toUpperCase()} results in "POİNT" (7 characters if counting Unicode).
 * - 4. {@code "POİNT".equals("POINT")} returns {@code false}.
 * - Case: German 'ß'
 * - 1. {@code "Straße".toUpperCase(Locale.GERMAN)} is called.
 * - 2. The single character 'ß' is replaced by "SS".
 * - 3. The string length changes from 6 to 7.
 * <p>
 * Fixes:
 * - For technical/neutral identifiers (XML, YAML, SQL): Always use {@code Locale.ROOT}.
 * - For user-facing text: If the default locale is intended, pass it explicitly
 * via {@code Locale.getDefault()} to signal intentionality to other developers.
 * - Testing: Include Turkish (tr) in your CI/CD test matrix to catch locale issues.
 * <p>
 * Lesson:
 * - Case conversion is a linguistic operation, not a simple bit-flip.
 * - Never use no-arg case conversion for protocol-level or internal strings.
 * - Always assume that string length may change after a case conversion.
 * <p>
 * Output:
 * - "point".toUpperCase() [Turkish Locale] -> POİNT
 * - "Straße".length() -> 6
 * - "Straße".toUpperCase().length() -> 7
 */
public class UnexpectedCaseConversionMistake {

    public static void main(String[] args) {
        String type = "point";
        String street = "Straße";

        // Demonstration of the Turkish Locale issue
        // We set it manually here for the demonstration, but it usually comes from the OS
        Locale turkish = new Locale("tr");
        // 'Locale(java.lang.String)' is deprecated since version 19

        System.out.println("--- Turkish Locale Results ---");
        String upperTurkish = type.toUpperCase(turkish);
        System.out.println("Upper ('point', Turkish): " + upperTurkish);
        System.out.println("Is 'point' equal to 'POINT' in Turkish? " + upperTurkish.equals("POINT"));

        // Demonstration of the Neutral (ROOT) Locale fix
        System.out.println("\n--- Root Locale Results ---");
        String upperRoot = type.toUpperCase(Locale.ROOT);
        System.out.println("Upper ('point', ROOT): " + upperRoot);
        System.out.println("Is 'point' equal to 'POINT' in ROOT? " + upperRoot.equals("POINT"));

        // Demonstration of Length Change
        System.out.println("\n--- Length Change (German) ---");
        System.out.println("Original: " + street + " (Length: " + street.length() + ")");
        String upperStreet = street.toUpperCase(Locale.GERMAN);
        System.out.println("Uppercase: " + upperStreet + " (Length: " + upperStreet.length() + ")");
    }
}