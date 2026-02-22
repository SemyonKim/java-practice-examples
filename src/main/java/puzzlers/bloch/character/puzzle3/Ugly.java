package puzzlers.bloch.character.puzzle3;

/**
 * Obfuscated Unicode Literal
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 17 (Variation)
 * <p>
 * Problem:
 * Is it possible to write a functional Java class where the source code 
 * contains zero ASCII characters for the logic?
 * <p>
 * Example:
 * \u0070\u0061\u0063\u006b\u0061\u0067\u0065\u0020\u0070\u0075\u007a\u007a\u006c\u0065\u0072\u0073\u002e\u0062
 * \u006c\u006f\u0063\u0068\u002e\u0063\u0068\u0061\u0072\u0061\u0063\u0074\u0065\u0072\u002e\u0070\u0075\u007a
 * \u007a\u006c\u0065\u0033\u003b\u000a\u000a\u0070\u0075\u0062\u006c\u0069\u0063\u0020\u0063\u006c\u0061\u0073
 * \u0073\u0020\u0055\u0067\u006c\u0079\u0020\u007b\u000a\u0020\u0020\u0020\u0020\u0070\u0075\u0062\u006c\u0069
 * \u0063\u0020\u0073\u0074\u0061\u0074\u0069\u0063\u0020\u0076\u006f\u0069\u0064\u0020\u006d\u0061\u0069\u006e
 * \u0028\u0053\u0074\u0072\u0069\u006e\u0067\u005b\u005d\u0020\u0061\u0072\u0067\u0073\u002base\u0029\u0020
 * \u007b\u000a\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0053\u0079\u0073\u0074\u0065\u006d\u002e\u006f
 * \u0075\u0074\u002e\u0070\u0072\u0069\u006e\u0074\u006c\u006e\u0028\u0022\u0048\u0065\u006c\u006c\u006f\u0020
 * \u0077\u0022\u0020\u002b\u0020\u0022\u006f\u0072\u006c\u0064\u0022\u0029\u003b\u000a\u0020\u0020\u0020\u0020
 * \u007d\u000a\u007d
 * <p>
 * Explanation:
 * - Java source files are processed in a "top-down" manner starting with 
 * the translation of Unicode escapes (\ uXXXX).
 * - This happens before the compiler identifies keywords (package, class, public), 
 * identifiers, or even string literals.
 * - In this example, every single character of the "Ugly" class has 
 * been replaced by its hexadecimal Unicode equivalent.
 * <p>
 * Step-by-step:
 * 1. The compiler reads the file and finds sequences like \ u0070\ u0061...
 * 2. It maps \ u0070 to 'p', \ u0061 to 'a', etc.
 * 3. After the first pass, the code is transformed into:
 * package puzzlers.bloch.character.puzzle3;
 * public class Ugly {
 *      public static void main(String[] args) {
 *          System.out.println("Hello w" + "orld");
 *      }
 * }
 * 4. The compiler then proceeds with standard Lexical Analysis.
 * <p>
 * Lesson:
 * - The "Early Translation" rule means that the Java language is 
 * entirely independent of the encoding of the source file, provided 
 * the characters are represented as Unicode escapes.
 * - This can be used for extreme obfuscation, but it is a major 
 * security risk (Trojan Source attacks) because the code being 
 * executed is not what it appears to be.
 * <p>
 * Output demonstration:
 * Hello world
 */
public class Ugly {
    public static void main(String[] args) {
        System.out.println("Hello w" + "orld");
    }
}