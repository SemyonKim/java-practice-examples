package puzzlers.effectivejava.ch3.item12;

/**
 * <h2>Always override toString</h2>
 *
 * <p>
 * <b>Core Principle:</b> Every instantiable class should override {@code toString}
 * to return a concise, informative representation that is easy for a person to read.
 * While not as critical as {@code equals} or {@code hashCode}, a good {@code toString}
 * makes your class much more pleasant to use and simplifies debugging.
 * </p>
 *
 * <h3>General Advantages</h3>
 * <ul>
 * <li><b>Diagnostic Clarity:</b> The method is automatically invoked by debuggers,
 * {@code println}, {@code printf}, and the string concatenation operator,
 * making logged error messages actually useful.</li>
 * <li><b>Collection Readability:</b> Provides meaningful output when objects are
 * stored in collections; e.g., seeing {@code {Jenny=707-867-5309}} instead of
 * {@code {Jenny=PhoneNumber@163b91}}.</li>
 * <li><b>Ease of Use:</b> Reduces the effort required for programmers to understand
 * the state of an object at runtime without manual inspection.</li>
 * </ul>
 *
 * <h3>Value Class Advantages (Specified Format)</h3>
 * <ul>
 * <li><b>Unambiguous Standard:</b> Provides a human-readable representation that
 * can be used for persistent data objects like CSV files.</li>
 * <li><b>Bidirectional Translation:</b> When paired with a static factory or
 * constructor, it allows easy translation between the object and its string form.</li>
 * </ul>
 *
 * <h3>Limitations / Disadvantages</h3>
 * <ul>
 * <li><b>API Lock-in:</b> If you specify the format in your documentation, you
 * are committed to it for life. Changing it in the future will break client
 * code that parses the string.</li>
 * <li><b>Impracticality for Large Objects:</b> If an object contains massive state,
 * a full representation is impractical; a summary (e.g., "Directory (1487536 listings)")
 * is preferred.</li>
 * <li><b>Fragility:</b> If you do not provide programmatic access (getters),
 * programmers will be forced to parse the string, leading to fragile systems.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item10 Equals
 * @see puzzlers.effectivejava.ch3.item11 HashCode
 * @see puzzlers.effectivejava.ch2.item4 StaticUtilityClass
 * @see puzzlers.effectivejava.ch6.item34 Enums
 */
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = (short) areaCode;
        this.prefix   = (short) prefix;
        this.lineNum  = (short) lineNum;
    }

    // Programmatic access to the information ensures users don't have to parse the string
    public short getAreaCode() { return areaCode; }
    public short getPrefix()   { return prefix; }
    public short getLineNum()  { return lineNum; }

    /**
     * Returns the string representation of this phone number.
     * The string consists of twelve characters whose format is
     * "XXX-YYY-ZZZZ", where XXX is the area code, YYY is the
     * prefix, and ZZZZ is the line number. Each of the capital
     * letters represents a single decimal digit.
     * <p>
     * If any of the three parts of this phone number is too small
     * to fill up its field, the field is padded with leading zeros.
     * For example, if the value of the line number is 123, the last
     * four characters of the string representation will be "0123".
     */
    @Override
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        PhoneNumber jenny = new PhoneNumber(707, 867, 5309);

        // Usage in concatenation
        System.out.println("Failed to connect to " + jenny);

        // Usage in a collection
        java.util.Map<String, PhoneNumber> directory = new java.util.HashMap<>();
        directory.put("Jenny", jenny);
        System.out.println("Directory: " + directory);
    }
}

/**
 * Example of a class that does NOT specify a format.
 */
class Potion {
    private final String type;
    private final String smell;

    public Potion(String type, String smell) {
        this.type = type;
        this.smell = smell;
    }

    /**
     * Returns a brief description of this potion. The exact details
     * of the representation are unspecified and subject to change,
     * but the following may be regarded as typical: "[Potion: type=love, smell=turpentine]"
     */
    @Override
    public String toString() {
        return String.format("[Potion: type=%s, smell=%s]", type, smell);
    }
}