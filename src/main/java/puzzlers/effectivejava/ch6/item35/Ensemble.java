package puzzlers.effectivejava.ch6.item35;

/**
 * <h2>Use instance fields instead of ordinals</h2>
 *
 * <p>
 * <b>Core Principle:</b> Never derive a value associated with an enum from its
 * {@code ordinal()} method. The ordinal is intended for internal use by
 * enum-based data structures. For domain-specific values, always use
 * private final instance fields and a constructor.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Robustness:</b> Reordering constants or adding new ones does not break
 * existing logic, as values are explicitly assigned rather than index-dependent.</li>
 * <li><b>Flexibility:</b> Allows multiple constants to share the same associated
 * value (e.g., both {@code OCTET} and {@code DOUBLE_QUARTET} representing 8).</li>
 * <li><b>Discontinuity Support:</b> You can represent sets with "gaps" in their values
 * without being forced to define dummy constants for unused integers.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>None:</b> This is strictly a superior pattern to using ordinals for data
 * association. The only "cost" is a few lines of boilerplate for the field and constructor.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch6.item34 EnumsOverInts
 * @see java.util.EnumSet
 * @see java.util.EnumMap
 */
public enum Ensemble {
    SOLO(1),
    DUET(2),
    TRIO(3),
    QUARTET(4),
    QUINTET(5),
    SEXTET(6),
    SEPTET(7),
    OCTET(8),
    DOUBLE_QUARTET(8), // Shared value: Not possible with ordinals
    NONET(9),
    DECTET(10),
    TRIPLE_QUARTET(12); // Discontinuous value: No dummy constant for 11 needed

    private final int numberOfMusicians;

    /**
     * Associating data via constructor ensures the value is tied to the
     * constant, not its position in the source file.
     */
    Ensemble(int size) {
        this.numberOfMusicians = size;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Ensemble myGroup = Ensemble.DOUBLE_QUARTET;

        System.out.println("Ensemble: " + myGroup);
        System.out.println("Number of musicians: " + myGroup.numberOfMusicians());

        // Demonstrating that ordinal is unreliable for business logic
        System.out.println("Internal Ordinal (index): " + myGroup.ordinal());
        // Note: Ordinal is 8, but if we moved this constant, it would change.
        // numberOfMusicians() remains 8 regardless of position.

        System.out.println("Triple Quartet musicians: " + Ensemble.TRIPLE_QUARTET.numberOfMusicians());
    }
}