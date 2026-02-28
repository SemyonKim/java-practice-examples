package puzzlers.valeev.library.mistake3;

import java.util.*;

/**
 * Using getClass() instead of instanceOf
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 87
 * <p>
 * Problem:
 * Why does a check like {@code object.getClass() == SomeClass.class} fail even when
 * the object clearly "is a" {@code SomeClass}?
 * <p>
 * Expected behavior:
 * Developers often use {@code getClass()} comparison as a shorthand for type
 * checking, expecting it to behave like the {@code instanceof} operator.
 * <p>
 * Actual behavior:
 * - {@code getClass() == Class.class}: Performs an **exact** match. It returns
 * {@code false} if the object is even a slightly specialized subclass.
 * - {@code instanceof}: Performs a **polymorphic** match. It returns {@code true}
 * if the object is the class or any subclass/implementation.
 * <p>
 * Explanation:
 * - The Abstract Class Trap: You can never have an instance of an abstract class.
 * Therefore, {@code obj.getClass() == AbstractClass.class} is **always false**.
 * - The Calendar Example: {@code Calendar.getInstance()} returns a
 * {@code GregorianCalendar}. Comparing its class to {@code Calendar.class}
 * fails, even though it is a {@code Calendar}.
 * - Use Case for getClass(): It is primarily useful in {@code equals()}
 * implementations where you want to strictly prohibit equality between a
 * superclass and a subclass (to maintain symmetry).
 * <p>
 *
 * <p>
 * Step-by-step (The Calendar Failure):
 * - Call {@code Calendar.getInstance()}.
 * - The JVM creates a {@code GregorianCalendar} instance.
 * - {@code obj instanceof Calendar} is checked: {@code true} (it inherits from Calendar).
 * - {@code obj.getClass() == Calendar.class} is checked: {@code false}
 * ({@code GregorianCalendar.class != Calendar.class}).
 * <p>
 * Fixes:
 * - Default to instanceof: Use {@code instanceof} for almost all type-checking
 * scenarios to support polymorphism.
 * - Class.isInstance(): If the class is only known at runtime (via a variable
 * {@code Class<?> cls}), use {@code cls.isInstance(obj)} instead of
 * {@code cls == obj.getClass()}.
 * - Final Classes: If a class is {@code final}, {@code getClass()} and
 * {@code instanceof} behave identically (except for null handling), but
 * {@code instanceof} is still more idiomatic.
 * - Design: Favor {@code final} classes to prevent unexpected subclass
 * behavior from entering your logic.
 * <p>
 * Lesson:
 * - {@code getClass()} is for identity; {@code instanceof} is for compatibility.
 * - Never use exact class equality for abstract classes or interfaces.
 * <p>
 * Output:
 * - instanceof Calendar: true
 * - getClass() == Calendar: false
 * - Runtime class: class java.util.GregorianCalendar
 */
public class GetClassVsInstanceof {

    public static void main(String[] args) {
        Object dateObj = new java.util.Date();
        Object calObj = Calendar.getInstance();

        System.out.println("--- Date Analysis ---");
        // java.sql.Date is a subclass of java.util.Date
        Object sqlDate = new java.sql.Date(System.currentTimeMillis());

        System.out.println("sqlDate instanceof Date: " + (sqlDate instanceof java.util.Date));
        System.out.println("sqlDate.getClass() == Date.class: " + (sqlDate.getClass() == java.util.Date.class));

        System.out.println("\n--- Calendar Analysis ---");
        System.out.println("calObj runtime class: " + calObj.getClass());

        // This is the common mistake
        if (calObj.getClass() == Calendar.class) {
            System.out.println("Match found via getClass() (This will never print!)");
        }

        // This is the correct way
        if (calObj instanceof Calendar) {
            System.out.println("Match found via instanceof (Correct!)");
        }

        // --- The Conversion Mistake ---
        System.out.println("\n--- Conversion Test ---");
        Object result = convert(calObj);
        System.out.println("Was Calendar converted? " + (result instanceof java.sql.Date));
    }

    /**
     * Demonstrates a faulty conversion method using getClass().
     */
    static Object convert(Object date) {
        Class<?> cls = date.getClass();

        // Exact check used to avoid re-converting java.sql.Date (which is a subclass)
        if (cls == java.util.Date.class) {
            return new java.sql.Date(((java.util.Date) date).getTime());
        }

        // BUG: This will never be true because Calendar is abstract
        if (cls == java.util.Calendar.class) {
            return new java.sql.Date(((java.util.Calendar) date).getTime().getTime());
        }

        return date;
    }
}