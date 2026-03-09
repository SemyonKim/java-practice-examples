package puzzlers.effectivejava.ch12.item90;

import java.io.*;
import java.util.Date;

/**
 * <h2>Consider serialization proxies instead of serialized instances</h2>
 *
 * <p>
 * <b>Core Principle:</b> The serialization proxy pattern greatly reduces the risks of bugs
 * and security vulnerabilities associated with implementing {@code Serializable}. By preventing
 * the creation of instances using the extralinguistic deserialization mechanism, it forces
 * the deserialized instance to be created using the exact same constructors, static factories,
 * and methods as any normal instance.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>True Immutability:</b> Allows the fields of the enclosing class to remain {@code final}, which is required for true immutability.</li>
 * <li><b>Security:</b> Completely stops bogus byte-stream attacks and internal field theft attacks dead in their tracks.</li>
 * <li><b>Simplicity:</b> Eliminates the need to perform explicit validity checking or defensive copying within a custom {@code readObject} method.</li>
 * <li><b>Type Flexibility:</b> Allows the deserialized instance to be of a different class than the originally serialized instance (e.g., {@code EnumSet} returning {@code RegularEnumSet} or {@code JumboEnumSet} depending on size).</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Inheritance Restriction:</b> It is not compatible with classes that are meant to be extendable by their users.</li>
 * <li><b>Circularities:</b> It is not compatible with some classes whose object graphs contain circularities (attempting to invoke a method on such an object from within its proxy's {@code readResolve} throws a {@code ClassCastException}).</li>
 * <li><b>Performance Cost:</b> The added power and safety come with a performance penalty (roughly 14% more expensive to serialize/deserialize than defensive copying).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item19 Inheritance
 * @see puzzlers.effectivejava.ch6.item36 EnumSet
 * @see puzzlers.effectivejava.ch12.item85 SerializationAlternatives
 * @see puzzlers.effectivejava.ch12.item86 SerializableCaution
 * @see puzzlers.effectivejava.ch12.item88 MutablePeriodAttack
 */
public final class Period implements Serializable {

    // --- Enclosing Class State ---

    // Fields can be definitively final thanks to the proxy pattern
    private final Date start;
    private final Date end;

    /**
     * Standard constructor that establishes invariants.
     */
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(this.start + " after " + this.end);
        }
    }

    public Date start() { return new Date(start.getTime()); }
    public Date end() { return new Date(end.getTime()); }

    @Override
    public String toString() { return start + " - " + end; }

    // --- 1. writeReplace Method ---

    /**
     * Translates an instance of the enclosing class to its serialization proxy
     * prior to serialization. The serialization system will never generate a
     * serialized instance of the enclosing class.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // --- 2. readObject Method (Attack Prevention) ---

    /**
     * Guarantees that any attempt by an attacker to fabricate a serialized
     * instance of the enclosing class will fail.
     */
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    // --- 3. The Serialization Proxy Nested Class ---

    /**
     * A private static nested class that concisely represents the logical state
     * of an instance of the enclosing class.
     */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 234098243823485285L;

        private final Date start;
        private final Date end;

        /**
         * Constructor merely copies the data; no consistency checking or
         * defensive copying is needed here.
         */
        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        /**
         * Translates the serialization proxy back into an instance of the
         * enclosing class upon deserialization, utilizing the public API.
         */
        private Object readResolve() {
            // Uses the public constructor, automatically enforcing all invariants!
            return new Period(start, end);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Period period = new Period(new Date(), new Date(System.currentTimeMillis() + 100000));
        String filename = "period_proxy.ser";

        System.out.println("Original Period: " + period);

        // Serialize the object (writeReplace will swap it for SerializationProxy)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(period);
            System.out.println("Period serialized successfully via Proxy.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Deserialize the object (readResolve will convert the Proxy back to Period)
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Period deserializedPeriod = (Period) ois.readObject();
            System.out.println("Deserialized Period: " + deserializedPeriod);

            // Verifying the type returned is indeed Period, not the Proxy
            System.out.println("Deserialized class type: " + deserializedPeriod.getClass().getSimpleName());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            new File(filename).delete();
        }
    }
}