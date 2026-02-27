package puzzlers.valeev.strings.mistake1;

import java.text.BreakIterator;

/**
 * Assuming that char value is a character
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 45
 * <p>
 * Problem:
 * Why is it dangerous to assume that a single Java 'char' represents a complete
 * displayed character, especially when performing string manipulations like substring?
 * <p>
 * Expected behavior:
 * Developers expect String.length() to return the count of visible characters and
 * assume that String.charAt() or String.substring() will always yield valid,
 * complete characters.
 * <p>
 * Actual behavior:
 * Characters with Unicode code points above 0xFFFF (outside the Basic Multilingual Plane)
 * are represented as surrogate pairs—two 'char' values for one character. Naive
 * splitting or processing can break these pairs, resulting in corrupted output
 * (like '?' or '') or the loss of modifiers (like skin tones).
 * <p>
 * Explanation:
 * - BMP vs. Supplementary Planes: Most common characters fit in 16 bits (BMP).
 * However, many emojis and complex scripts exist in supplementary planes and
 * require 32 bits, encoded in Java as a "high surrogate" and a "low surrogate."
 * - Character Class Overloads: Methods like Character.isDigit(char) only check 16-bit
 * values. They fail for non-BMP digits unless the int-based (codepoint) overload is used.
 * - Combining Sequences: Some visual symbols consist of multiple codepoints
 * (e.g., a base character followed by a skin tone modifier). Even if you don't
 * break a surrogate pair, you might accidentally separate the modifier.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: Splitting Emojis
 * - The emoji \uD83D\uDE01 (U+1F601) is two chars long.
 * - If rowLength is 10 and the emoji starts at index 9, a substring(0, 10)
 * captures only \uD83D, creating an invalid sequence.
 * - Case: Skin Tone Modifiers
 * - A waving hand with dark skin tone is four chars long (two surrogate pairs).
 * - Splitting between the two pairs preserves the hand but removes the skin tone.
 * - Case: startsWithDigit Validation
 * - A non-BMP digit like U+1D7D8 (Mathematical Double-Struck Digit 0) is
 * represented by a surrogate pair.
 * - Calling isDigit(str.charAt(0)) only checks the high surrogate, returning false.
 * <p>
 * Fixes:
 * - Use codepoint-aware APIs: string.codePoints(), string.codePointAt(index),
 * and StringBuilder.appendCodePoint(int).
 * - Use int-based overloads for Character class methods (e.g., isLetter(int)).
 * - For UI-level text wrapping, use BreakIterator.getCharacterInstance() to
 * respect grapheme boundaries (including modifiers and ligatures).
 * - Prefer library solutions like ICU4J for complex text processing.
 * <p>
 * Lesson:
 * - A Java 'char' is a UTF-16 code unit, not necessarily a full Unicode character.
 * - Always use codepoints or grapheme iterators when your application handles
 * arbitrary user input or international scripts.
 * - Avoid char-centric public APIs; they are "time bombs" for modern Unicode support.
 * <p>
 * Output:
 * - startsWithDigit("\uD835\uDFD8") [char version] -> false
 * - startsWithDigit("\uD835\uDFD8") [int version] -> true
 * - substring on "Welcome! \uD83D\uDE01" at index 10 -> "Welcome! ?"
 * - BreakIterator/codepoints() on "Welcome! \uD83D\uDE01" at index 10 -> "Welcome! 😁"
 */
public class CharValueMistake {

    // Naive implementation that fails for non-BMP digits
    public static boolean startsWithDigitBroken(String str) {
        return !str.isEmpty() && Character.isDigit(str.charAt(0));
    }

    // Correct implementation using codepoints
    public static boolean startsWithDigitCorrect(String str) {
        return !str.isEmpty() && Character.isDigit(str.codePointAt(0));
    }

    // Extracting parts using the Stream API to respect codepoints
    public static String getPartByCodePoints(String string, int rowNumber, int rowLength) {
        return string.codePoints()
                .skip((long) rowNumber * rowLength)
                .limit(rowLength)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    // Using BreakIterator to respect modifiers and grapheme clusters
    public static String getPartByGraphemes(String string, int rowNumber, int rowLength) {
        BreakIterator iterator = BreakIterator.getCharacterInstance();
        iterator.setText(string);
        int start = iterator.first();

        // Move to the beginning of the desired chunk
        for (int i = 0; i < rowNumber * rowLength && start != BreakIterator.DONE; i++) {
            start = iterator.next();
        }

        // Find the end of the chunk
        int end = start;
        for (int i = 0; i < rowLength && end != BreakIterator.DONE; i++) {
            end = iterator.next();
        }

        if (start == BreakIterator.DONE || end == BreakIterator.DONE) {
            return "";
        }
        return string.substring(start, end);
    }

    public static void main(String[] args) {
        // A non-BMP digit (Mathematical Double-Struck Digit Zero: 𝟘)
        String nonBmpDigit = "\uD835\uDFD8";

        System.out.println("String: " + nonBmpDigit);
        System.out.println("String length (chars): " + nonBmpDigit.length());
        System.out.println("Is digit (char-based)? " + startsWithDigitBroken(nonBmpDigit));
        System.out.println("Is digit (int-based)? " + startsWithDigitCorrect(nonBmpDigit));

        // Emoji splitting example
        String greeting = "Welcome! \uD83D\uDE01"; // "Welcome! 😁"
        System.out.println("\nOriginal: " + greeting);

        // Naive split that breaks the surrogate pair
        String brokenSplit = greeting.substring(0, 10);
        System.out.println("Broken split (index 10): [" + brokenSplit + "]");

        // Correct split using Grapheme BreakIterator and CodePoints
        String safeSplit = getPartByGraphemes(greeting, 0, 10);
        System.out.println("Safe split (by graphemes): [" + safeSplit + "]");
        safeSplit = getPartByCodePoints(greeting, 0, 10);
        System.out.println("Safe split (by codepoints): [" + safeSplit + "]");
    }
}