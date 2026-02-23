package puzzlers.bloch.classy.puzzle6;

import java.util.Random;

/**
 * The Private Constructor Capture
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 53
 * <p>
 * Problem:
 * You need to pass a value to {@code super()} and store it in a field.
 * If you try to assign the field *inside* the {@code super()} call, the
 * compiler stops you. If you call the random/dynamic method twice,
 * the values won't match.
 * <p>
 * The "Invalid" Approach:
 * class MyThing extends Thing {
 *      private final int arg;
 *      MyThing() {
 *          // ERROR: cannot reference arg before supertype constructor has been called.
 *          super(arg = new Random().nextInt());
 *      }
 * }
 * <p>
 * Explanation:
 * - Java prevents you from accessing any instance members (fields or
 * methods) of a class until its superclass has been fully initialized.
 * - Even though {@code arg = ...} looks like an expression, it is
 * attempting to write to a field of {@code MyThing} before
 * {@code Thing} exists.
 * - **The Solution:** Use a private constructor to "capture" the value
 * as a parameter. Parameters are accessible throughout the constructor
 * call, unlike instance fields.
 * <p>
 * Step-by-step (The Fix):
 * 1. The public constructor calls {@code this(new Random().nextInt())}.
 * 2. The value is generated and passed as the argument {@code captured}.
 * 3. The private constructor receives {@code captured}.
 * 4. It safely calls {@code super(captured)} and assigns
 * {@code this.arg = captured}.
 * <p>
 * Lesson:
 * - You cannot use instance fields of a subclass as "scratchpad" storage
 * during a {@code super()} call.
 * - Constructor chaining ({@code this()}) is the idiomatic way to
 * share a single computed value between a super-constructor and a
 * local field.
 */
class Thing {
    private final int value;

    Thing(int value) {
        this.value = value;
    }
}

public class MyThing extends Thing {
    private final int arg;

    // Public constructor generates the value and "hands it off"
    public MyThing() {
        this(new Random().nextInt(10));
    }

    // Private constructor captures the value in a parameter to reuse it
    private MyThing(int captured) {
        super(captured);
        this.arg = captured;
    }

    public static void main(String[] args) {
        MyThing mt = new MyThing();
        System.out.println("Arg: " + mt.arg);
    }
}