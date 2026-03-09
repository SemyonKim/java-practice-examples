package puzzlers.effectivejava.ch11.item83;

/**
 * <h2>Use lazy initialization judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> Under most circumstances, normal initialization is preferable to
 * lazy initialization. Lazy initialization is the act of delaying the initialization of a
 * field until its value is needed. It should be used primarily as an optimization when a
 * field is accessed on only a fraction of instances and is costly to initialize, or to
 * break harmful circularities in class and instance initialization.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>General Lazy Initialization:</b> Decreases the cost of initializing a class or
 * creating an instance. Can break harmful initialization circularities.</li>
 * <li><b>Synchronized Accessor Idiom:</b> The simplest and clearest alternative to break
 * an initialization circularity.</li>
 * <li><b>Lazy Initialization Holder Class Idiom:</b> Adds practically nothing to the cost
 * of access for static fields because the method is not synchronized. It relies on the VM
 * to synchronize field access only during class initialization.</li>
 * <li><b>Double-Check Idiom:</b> Avoids the cost of locking when accessing an instance
 * field after it has already been initialized.</li>
 * <li><b>Single-Check Idiom:</b> Provides faster lazy initialization for instance fields
 * that can tolerate being initialized more than once by different threads.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>General Lazy Initialization:</b> It is a double-edged sword; it increases the
 * cost of accessing the lazily initialized field. It can harm performance if the field
 * is accessed frequently or cheap to initialize. Severe bugs can result if shared across
 * threads without proper synchronization.</li>
 * <li><b>Synchronized Accessor Idiom:</b> Incurs the overhead of a synchronized method
 * call every time the field is accessed, even after initialization.</li>
 * <li><b>Lazy Initialization Holder Class Idiom:</b> Only applicable to {@code static} fields.</li>
 * <li><b>Double-Check Idiom:</b> Requires the field to be declared {@code volatile}, and
 * the code is slightly convoluted (requires a local variable for optimal performance).</li>
 * <li><b>Single-Check Idiom:</b> Can cause repeated initializations of the same field if
 * accessed concurrently before the first initialization completes. Still requires the
 * {@code volatile} modifier (unless using the highly exotic "racy single-check" variant).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch9.item67 OptimizeJudiciously
 * @see puzzlers.effectivejava.ch11.item78 SynchronizationAndVolatile
 * @see puzzlers.effectivejava.ch11.item79 AvoidExcessiveSynchronization
 * @see puzzlers.effectivejava.ch4.item17 FinalModifier
 */
public class LazyInitialization {

    // Dummy class to represent the type of the field being initialized
    private static class FieldType {
        private final String name;
        FieldType(String name) {
            this.name = name;
            System.out.println("Initialized FieldType: " + name);
        }
        @Override
        public String toString() { return name; }
    }

    // Helper methods to simulate expensive computations
    private FieldType computeFieldValue() {
        return new FieldType("Instance Field Value");
    }

    private static FieldType computeFieldValueStatic() {
        return new FieldType("Static Field Value");
    }

    // --- 1. Normal Initialization ---

    /**
     * Normal initialization of an instance field.
     * Preferable under most circumstances. Note the use of the final modifier.
     */
    private final FieldType normalField = computeFieldValue();

    public FieldType getNormalField() {
        return normalField;
    }


    // --- 2. Synchronized Accessor ---

    /**
     * Lazy initialization of instance field - synchronized accessor.
     * Use this to break an initialization circularity.
     */
    private FieldType syncField;

    public synchronized FieldType getSyncField() {
        if (syncField == null) {
            syncField = computeFieldValue();
        }
        return syncField;
    }


    // --- 3. Lazy Initialization Holder Class Idiom (Static Fields) ---

    /**
     * Lazy initialization holder class idiom for static fields.
     * Use this if you need lazy initialization for performance on a static field.
     */
    private static class FieldHolder {
        static final FieldType field = computeFieldValueStatic();
    }

    public static FieldType getStaticField() {
        return FieldHolder.field;
    }


    // --- 4. Double-Check Idiom (Instance Fields) ---

    /**
     * Double-check idiom for lazy initialization of instance fields.
     * Use this for performance on an instance field. Requires 'volatile'.
     */
    private volatile FieldType doubleCheckField;

    public FieldType getDoubleCheckField() {
        // The local variable 'result' ensures the field is read only once in the common case
        FieldType result = doubleCheckField;
        if (result == null) { // First check (no locking)
            synchronized (this) {
                if (doubleCheckField == null) { // Second check (with locking)
                    doubleCheckField = result = computeFieldValue();
                }
            }
        }
        return result;
    }


    // --- 5. Single-Check Idiom (Instance Fields, Tolerates Repeated Init) ---

    /**
     * Single-check idiom.
     * Use this if you can tolerate repeated initialization. Can cause repeated initializations!
     */
    private volatile FieldType singleCheckField;

    public FieldType getSingleCheckField() {
        FieldType result = singleCheckField;
        if (result == null) {
            singleCheckField = result = computeFieldValue();
        }
        return result;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        System.out.println("Creating LazyInitialization instance...");
        // At this point, only 'normalField' is initialized.
        LazyInitialization instance = new LazyInitialization();

        System.out.println("\nAccessing normal field:");
        System.out.println(instance.getNormalField());

        System.out.println("\nAccessing synchronized lazy field for the first time:");
        System.out.println(instance.getSyncField());

        System.out.println("\nAccessing static lazy field via Holder Class for the first time:");
        System.out.println(LazyInitialization.getStaticField());

        System.out.println("\nAccessing double-checked lazy field for the first time:");
        System.out.println(instance.getDoubleCheckField());
        System.out.println("Accessing double-checked lazy field again (no lock cost):");
        System.out.println(instance.getDoubleCheckField());

        System.out.println("\nAccessing single-checked lazy field for the first time:");
        System.out.println(instance.getSingleCheckField());
    }
}