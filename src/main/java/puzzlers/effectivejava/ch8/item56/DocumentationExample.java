package puzzlers.effectivejava.ch8.item56;

import java.util.Objects;

/**
 * <h2>Write doc comments for all exposed API elements</h2>
 *
 * <p>
 * <b>Core Principle:</b> Every exported class, interface, constructor, method, and field
 * declaration must be preceded by a doc comment. The documentation should describe the
 * contract between the API and the client, covering preconditions, postconditions, and side effects.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Professionalism:</b> Provides a de facto standard for API communication that users expect.</li>
 * <li><b>Maintainability:</b> Keeps documentation in sync with source code, reducing the "documentation rot" common in manual docs.</li>
 * <li><b>Searchability:</b> Modern Javadoc (Java 9+) includes a client-side search index (enabled by {@code @index}).</li>
 * <li><b>Subclass Guidance:</b> Uses {@code @implSpec} to clearly define the implementation requirements for classes designed for inheritance.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Maintenance Burden:</b> Requires discipline to update comments whenever the code logic changes.</li>
 * <li><b>HTML Verbosity:</b> Since Javadoc is translated to HTML, complex formatting requires escaping metacharacters ({@code <}, {@code >}, {@code &}) using {@code {@literal}} or {@code {@code}}.</li>
 * <li><b>Architectural Gaps:</b> Doc comments describe individual elements but often fail to describe the high-level architecture of complex APIs, requiring supplemental external documents.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 Accessibility
 * @see puzzlers.effectivejava.ch4.item19 DesignForInheritance
 * @see puzzlers.effectivejava.ch10.item74 DocumentExceptions
 * @see puzzlers.effectivejava.ch11.item82 ThreadSafetyDocumentation
 * @see puzzlers.effectivejava.ch12.item87 SerializedForm
 */
public class DocumentationExample<E> {

    /**
     * An instantaneous point on the time-line (Noun phrase for fields/classes).
     */
    private E element;

    /**
     * Constructs a new documentation example with the specified element.
     * @param element the element to be stored in this container
     */
    public DocumentationExample(E element) {
        this.element = Objects.requireNonNull(element);
    }

    /**
     * Returns the element stored in this container (Verb phrase for methods).
     *
     * <p>This method complies with the {@index IEEE 754} standard for
     * no particular reason other than demonstrating the index tag.
     *
     * @return the element stored in this container
     * @throws IllegalStateException if the element has been cleared
     */
    public E getElement() {
        if (element == null) throw new IllegalStateException();
        return element;
    }

    /**
     * Returns true if the provided value is {@literal >} the threshold.
     *
     *  <p>Note the use of {@code {@literal}} to handle the greater-than sign
     * without confusing the HTML parser.</p>
     *
     * @param value the value to compare
     * @param threshold the threshold to compare against
     * @return {@code true} if value is greater than threshold
     */
    public boolean isGreaterThan(int value, int threshold) {
        return value > threshold;
    }

    /**
     * Clears the current element.
     *
     * @implSpec
     * This implementation sets the {@code element} field to {@code null}.
     *
     * @see #getElement()
     */
    public void clear() {
        this.element = null;
    }

    // --- Client Usage ---

    /**
     * Demonstrates how the documented API is invoked.
     *
     * @param args command line arguments (ignored)
     */
    public static void main(String[] args) {
        DocumentationExample<String> doc = new DocumentationExample<>("Effective Java");

        // The IDE (e.g., IntelliJ or Eclipse) will display the Javadoc
        // when hovering over these methods.
        String val = doc.getElement();
        System.out.println("Retrieved: " + val);

        if (doc.isGreaterThan(10, 5)) {
            doc.clear();
        }
    }
}

/**
 * Indicates a college degree, such as B.S., {@literal M.S.} or Ph.D.
 * <p>
 * Use {@code {@literal}} to prevent the period after "M.S." from
 * prematurely terminating the summary description.
 */
class Degree {
    // Implementation omitted
}