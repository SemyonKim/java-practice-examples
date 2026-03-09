package puzzlers.effectivejava.ch12.item89;

import java.io.*;
import java.util.Arrays;

/**
 * <h2>For instance control, prefer enum types to readResolve</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use {@code enum} types to enforce instance control invariants
 * (like singletons) wherever possible. If a class must be serializable and instance-controlled,
 * but cannot be an enum, you must provide a {@code readResolve} method and ensure that all
 * instance fields with object reference types are either primitive or {@code transient}.
 * </p>
 *
 * <h3>Advantages of Enum Types</h3>
 * <ul>
 * <li><b>Ironclad Guarantee:</b> Java inherently guarantees that there can be no instances besides the declared constants.</li>
 * <li><b>Simplicity:</b> Serialized form does not require custom handling, and fields do not need to be marked transient.</li>
 * <li><b>Security:</b> Completely immune to circularity "stealer" attacks during deserialization.</li>
 * </ul>
 *
 * <h3>Limitations of Enum Types</h3>
 * <ul>
 * <li><b>Compile-Time Restriction:</b> Cannot be used if the serializable instances are not known at compile time.</li>
 * </ul>
 *
 * <h3>Advantages of readResolve</h3>
 * <ul>
 * <li><b>Dynamic Instances:</b> Essential for serializable instance-controlled classes whose instances are only known at runtime.</li>
 * </ul>
 *
 * <h3>Limitations of readResolve</h3>
 * <ul>
 * <li><b>Extreme Fragility:</b> Demands great care to prevent temporary deserialized instances from being accessed by attackers.</li>
 * <li><b>Transient Requirement:</b> All object reference fields MUST be declared {@code transient} to avoid "stealer" attacks.</li>
 * <li><b>Accessibility Complexity:</b> Placing {@code readResolve} on nonfinal classes requires strict attention to access modifiers (private, package-private, protected) to prevent {@code ClassCastException} in subclasses.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item3 Singleton
 * @see puzzlers.effectivejava.ch12.item87 CustomSerializedForm
 * @see puzzlers.effectivejava.ch12.item88 MutablePeriodAttack
 */
public class EnumVsReadResolve {

    // --- 1. The Preferred Approach: Enum Singleton ---

    /**
     * Enum singleton - the preferred approach.
     * Protects against serialization vulnerabilities naturally without readResolve.
     */
    public enum ElvisEnum {
        INSTANCE;
        private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };

        public void printFavorites() {
            System.out.println(Arrays.toString(favoriteSongs));
        }
    }

    // --- 2. The Fragile Approach: readResolve (Broken Example) ---

    /**
     * Broken singleton - has a nontransient object reference field!
     * If deserialized, a carefully crafted stream can "steal" a reference
     * before readResolve is executed.
     */
    public static class Elvis implements Serializable {
        public static final Elvis INSTANCE = new Elvis();
        private Elvis() { }

        // VULNERABILITY: Nontransient object reference field in a readResolve singleton
        private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };

        public void printFavorites() {
            System.out.println(Arrays.toString(favoriteSongs));
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    // --- 3. The Attacker: Stealer Class ---

    /**
     * "Stealer" class constructed to exploit the nontransient field in Elvis.
     * It creates a circularity where it hides in the singleton and captures the reference.
     */
    public static class ElvisStealer implements Serializable {
        private static final long serialVersionUID = 0;
        static Elvis impersonator;
        private Elvis payload;

        private Object readResolve() {
            // Save a reference to the "unresolved" Elvis instance
            impersonator = payload;
            // Return object of correct type for favoriteSongs field to avoid ClassCastException
            return new String[] { "A Fool Such as I" };
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        System.out.println("=== Safe Enum Singleton ===");
        ElvisEnum safeElvis = ElvisEnum.INSTANCE;
        safeElvis.printFavorites();

        System.out.println("\n=== Vulnerable readResolve Attack Concept ===");
        System.out.println("If a crafted byte stream is deserialized, it creates two distinct Elvis instances:");
        System.out.println("Original: [Hound Dog, Heartbreak Hotel]");
        System.out.println("Impersonator: [A Fool Such as I]");

        /* * Note: The following byte stream represents the attack described in the text.
         * It is commented out because it relies on the exact package structure and class
         * names from the original execution to prevent a ClassNotFoundException.
         * <p>
         * byte[] serializedForm = {
                (byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x05,
                0x45, 0x6c, 0x76, 0x69, 0x73, (byte)0x84, (byte)0xe6,
                (byte)0x93, 0x33, (byte)0xc3, (byte)0xf4, (byte)0x8b,
                0x32, 0x02, 0x00, 0x01, 0x4c, 0x00, 0x0d, 0x66, 0x61, 0x76,
                0x6f, 0x72, 0x69, 0x74, 0x65, 0x53, 0x6f, 0x6e, 0x67, 0x73,
                0x74, 0x00, 0x12, 0x4c, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c,
                0x61, 0x6e, 0x67, 0x2f, 0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74,
                0x3b, 0x78, 0x70, 0x73, 0x72, 0x00, 0x0c, 0x45, 0x6c, 0x76,
                0x69, 0x73, 0x53, 0x74, 0x65, 0x61, 0x6c, 0x65, 0x72, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,
                0x4c, 0x00, 0x07, 0x70, 0x61, 0x79, 0x6c, 0x6f, 0x61, 0x64,
                0x74, 0x00, 0x07, 0x4c, 0x45, 0x6c, 0x76, 0x69, 0x73, 0x3b,
                0x78, 0x70, 0x71, 0x00, 0x7e, 0x00, 0x02
                };
         * Elvis elvis = (Elvis) deserialize(serializedForm);
         * Elvis impersonator = ElvisStealer.impersonator;
         * elvis.printFavorites();
         * impersonator.printFavorites();
         */
    }
}