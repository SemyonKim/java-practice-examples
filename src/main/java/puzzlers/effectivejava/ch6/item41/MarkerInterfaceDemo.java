package puzzlers.effectivejava.ch6.item41;

/**
 * <h2>Use marker interfaces to define types</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use a marker interface (an interface with no methods) if you want
 * to define a type that allows for compile-time type checking. Use a marker annotation
 * if you need to mark program elements other than classes/interfaces or if the marker
 * belongs in an annotation-heavy framework.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Compile-Time Type Safety:</b> Because marker interfaces define a type, you can use
 * them in method signatures to catch errors at compile time rather than runtime.</li>
 * <li><b>Precise Targeting:</b> Unlike annotations (which target all types via {@code ElementType.TYPE}),
 * a marker interface can extend another interface, restricting its application only to specific
 * sub-hierarchies.</li>
 * <li><b>Implicit Documentation:</b> Implementing an interface is a clear, structural statement
 * about the class's nature that is visible to all users of the API.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Restricted Scope:</b> Marker interfaces can only be applied to classes and interfaces;
 * they cannot mark methods, fields, or parameters.</li>
 * <li><b>Lack of Metadata:</b> Unlike annotations, marker interfaces cannot carry additional
 * data or parameters (e.g., {@code @ExceptionTest(ArithmeticException.class)}).</li>
 * <li><b>Framework Consistency:</b> In modern Java, many frameworks rely exclusively on
 * reflection-based annotation processing, making interfaces feel "out of place."</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch6.item39 MarkerAnnotations
 * @see puzzlers.effectivejava.ch4.item22 InterfaceOnlyToDefineTypes
 */
public class MarkerInterfaceDemo {

    /**
     * A marker interface representing a resource that has been encrypted.
     * Because it is a type, we can write methods that ONLY accept encrypted resources.
     */
    public interface Encrypted {
    }

    /**
     * A marker interface with precise targeting.
     * It extends 'Encrypted', ensuring only encrypted objects can be 'Authenticated'.
     */
    public interface AuthenticatedEncrypted extends Encrypted {
    }

    // --- Implementation Details ---

    public static class SecureDocument implements AuthenticatedEncrypted {
        private final String content = "Top Secret";
        @Override public String toString() { return content; }
    }

    public static class PlainTextDocument {
        private final String content = "Public Info";
        @Override public String toString() { return content; }
    }

    // --- Client Usage ---

    /**
     * This method takes advantage of the marker interface.
     * It is impossible to pass a non-encrypted object here; the compiler won't allow it.
     */
    public static void transmit(Encrypted resource) {
        System.out.println("Transmitting encrypted data: " + resource);
    }

    public static void main(String[] args) {
        SecureDocument secret = new SecureDocument();
        PlainTextDocument publicDoc = new PlainTextDocument();

        // This works! The compiler knows 'secret' is of type 'Encrypted'.
        transmit(secret);

        /*
         * UNCOMMENTING THE LINE BELOW WILL CAUSE A COMPILE-TIME ERROR.
         * This is the primary advantage over a marker annotation.
         * If 'Encrypted' were an annotation, this would only fail at runtime (if checked).
         */
        // transmit(publicDoc);

        System.out.println("Success: Compile-time safety achieved via Marker Interface.");
    }
}