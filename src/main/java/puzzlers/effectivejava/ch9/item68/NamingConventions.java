package puzzlers.effectivejava.ch9.item68;

import java.util.Collections;
import java.util.List;

/**
 * <h2>Adhere to generally accepted naming conventions</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use the well-established naming conventions defined in the
 * Java Language Specification to ensure code is predictable, maintainable, and
 * professional. Conventions fall into typographical and grammatical categories.
 * </p>
 *
 * <h3>Typographical Conventions</h3>
 * <ul>
 * <li><b>Packages:</b> Lowercase, hierarchical, reversed domain (e.g., com.google.common).</li>
 * <li><b>Classes/Interfaces:</b> CamelCase (e.g., {@code FutureTask}). Prefer {@code HttpUrl}
 * over {@code HTTPURL} for readability.</li>
 * <li><b>Methods/Fields:</b> lowerCamelCase (e.g., {@code ensureCapacity}).</li>
 * <li><b>Constants:</b> UPPER_SNAKE_CASE (e.g., {@code MIN_VALUE}). Used for static final
 * fields of immutable types.</li>
 * <li><b>Type Parameters:</b> Single letters: T (Type), E (Element), K/V (Key/Value),
 * X (Exception), R (Return).</li>
 * </ul>
 *
 * <h3>Grammatical Conventions</h3>
 * <ul>
 * <li><b>Classes:</b> Singular nouns or noun phrases (e.g., {@code Thread}, {@code ChessPiece}).
 * Utility classes are often plural (e.g., {@code Collections}).</li>
 * <li><b>Interfaces:</b> Similar to classes or adjectives ending in "-able" or "-ible"
 * (e.g., {@code Runnable}).</li>
 * <li><b>Methods:</b> Verbs/verb phrases for actions (e.g., {@code drawImage}).
 * Accessors often use {@code get} or just the noun (e.g., {@code getTime} or {@code size}).
 * Booleans use {@code is} or {@code has}.</li>
 * </ul>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Clarity:</b> Reduces the cognitive load for new developers reading the code.</li>
 * <li><b>Interoperability:</b> Standard conventions ensure compatibility with tools
 * like build systems, IDEs, and the JavaBeans specification.</li>
 * <li><b>Reliability:</b> Prevents errors caused by faulty assumptions about what an
 * identifier represents (e.g., mistaking a field for a constant).</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Legacy Conflicts:</b> Existing libraries or specific framework requirements
 * (like JavaBeans) may force specific naming styles that diverge from modern preferences.</li>
 * <li><b>Subjectivity:</b> Grammatical choices (e.g., {@code speed()} vs {@code getSpeed()})
 * can still lead to debate.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch2.item4 Noninstantiability
 * @see puzzlers.effectivejava.ch4.item15 InformationHiding
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 */
public class NamingConventions<T> {

    // Constant Field: static, final, and immutable
    public static final int MAX_RETRY_COUNT = 10;

    // Instance Field: lowerCamelCase
    private boolean initialized = false;

    /**
     * Action method: named with a verb.
     */
    public void performAction() {
        initialized = true;
    }

    /**
     * Boolean accessor: starts with 'is'.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Static factory method: using 'of' or 'from'.
     */
    public static <T> List<T> of(T element) {
        return Collections.singletonList(element);
    }

    /**
     * Conversion method: 'toType'.
     */
    @Override
    public String toString() {
        return "NamingConventions{initialized=" + initialized + "}";
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Class/Interface: Noun phrase, CamelCase
        NamingConventions<String> demo = new NamingConventions<>();

        // Method: Verb phrase, lowerCamelCase
        demo.performAction();

        // Constant: UPPER_SNAKE_CASE
        if (NamingConventions.MAX_RETRY_COUNT > 0) {
            System.out.println("Status: " + demo.isInitialized());
        }

        // Static factory: common naming
        List<String> list = NamingConventions.of("Effective Java");
        System.out.println("Factory list: " + list);
    }
}