package puzzlers.effectivejava.ch2.item5;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <h2>Prefer dependency injection to hardwiring resources</h2>
 *
 * <p>
 * <b>Core Principle:</b> Do not use singletons or static utility classes for classes
 * whose behavior is parameterized by underlying resources. Instead, pass the resources
 * (or factories to create them) into the constructor, static factory, or builder.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Flexibility:</b> Allows a single class to work with multiple variations of a resource (e.g., different languages or specialized vocabularies).</li>
 * <li><b>Testability:</b> Enables the use of mock or specialized resources during testing.</li>
 * <li><b>Reusability:</b> The class is no longer hardwired to a specific implementation, making it useful in more contexts.</li>
 * <li><b>Thread-Safety:</b> Supports immutability by allowing resources to be {@code final}, facilitating safe sharing among multiple clients.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Project Clutter:</b> In very large projects with thousands of dependencies, manual injection can become verbose and difficult to manage.
 * <i>Note: This is typically mitigated by using DI frameworks like Dagger, Guice, or Spring.</i></li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch2.item2 Builders
 * @see puzzlers.effectivejava.ch2.item3 Singletons
 * @see puzzlers.effectivejava.ch2.item4 StaticUtilities
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch5.item31 Wildcards
 */
public class SpellChecker {

    private final Lexicon dictionary;

    /**
     * Dependency injection: The resource is passed at creation time.
     * This ensures the class is flexible and testable.
     */
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {
        return dictionary.contains(word);
    }

    public List<String> suggestions(String typo) {
        return dictionary.findSuggestions(typo);
    }

    /**
     * Factory-based variant: Uses a Supplier to produce resources.
     * Uses bounded wildcards (Item 31) to allow factories of any subtype.
     */
    public static SpellChecker create(Supplier<? extends Lexicon> dictionaryFactory) {
        return new SpellChecker(dictionaryFactory.get());
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Injecting a specific dictionary (e.g., English)
        Lexicon englishLexicon = new EnglishLexicon();
        SpellChecker englishScanner = new SpellChecker(englishLexicon);

        // Injecting a mock for testing
        Lexicon mockLexicon = new Lexicon() {
            @Override public boolean contains(String word) { return true; }
            @Override public List<String> findSuggestions(String typo) { return List.of(); }
        };
        SpellChecker testScanner = new SpellChecker(mockLexicon);

        System.out.println("English Lexicon used: " + englishScanner.isValid("Hello"));
    }
}

/**
 * Mock interface representing the underlying resource.
 */
interface Lexicon {
    boolean contains(String word);
    List<String> findSuggestions(String typo);
}

class EnglishLexicon implements Lexicon {
    @Override public boolean contains(String word) { return true; }
    @Override public List<String> findSuggestions(String typo) { return List.of(); }
}