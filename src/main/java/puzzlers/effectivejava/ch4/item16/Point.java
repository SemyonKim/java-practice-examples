package puzzlers.effectivejava.ch4.item16;

/**
 * <h2>In public classes, use accessor methods, not public fields</h2>
 *
 * <p>
 * <b>Core Principle:</b> If a class is accessible outside its package, provide
 * accessor methods (getters) and, if mutable, mutators (setters). Public classes
 * should never expose mutable fields directly, as this destroys the benefits
 * of encapsulation and ties the API to the internal representation.
 * </p>
 *
 * <h3>Advantages of Accessors</h3>
 * <ul>
 * <li><b>Internal Flexibility:</b> You can change the internal data representation
 * (e.g., switching from Cartesian to Polar coordinates) without breaking client code.</li>
 * <li><b>Invariant Enforcement:</b> Mutator methods can perform validation to
 * ensure the object remains in a valid state (e.g., preventing negative time values).</li>
 * <li><b>Auxiliary Actions:</b> Allows taking additional actions when a field
 * is accessed or modified, such as logging, lazy initialization, or event notification.</li>
 * </ul>
 *
 * <h3>Limitations / Exceptions</h3>
 * <ul>
 * <li><b>Package-Private / Private Nested Classes:</b> For these restricted
 * scopes, exposing fields is acceptable and often desirable to reduce visual
 * clutter, as the "API" is confined to the package or enclosing class.</li>
 * <li><b>Immutable Public Fields:</b> Exposing {@code public final} fields is
 * less harmful than mutable ones. While it allows invariant enforcement,
 * it still limits the flexibility to change the internal data structure.</li>
 * <li><b>Performance:</b> In extremely rare cases, direct field access might
 * be necessary for performance, but this is usually a premature optimization.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 Accessibility
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch9.item67 PerformanceOptimization
 */
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Accessors preserve the ability to change representation later
    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // --- Examples of varying degrees of "Harm" ---

    /**
     * Less harmful but still questionable: Exposed immutable fields.
     * Enforces invariants but locks the API to these specific fields.
     */
    public final class Time {
        private static final int HOURS_PER_DAY = 24;
        private static final int MINUTES_PER_HOUR = 60;

        public final int hour;
        public final int minute;

        public Time(int hour, int minute) {
            if (hour < 0 || hour >= HOURS_PER_DAY)
                throw new IllegalArgumentException("Hour: " + hour);
            if (minute < 0 || minute >= MINUTES_PER_HOUR)
                throw new IllegalArgumentException("Min: " + minute);
            this.hour = hour;
            this.minute = minute;
        }
    }

    /**
     * Acceptable: Package-private nested class exposing fields.
     * Reduces boilerplate for internal implementation details.
     */
    static class Dimension {
        double width;
        double height;
    }

    // --- Client Usage ---

    public static void clientUsage() {
        Point p = new Point(10.5, 20.0);

        // Encapsulation allows us to intercept this call if we needed to
        p.setX(15.0);

        // For package-private classes, direct access is cleaner
        Dimension d = new Dimension();
        d.width = 100;

        System.out.println("Point X: " + p.getX());
        System.out.println("Dimension Width: " + d.width);
    }
}