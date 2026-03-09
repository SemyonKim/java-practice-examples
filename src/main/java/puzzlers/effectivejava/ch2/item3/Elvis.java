package puzzlers.effectivejava.ch2.item3;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * <h2>Enforce the singleton property with a private constructor or an enum type</h2>
 *
 * <p><strong>Core Principle:</strong> A singleton is a class instantiated exactly once, typically
 * representing stateless objects or unique system components. To maintain this property,
 * keep the constructor private and export a public static member to provide access.</p>
 *
 * <h3>Public Final Field Approach</h3>
 * <ul>
 * <li><b>Advantage 1:</b> API Clarity - The public static final field makes it obvious that the class is a singleton.</li>
 * <li><b>Advantage 2:</b> Simplicity - It is the most straightforward implementation.</li>
 * </ul>
 *
 * <h3>Static Factory Approach</h3>
 * <ul>
 * <li><b>Advantage 1:</b> Flexibility - Allows changing the implementation to return a different instance (e.g., per-thread) without changing the API.</li>
 * <li><b>Advantage 2:</b> Generics - Enables the creation of a generic singleton factory if required.</li>
 * <li><b>Advantage 3:</b> Functional Compatibility - A method reference (e.g., <code>Elvis::getInstance</code>) can be used as a {@link Supplier}.</li>
 * </ul>
 *
 * <h3>Enum Singleton Approach (Preferred)</h3>
 * <ul>
 * <li><b>Advantage 1:</b> Conciseness - Minimal boilerplate code.</li>
 * <li><b>Advantage 2:</b> Automatic Serialization - Provides the serialization machinery for free.</li>
 * <li><b>Advantage 3:</b> Ironclad Guarantee - Absolute protection against multiple instantiation, even via reflection or complex serialization attacks.</li>
 * </ul>
 *
 * <h3>General Limitations & Caveats</h3>
 * <ul>
 * <li><b>Limitation 1:</b> Testing Difficulty - Singletons are hard to mock for testing unless they implement an interface.</li>
 * <li><b>Limitation 2 (Field/Factory):</b> Reflection Attack - A privileged client can use <code>AccessibleObject.setAccessible</code> to invoke the private constructor.</li>
 * <li><b>Limitation 3 (Field/Factory):</b> Serialization Overhead - Requires <code>implements Serializable</code>, making all fields <code>transient</code>, and adding a <code>readResolve</code> method.</li>
 * <li><b>Limitation 4 (Enum):</b> Inheritance Constraint - An enum singleton cannot extend a superclass (other than <code>Enum</code>).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item24 StatelessObjects
 * @see puzzlers.effectivejava.ch5.item30 GenericSingletonFactory
 * @see puzzlers.effectivejava.ch9.item65 Reflection
 * @see puzzlers.effectivejava.ch12.item89 readResolve
 */
public class Elvis {

    // --- APPROACH 1: PUBLIC FINAL FIELD ---
    public static class ElvisField {
        public static final ElvisField INSTANCE = new ElvisField();

        private ElvisField() {
            // Defense against reflection attack (Item 65)
            if (INSTANCE != null) {
                throw new IllegalStateException("Already initialized");
            }
        }

        public void leaveTheBuilding() {
            System.out.println("Elvis (Field) has left.");
        }
    }

    // --- APPROACH 2: STATIC FACTORY ---
    public static class ElvisFactory implements Serializable {
        private static final ElvisFactory INSTANCE = new ElvisFactory();

        // Fields must be transient for serialization safety (Chapter 12)
        private transient String name = "Elvis";

        private ElvisFactory() { }

        public static ElvisFactory getInstance() {
            return INSTANCE;
        }

        public void leaveTheBuilding() {
            System.out.println("Elvis (Factory) has left.");
        }

        /**
         * <b>Serialization Defense:</b>
         * Ensures that deserialization returns the same instance (Item 89).
         */
        private Object readResolve() {
            // Return the one true Elvis and let the garbage collector
            // take care of the Elvis impersonator.
            return INSTANCE;
        }
    }

    // --- APPROACH 3: ENUM SINGLETON ---
    public enum ElvisEnum {
        INSTANCE;

        public void leaveTheBuilding() {
            System.out.println("Elvis (Enum) has left.");
        }
    }

    /**
     * <b>Client Usage Example</b>
     */
    public void clientUsage() {
        // 1. Using Public Field
        ElvisField.INSTANCE.leaveTheBuilding();

        // 2. Using Static Factory
        ElvisFactory factoryElvis = ElvisFactory.getInstance();
        factoryElvis.leaveTheBuilding();

        // 3. Using Enum (The preferred way)
        ElvisEnum.INSTANCE.leaveTheBuilding();

        // 4. Method Reference Advantage (Supplier)
        Supplier<ElvisFactory> elvisSupplier = ElvisFactory::getInstance;
    }
}