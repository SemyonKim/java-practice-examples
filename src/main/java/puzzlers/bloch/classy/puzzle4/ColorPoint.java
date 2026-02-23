package puzzlers.bloch.classy.puzzle4;

/**
 * Overridable Method in Constructor Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 51
 * <p>
 * Problem:
 * What happens when a base class constructor calls a method that is
 * overridden in a subclass? If the subclass method relies on its own
 * initialized fields, will they be ready?
 * <p>
 * Explanation:
 * - When {@code new ColorPoint} is called, the {@code Point} constructor
 * runs first.
 * - The {@code Point} constructor calls {@code makeName()}.
 * - Because of **dynamic dispatch**, Java invokes the version of the method
 * associated with the actual object type: {@code ColorPoint.makeName()}.
 * - {@code ColorPoint.makeName()} tries to access the {@code color} field.
 * - **The Disaster:** The {@code color} field has not been initialized yet
 * because the {@code ColorPoint} constructor hasn't even started its body
 * (it is still waiting for the {@code super} call to finish).
 * - Therefore, {@code color} is {@code null}.
 * <p>
 * The "Premature" Result:
 * - You expect: {@code [4,2]:purple}
 * - You get: **{@code [4,2]:null}**
 * <p>
 * Step-by-step:
 * 1. {@code ColorPoint} constructor is invoked.
 * 2. It immediately calls {@code super(4, 2)}.
 * 3. {@code Point} constructor sets {@code x=4, y=2}.
 * 4. {@code Point} constructor calls {@code makeName()}.
 * 5. Dynamic dispatch picks {@code ColorPoint.makeName()}.
 * 6. {@code ColorPoint.makeName()} reads {@code this.color}, which is
 * still {@code null}.
 * 7. {@code Point} constructor finishes.
 * 8. {@code ColorPoint} constructor finally sets {@code color = "purple"}.
 * <p>
 * Lesson:
 * - **Rule:** Never call overridable methods from constructors.
 * - If you must call a method, make it {@code private} or {@code final}
 * so it cannot be overridden by a subclass.
 * - Constructors should only perform the simplest possible initialization
 * to avoid exposing "partially baked" objects to subclass logic.
 * <p>
 * Output:
 * // [4,2]:null
 */
class Point {
    protected final int x, y;
    private final String name;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
        // TRAP: Calling an overridable method from constructor!
        this.name = makeName();
    }

    protected String makeName() {
        return "[" + x + "," + y + "]";
    }

    @Override
    public String toString() {
        return name;
    }
}

public class ColorPoint extends Point {
    private final String color;

    ColorPoint(int x, int y, String color) {
        super(x, y);
        this.color = color;
    }

    @Override
    protected String makeName() {
        // This runs while Point is constructing, so color is null!
        return super.makeName() + ":" + color;
    }

    public static void main(String[] args) {
        System.out.println(new ColorPoint(4, 2, "purple"));
    }
}