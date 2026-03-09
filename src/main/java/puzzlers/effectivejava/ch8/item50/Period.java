package puzzlers.effectivejava.ch8.item50;

import java.util.Date;

/**
 * <h2>Make defensive copies when needed</h2>
 *
 * <p>
 * <b>Core Principle:</b> You must program defensively with the assumption that clients
 * of your class will do their best (intentionally or accidentally) to destroy its
 * invariants. If a class has mutable components that it receives from or returns to
 * its clients, it must make defensive copies of these components.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Invariant Protection:</b> Ensures that the internal state of an object remains
 * consistent regardless of changes made to the objects passed into its constructor.</li>
 * <li><b>Security:</b> Protects against "Time-of-Check/Time-of-Use" (TOCTOU) attacks by
 * copying parameters before validating them.</li>
 * <li><b>True Encapsulation:</b> Prevents clients from obtaining a reference to internal
 * mutable fields, making the class effectively immutable even if it uses mutable types.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Performance Cost:</b> Defensive copying incurs a penalty in both memory and
 * execution time, which might be prohibitive for large objects or high-frequency calls.</li>
 * <li><b>Documentation Requirement:</b> If copying is skipped for performance or trust
 * reasons (e.g., across package boundaries), the API must clearly document the "handoff"
 * and the client's responsibility not to modify the object.</li>
 * <li><b>Clone Vulnerability:</b> {@code clone()} should not be used to copy parameters
 * of types that could be subclassed by untrusted parties.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 Accessibility
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item18 WrapperClasses
 * @see puzzlers.effectivejava.ch8.item49 ValidityChecks
 * @see puzzlers.effectivejava.ch3.item13 Cloning
 */
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * Repaired Constructor: Defensive copying of parameters.
     * Note: Copying happens BEFORE validity check to prevent TOCTOU attacks.
     * Note: We don't use start.clone() because Date is non-final (security risk).
     */
    public Period(Date start, Date end) {
        // 1. Create the copies first
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        // 2. Validate the copies, not the originals
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(this.start + " after " + this.end);
        }
    }

    /**
     * Repaired Accessor: Returning defensive copies.
     * This prevents clients from modifying the internal state via returned references.
     */
    public Date start() {
        return new Date(start.getTime());
    }

    /**
     * Repaired Accessor: Returning defensive copies.
     */
    public Date end() {
        return new Date(end.getTime());
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Attack 1: Modifying the parameters after construction
        Date start = new Date();
        Date end = new Date();
        Period p = new Period(start, end);

        // This modification would affect a "broken" Period, but not the "repaired" one
        end.setYear(78);
        System.out.println("Period after parameter attack: " + p);

        // Attack 2: Modifying the internal state via accessors
        p.start().setYear(78);
        System.out.println("Period after accessor attack: " + p);

        /*
         * Better Practice: Use java.time.Instant or LocalDateTime.
         * These are immutable and remove the need for defensive copying.
         */
    }
}