package puzzlers.effectivejava.ch6.item36;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * <h2>Use EnumSet instead of bit fields</h2>
 *
 * <p>
 * <b>Core Principle:</b> Do not use {@code int} bit fields (powers of 2) to represent
 * sets of constants. Instead, use the {@code EnumSet} class, which combines the
 * performance of bitwise arithmetic with the safety and expressiveness of the
 * {@code Set} interface.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Type Safety:</b> Unlike {@code int} bit fields, {@code EnumSet} ensures
 * only valid constants of the correct type are included.</li>
 * <li><b>Performance:</b> Internally represented as a bit vector (a single {@code long}
 * if the enum has &le; 64 elements), making it as efficient as manual bit twiddling.</li>
 * <li><b>Expressiveness:</b> Provides a rich API for set operations (union, intersection,
 * iteration) without the ugliness of {@code |} and {@code &} operators.</li>
 * <li><b>Scalability:</b> Unlike bit fields, you aren't limited to 32 or 64 bits;
 * {@code EnumSet} scales automatically if the enum grows.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Mutability:</b> As of Java 9, there is no built-in immutable {@code EnumSet}.
 * You must wrap it in {@code Collections.unmodifiableSet}, which adds boilerplate
 * and a minor performance hit.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch6.item34 EnumsOverInts
 * @see puzzlers.effectivejava.ch9.item64 InterfaceTypes
 * @see java.util.EnumSet
 * @see java.util.Collections#unmodifiableSet(Set)
 */
public class Text {

    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

    /**
     * Accepts a Set of Styles. Using the interface type (Item 64) rather than
     * the implementation (EnumSet) allows for maximum flexibility.
     */
    public void applyStyles(Set<Style> styles) {
        Objects.requireNonNull(styles);
        System.out.printf("Applying styles: %s%n", styles);

        // Internal logic can still benefit from EnumSet performance
        if (styles.contains(Style.BOLD)) {
            // Apply bolding...
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Text text = new Text();

        // The modern replacement for: text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
        // Clear, type-safe, and efficient.
        text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));

        // Creating a set with all styles
        text.applyStyles(EnumSet.allOf(Style.class));

        // Creating an immutable version (Limitation workaround)
        Set<Style> immutableStyles = Collections.unmodifiableSet(
                EnumSet.of(Style.UNDERLINE, Style.STRIKETHROUGH)
        );
        text.applyStyles(immutableStyles);
    }
}