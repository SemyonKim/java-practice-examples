package puzzlers.effectivejava.ch12.item88;

import java.io.*;
import java.util.Date;

/**
 * <h2>Write readObject methods defensively</h2>
 *
 * <p>
 * <b>Core Principle:</b> Treat the {@code readObject} method as a public constructor
 * that takes a byte stream as its parameter. Just as a constructor must check
 * arguments for validity and make defensive copies, a {@code readObject} method must
 * do the same to prevent attackers from creating "impossible" objects or stealing
 * internal mutable references.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Invariant Protection:</b> Ensures that objects created via deserialization
 * adhere to the same rules as those created via standard constructors.</li>
 * <li><b>Immutability Preservation:</b> Defensive copying prevents "rogue reference"
 * attacks where an attacker grabs a handle to internal mutable components (like
 * {@code Date}) by manipulating the byte stream.</li>
 * <li><b>Security:</b> Thwarting malicious byte streams prevents the creation of
 * corrupted objects that could crash or compromise a system.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Non-final Fields:</b> To perform defensive copying within {@code readObject},
 * internal fields cannot be {@code final}. This is a necessary trade-off to
 * ensure security.</li>
 * <li><b>Overridable Methods:</b> Like constructors, {@code readObject} must not
 * invoke overridable methods, as they run before the subclass state is initialized.</li>
 * <li><b>Complexity:</b> Manually managing defensive copies and validation
 * increases the maintenance surface of the class.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch8.item49 ValidateParameters
 * @see puzzlers.effectivejava.ch8.item50 DefensiveCopying
 * @see puzzlers.effectivejava.ch12.item90 SerializationProxy
 */
public final class Period implements Serializable {

    // Fields cannot be final if we need to defensively copy in readObject
    private Date start;
    private Date end;

    /**
     * Standard constructor with defensive copying and invariant checks.
     * @param start the beginning of the period
     * @param end the end of the period; must not precede start
     * @throws IllegalArgumentException if start is after end
     * @throws NullPointerException if start or end is null
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

    /**
     * Defensive readObject implementation.
     * <p>
     * 1. Calls defaultReadObject to populate fields.
     * 2. Defensively copies mutable components to prevent "stolen reference" attacks.
     * 3. Checks invariants AFTER copying to ensure integrity.
     * </p>
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        // 1. Defensively copy mutable components (re-linking fields)
        // This is why start and end cannot be final.
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        // 2. Check that our invariants are satisfied
        if (this.start.compareTo(this.end) > 0) {
            throw new InvalidObjectException(start + " after " + end);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) throws Exception {
        Date start = new Date();
        Date end = new Date();
        Period p = new Period(start, end);

        // Standard usage
        System.out.println("Valid Period: " + p);

        // Simulation of a defensive failure (conceptual)
        byte[] serialized = serialize(p);

        try {
            Period deserialized = (Period) deserialize(serialized);
            System.out.println("Deserialized Period: " + deserialized);
        } catch (Exception e) {
            System.err.println("Deserialization failed as expected: " + e.getMessage());
        }
    }

    private static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(o);
        return bos.toByteArray();
    }

    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        return in.readObject();
    }
}