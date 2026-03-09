package puzzlers.effectivejava.ch12.item86;

import java.io.*;

/**
 * <h2>Implement Serializable with great caution</h2>
 *
 * <p>
 * <b>Core Principle:</b> Implementing {@code Serializable} is not a decision to be
 * undertaken lightly. While the immediate cost is negligible, the long-term costs
 * include decreased flexibility to change the class's internal representation,
 * an increased likelihood of security holes, and a significant testing burden
 * across versions.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Framework Participation:</b> Necessary for classes used in frameworks
 * relying on Java serialization (e.g., RMI, legacy GUI state persistence).</li>
 * <li><b>Composition Ease:</b> Makes it easier for the class to be used as a
 * component in another class that must be serializable.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>API Lock-in:</b> The serialized form becomes part of the exported API,
 * making it difficult to change private fields without breaking compatibility.</li>
 * <li><b>Serial Version UID Fragility:</b> If not explicitly declared, any change
 * to the class structure (even adding a method) changes the automatically
 * generated SHA-1 hash, breaking binary compatibility.</li>
 * <li><b>Hidden Constructor Risk:</b> Deserialization is an "extralinguistic"
 * mechanism that can bypass class invariants and constructors, creating
 * security vulnerabilities.</li>
 * <li><b>Testing Overhead:</b> Every new release requires testing
 * serialization/deserialization compatibility between the new and all
 * previous versions.</li>
 * <li><b>Inheritance Constraints:</b> Classes designed for inheritance should
 * rarely implement {@code Serializable}, as it places a massive burden on subclasses.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 InformationHiding
 * @see puzzlers.effectivejava.ch12.item85 SerializationAlternatives
 * @see puzzlers.effectivejava.ch12.item87 CustomSerializedForm
 * @see puzzlers.effectivejava.ch12.item90 SerializationProxy
 */
public class SerializableCaution implements Serializable {

    /**
     * ALWAYS declare a serialVersionUID. This prevents the system from
     * generating a new one if you add a simple method or change a non-serialized field.
     */
    private static final long serialVersionUID = 1L;

    private final String name;
    private int value;

    public SerializableCaution(String name, int value) {
        this.name = name;
        this.value = value;
    }

    // --- 1. Protection for Extendable Classes ---

    /**
     * If this class is designed for inheritance, we must prevent "finalizer attacks"
     * by making finalize final.
     */
    @Override
    protected final void finalize() {}

    /**
     * readObjectNoData is required for stateful extendable serializable classes
     * to handle the corner case where a serializable superclass is added
     * to an existing serializable subclass.
     */
    private void readObjectNoData() throws InvalidObjectException {
        throw new InvalidObjectException("Stream data required");
    }

    // --- 2. Inner Classes vs Static Member Classes ---

    /**
     * Inner classes (non-static) should NOT implement Serializable.
     * They contain synthetic fields (like references to the outer instance)
     * that make their serialized form ill-defined.
     */
    public class InnerClass {
        // Implementation omitted: DO NOT make this Serializable
    }

    /**
     * Static member classes CAN safely implement Serializable if necessary.
     */
    public static class StaticMemberClass implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        SerializableCaution instance = new SerializableCaution("CautionExample", 42);
        String filename = "caution.ser";

        // Demonstrate the serialization lifecycle
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(instance);
            System.out.println("Object serialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            SerializableCaution deserialized = (SerializableCaution) ois.readObject();
            System.out.println("Object deserialized. Name: " + deserialized.name);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            new File(filename).delete();
        }
    }
}