package puzzlers.bloch.classier.puzzle1;

/**
 * The Field Hiding Illusion
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 66
 * <p>
 * Problem:
 * Why does the code print "Base" instead of "Derived" (or failing to compile),
 * even though the actual object instance is {@code Derived}?
 * <p>
 * Explanation:
 * - In Java, **methods** are overridden, but **fields** are hidden.
 * - Method calls are resolved at runtime using dynamic dispatch based on the
 * actual object type.
 * - Field accesses are resolved at compile-time based on the **declared type** * of the reference.
 * - In the expression {@code ((Base)new Derived()).name}, the object is cast
 * to a {@code Base} reference. The compiler sees the type {@code Base} and
 * links the access to the {@code public String name} defined in {@code Base}.
 * <p>
 * The "Private" Red Herring:
 * - One might expect a compilation error because {@code Derived.name} is
 * {@code private}. However, because field access is not polymorphic, the
 * compiler isn't even looking at {@code Derived.name}; it only cares about
 * the accessible {@code name} field in the reference type {@code Base}.
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Memory Layout:** A {@code Derived} object actually contains two {@code name}
 * fields: one from {@code Base} and one from {@code Derived}.
 * 2. **Static Resolution:** When you access a field, Java uses the reference
 * type (the "lens") to decide which field to pick.
 * 3. **Casting:** By casting the instance to {@code Base}, you are explicitly
 * telling the compiler to use the {@code Base} lens, which reveals the
 * {@code Base} version of the field.
 * <p>
 * Lesson:
 * - Never hide fields. It makes code confusing and prone to errors.
 * - If you want polymorphic behavior for data, use **accessor methods**
 * (getters) instead of direct field access. Methods can be overridden,
 * ensuring the subclass version is always called.
 * <p>
 * Output:
 * Base
 */
public class PublicMatter {
    public static void main(String[] args) {
        // The cast to (Base) ensures the field is resolved to Base.name
        System.out.println(((Base) new Derived()).name);

//        //Compilation fails -> "Derived.name" is private
//        System.out.println(new Derived().name);
    }
}

class Base {
    public String name = "Base";
}

class Derived extends Base {
    // This field hides Base.name; it does not override it.
    private String name = "Derived";
}