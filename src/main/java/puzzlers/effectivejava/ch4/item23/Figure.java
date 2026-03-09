package puzzlers.effectivejava.ch4.item23;

/**
 * <h2>Prefer class hierarchies to tagged classes</h2>
 *
 * <p>
 * <b>Core Principle:</b> Avoid "tagged classes"—classes that use a tag field and
 * switch statements to manage multiple flavors. Instead, refactor them into a class
 * hierarchy where each flavor is represented by its own subclass.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Type Safety:</b> The compiler ensures all methods are implemented; no risk of
 * missing a {@code switch} case at runtime.</li>
 * <li><b>Clarity and Readability:</b> Eliminates boilerplate (enums, tag fields, switch logic)
 * and separates concerns into distinct classes.</li>
 * <li><b>Memory Efficiency:</b> Instances only contain the fields relevant to their flavor.</li>
 * <li><b>Extensibility:</b> New flavors can be added by multiple programmers independently
 * without modifying the original source code.</li>
 * <li><b>Natural Relationships:</b> Hierarchies can reflect real-world relationships,
 * such as a Square being a special kind of Rectangle.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Complexity:</b> For extremely simple cases where the "flavors" are purely
 * data-driven and share no logic, a hierarchy might feel like overkill, though it
 * is still technically safer.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item16 Accessors
 * @see puzzlers.effectivejava.ch4.item20 InterfacesVsAbstractClasses
 */
public abstract class Figure {

    /**
     * Calculates the area of the figure.
     * <p>
     * For a circle, the formula is: A = PI * radius * radius
     * For a rectangle, the formula is: A = width * length
     * </p>
     */
    abstract double area();

    // --- Implementation: Circle ---

    public static class Circle extends Figure {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * (radius * radius);
        }
    }

    // --- Implementation: Rectangle ---

    public static class Rectangle extends Figure {
        private final double length;
        private final double width;

        public Rectangle(double length, double width) {
            this.length = length;
            this.width = width;
        }

        @Override
        double area() {
            return length * width;
        }
    }

    // --- Implementation: Square (Hierarchical Relationship) ---

    public static class Square extends Rectangle {
        public Square(double side) {
            super(side, side);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Figure circle = new Circle(5.0);
        Figure rectangle = new Rectangle(4.0, 7.0);
        Figure square = new Square(4.0);

        System.out.printf("Circle area: %.2f%n", circle.area());
        System.out.printf("Rectangle area: %.2f%n", rectangle.area());
        System.out.printf("Square area: %.2f%n", square.area());

        // Hierarchy allows specific type-checking and better API design
        if (square instanceof Rectangle) {
            System.out.println("A Square is indeed a Rectangle.");
        }
    }
}

/**
 * ANTIPATTERN: A tagged class.
 * This is verbose, inefficient, and prone to runtime errors if a new Shape is added.
 */
class TaggedFigure {
    enum Shape { RECTANGLE, CIRCLE }

    final Shape shape; // The tag field
    double length;     // Only used if RECTANGLE
    double width;      // Only used if RECTANGLE
    double radius;     // Only used if CIRCLE

    // Constructor for circle
    TaggedFigure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // Constructor for rectangle
    TaggedFigure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch(shape) {
            case RECTANGLE: return length * width;
            case CIRCLE:    return Math.PI * (radius * radius);
            default:        throw new AssertionError(shape);
        }
    }
}