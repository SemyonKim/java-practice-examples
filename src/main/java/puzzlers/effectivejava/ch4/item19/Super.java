package puzzlers.effectivejava.ch4.item19;

import java.time.Instant;

/**
 * <h2>Design and Document for Inheritance or Else Prohibit It</h2>
 *
 * <p>Inheritance is not just about extending a class; it is a formal commitment.
 * To allow safe subclassing, a class must document its <b>self-use</b> of overridable
 * methods and provide necessary internal hooks via protected members.</p>
 *
 *
 *
 * <h3>Core Principle</h3>
 * A class must document precisely the effects of overriding any method.
 * <b>Constructors, {@code clone}, and {@code readObject} must never invoke
 * overridable methods</b>, as this leads to program failure when a subclass
 * is instantiated or deserialized.
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Predictability:</b> Using {@code @implSpec} (Implementation Requirements) clarifies how
 * overriding one method affects others (e.g., how {@code remove} uses {@code iterator}).</li>
 * <li><b>Performance Hooks:</b> Judiciously chosen {@code protected} methods (like
 * {@code removeRange} in {@code AbstractList}) allow subclasses to optimize operations.</li>
 * <li><b>Safety:</b> Prohibiting inheritance in classes not designed for it prevents
 * future maintenance nightmares and "broken" subclasses.</li>
 * </ul>
 *
 * <h3>Limitations & Disadvantages</h3>
 * <ul>
 * <li><b>Encapsulation Breach:</b> Good documentation usually hides <i>how</i> a method
 * works; designing for inheritance forces you to expose implementation details.</li>
 * <li><b>Permanence:</b> Once documented, self-use patterns and protected members
 * become part of the API and must be supported forever.</li>
 * <li><b>Complexity:</b> Testing requires writing at least three subclasses, preferably
 * by different authors, to ensure the design is sound.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item13 OverrideCloneJudiciously
 * @see puzzlers.effectivejava.ch4.item17 MinimizeMutability
 * @see puzzlers.effectivejava.ch4.item18 FavorCompositionOverInheritance
 * @see puzzlers.effectivejava.ch4.item20 PreferInterfacesToAbstractClasses
 * @see puzzlers.effectivejava.ch12.item86 ImplementSerializableWithCaution
 */
public class Super {

    /**
     * Constructs a new Super instance.
     * <b>Broken:</b> This constructor invokes an overridable method.
     * Because the superclass constructor runs before the subclass constructor,
     * any overriding method in a subclass will be executed before the subclass
     * has a chance to initialize its own fields.
     */
    public Super() {
        overrideMe();
    }

    /**
     * An overridable method that is part of the class's internal self-use pattern.
     * @implSpec The default implementation does nothing.
     */
    public void overrideMe() {
        // Base implementation
    }
}

/**
 * A concrete subclass demonstrating the danger of invoking overridable
 * methods from a superclass constructor.
 */
final class Sub extends Super {
    // Blank final, set by constructor
    private final Instant instant;

    Sub() {
        this.instant = Instant.now();
    }

    /**
     * Overriding method invoked by the superclass constructor.
     * This will print 'null' because 'instant' is not yet initialized.
     */
    @Override
    public void overrideMe() {
        System.out.println("Current instant: " + instant);
    }

    /**
     * Client usage demonstrating the initialization failure.
     */
    public static void main(String[] args) {
        // This will print 'null' then the actual instant
        Sub sub = new Sub();
        sub.overrideMe();
    }
}

/**
 * Demonstration of how to safely "prohibit" inheritance or
 * eliminate self-use if inheritance must be allowed.
 */
final class SafeConcreteClass {
    // 1. Declare class as 'final' to prohibit subclassing.

    // OR:
    // 2. Eliminate self-use of overridable methods via private helpers.

    public void overridableMethod() {
        helper();
    }

    private void helper() {
        // Real implementation logic here
    }

    public void anotherMethod() {
        // Instead of calling overridableMethod(), call the private helper.
        helper();
    }
}