package puzzlers.bloch.libraryadvanced.puzzle1;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The Reflection Infection: Inaccessible Qualifying Type Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 78
 * <p>
 * Problem:
 * Why does {@code m.invoke(it)} throw an {@code IllegalAccessException} even
 * though the method {@code hasNext()} is {@code public} and the interface
 * {@code Iterator} is {@code public}?
 * <p>
 * Explanation:
 * - This puzzle reveals a strict rule in Java reflection: to invoke a method,
 * the **class** from which you obtained the {@code Method} object must be
 * accessible to the caller.
 * - When you call {@code it.getClass()}, you get the concrete implementation
 * class (e.g., {@code java.util.HashMap$KeyIterator}). This class is
 * **package-private**, making it inaccessible outside its package.
 * - Even though the method {@code hasNext()} is public, obtaining it via
 * the private concrete class "infects" the {@code Method} object with
 * the class's inaccessibility.
 * <p>
 * The "Qualifying Type" Rule (The Client Example):
 * - In the second example, {@code Api.member.hashCode()} fails to compile.
 * - Even though {@code hashCode()} is a public method of {@code Object},
 * the "qualifying type" used to find it is {@code PackagePrivate}.
 * - Since {@code Client} cannot see {@code PackagePrivate}, it cannot
 * access its members, even those inherited from {@code Object}.
 * <p>
 *
 * <p>
 * Step-by-step (The Fixes):
 * 1. **Reflection Fix:** Always obtain {@code Method} objects from an
 * accessible interface or public superclass. Using {@code Iterator.class.getMethod}
 * works because {@code Iterator} is a public interface.
 * 2. **Compilation Fix:** Use an accessible qualifying type by casting the
 * object to a public type (like {@code Object}) before accessing the member.
 * <p>
 * Lesson:
 * - Access control is not just about the member itself; it's about the
 * path you take to get to it.
 * - In reflection, the {@code Class} object you use to call {@code getMethod}
 * matters immensely.
 * - Always prefer public interfaces or public superclasses as the
 * "entry point" for your reflection or member access.
 * <p>
 * Output:
 * // Reflection: Exception in thread "main" java.lang.IllegalAccessException
 * // Client: Compilation error (The type library.Api.PackagePrivate is not visible)
 */
public class Reflector { // Example 1: The Reflection Trap
    public static void main(String[] args) throws Exception {
        Set<String> s = new HashSet<>();
        s.add("foo");
        Iterator it = s.iterator();

        // BUG: it.getClass() returns a non-public implementation class java.util.HashMap.KeyIterator
        Method mBug = it.getClass().getMethod("hasNext");
        // System.out.println(mBug.invoke(it)); // Throws IllegalAccessException

        // FIX: Use the public interface Class object
        Method mFix = Iterator.class.getMethod("hasNext");
        System.out.println(mFix.invoke(it)); // Returns true
    }
}