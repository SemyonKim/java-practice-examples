package puzzlers.bloch.character.puzzle5;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex Replacement Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzles 20, 21 & Extension
 * <p>
 * Problem:
 * Why do `String.replaceAll()` and `String.replaceFirst()` crash or produce
 * corrupted results when using system paths, URLs, or file separators?
 * <p>
 * Explanation:
 * - `String.replaceAll(regex, replacement)` treats BOTH arguments as special:
 * 1. The first is a Regular Expression (where `.` or `*` are meta-characters).
 * 2. The second is a Replacement String (where `\` and `$` are meta-characters).
 * - A major cross-platform risk is `File.separator`. On Windows, this is a
 * backslash (`\`). When used as a replacement, the regex engine expects
 * a character to follow it for escaping. If it's at the end of the string,
 * it throws a `StringIndexOutOfBoundsException`.
 * <p>
 * Step-by-step:
 * 1. User provides a Windows path: `C:\temp\`.
 * 2. `replaceAll` sees the trailing `\` in the replacement string.
 * 3. The engine looks for a character to "escape" but finds the end of string.
 * 4. Crash occurs because the syntax is incomplete.
 * <p>
 * Lesson:
 * - Never assume a String is "just text" when passing it to `replaceAll`.
 * - Code that works on Linux (where separator is `/`) will fail on Windows
 * (where separator is `\`) unless handled correctly.
 * <p>
 * Safer alternatives:
 * 1. Use `String.replace(target, replacement)` for literal, non-regex work.
 * 2. Use `Pattern.quote(regex)` and `Matcher.quoteReplacement(sub)` for
 * dynamic regex work.
 * <p>
 * Output demonstration:
 * // On Windows:
 * // "path".replaceAll("path", File.separator); -> CRASH
 * // "path".replace("path", File.separator);    -> SUCCESS ("\")
 */
public class RegexTrap {
    public static void main(String[] args) {
        String input = "replace.me";
        String regex = ".";
        String replacement = File.separator + "data" + File.separator; // e.g., "\data\"

        // 1. DANGEROUS: regex "." matches everything; replacement "\" crashes on Windows
        try {
            System.out.println("Unsafe: " + input.replaceAll(regex, replacement));
        } catch (Exception e) {
            System.out.println("Unsafe call failed as expected on Windows.");
        }

        // 2. SAFE (The Manual Way): Quote both parts
        String safeRegex = Pattern.quote(regex);
        String safeReplacement = Matcher.quoteReplacement(replacement);
        System.out.println("Safe Regex: " + input.replaceAll(safeRegex, safeReplacement));

        // 3. BEST (The Simple Way): Use .replace() for literals
        System.out.println("Best Practice: " + input.replace(regex, replacement));
    }
}