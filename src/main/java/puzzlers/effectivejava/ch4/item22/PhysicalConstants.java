package puzzlers.effectivejava.ch4.item22;

/**
 * <h2>Use interfaces only to define types</h2>
 *
 * <p>
 * <b>Core Principle:</b> An interface should only be used to define a type that describes
 * what a client can do with instances of a class. Avoid the "constant interface"
 * antipattern, which uses interfaces solely to export static constants.
 * </p>
 *
 * <h3>Advantages (of proper interface use)</h3>
 * <ul>
 * <li><b>Type Definition:</b> Clearly communicates the contract and capabilities of a class to its clients.</li>
 * <li><b>Decoupling:</b> Allows multiple implementations to be used interchangeably through the interface type.</li>
 * <li><b>Clean Namespaces:</b> Using utility classes or enums for constants keeps the class hierarchy
 * and public API free from implementation-specific clutter.</li>
 * </ul>
 *
 * <h3>Limitations & Disadvantages (of Constant Interfaces)</h3>
 * <ul>
 * <li><b>API Leakage:</b> Internal implementation details (constants) become part of the class's
 * exported public API.</li>
 * <li><b>Namespace Pollution:</b> All subclasses of a class implementing a constant interface
 * have their namespaces cluttered with those constants.</li>
 * <li><b>Future Lock-in:</b> To maintain binary compatibility, a class must continue to implement
 * the interface even if it no longer needs the constants.</li>
 * <li><b>Client Confusion:</b> Users may be misled into thinking the interface represents
 * a meaningful type or behavior.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item4 Noninstantiability
 * @see puzzlers.effectivejava.ch6.item34 Enums
 */
public class PhysicalConstants {

    // Prevent instantiation of this utility class (Item 4)
    private PhysicalConstants() {
        throw new AssertionError("Noninstantiable utility class");
    }

    /**
     * Avogadro's number (1/mol).
     * Note the use of underscores for readability (Java 7+).
     */
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;

    /**
     * Boltzmann constant (J/K).
     */
    public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;

    /**
     * Mass of the electron (kg).
     */
    public static final double ELECTRON_MASS = 9.109_383_56e-31;

    // --- Client Usage ---

    public static void main(String[] args) {
        double mols = 2.5;

        // Using static import (see import statement at top) to avoid qualification
        double atoms = AVOGADROS_NUMBER * mols;

        // Standard qualification for clarity if static import is not used
        double energy = PhysicalConstants.BOLTZMANN_CONSTANT * 300; // 300K

        System.out.println("Atoms: " + atoms);
        System.out.println("Energy at 300K: " + energy + " J");
    }
}

/**
 * ANTIPATTERN EXAMPLE: Do not do this!
 * This interface is not a type; it is just a "constant bucket."
 */
interface PhysicalConstantsAntipattern {
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
}

/**
 * Bad practice: Implementing an interface just to "shorthand" constants.
 * This makes the interface part of the public API of 'BadClass'.
 */
class BadClass implements PhysicalConstantsAntipattern {
    double getAtoms(double mols) {
        return AVOGADROS_NUMBER * mols; // No qualification, but at what cost?
    }
}