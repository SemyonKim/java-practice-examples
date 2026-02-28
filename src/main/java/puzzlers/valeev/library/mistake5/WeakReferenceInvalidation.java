package puzzlers.valeev.library.mistake5;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Accidental invalidation of weak or soft references
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 91
 * <p>
 * Problem:
 * Why does {@code ref.get()} sometimes return {@code null} even immediately after
 * creating the reference?
 * <p>
 * Expected behavior:
 * Developers expect a local variable holding a strong reference to keep an object
 * alive until the end of the current scope (the closing brace of the method).
 * <p>
 * Actual behavior:
 * - JIT compilers optimize for memory efficiency. They release a variable as soon
 * as its last **actual use** occurs, not at the end of the scope.
 * - If the strong reference is "dead" from the compiler's perspective, the garbage
 * collector (GC) can reclaim the object even while the method is still executing.
 * <p>
 * Explanation:
 * - Strong Reference: The standard {@code Object obj = new Object();} keeps the
 * object alive.
 * - Weak Reference: Tells the JVM "I don't mind if this is collected if no strong
 * references exist."
 * - Soft Reference: Similar to weak, but cleared only when heap memory is low.
 * - Reachability: If a JIT compiler determines a local variable is never read
 * again, that strong reference is effectively gone, making the object
 * "weakly reachable."
 * <p>
 * Step-by-step (The GC Race):
 * - A strong reference {@code obj} is created.
 * - A {@code WeakReference ref} is created pointing to {@code obj}.
 * - The variable {@code obj} is never mentioned again in the code.
 * - The JIT compiler marks the reference held by {@code obj} as disposable.
 * - GC triggers at that exact moment.
 * - {@code ref.get()} is called and returns {@code null} because the object was collected.
 * <p>
 * Fixes:
 * - Local Variable Capture: Always store the result of {@code ref.get()} in a
 * local strong variable and check it for {@code null} before use.
 * - reachabilityFence(): In Java 9+, use {@code Reference.reachabilityFence(obj)}
 * at the end of the method to ensure {@code obj} remains strongly reachable
 * until that line.
 * - Avoid multiple get() calls: Calling {@code get()} twice might return the
 * object the first time and {@code null} the second time if GC fires in between.
 * <p>
 * Lesson:
 * - Scope does not guarantee lifetime.
 * - Never assume a weak/soft reference stays populated without a local strong
 * reference to guard it.
 * <p>
 * Output:
 * - Value from weak ref: [Could be the hashcode OR NullPointerException]
 */
public class WeakReferenceInvalidation {

    public static void main(String[] args) {
        demonstratePotentialIssue();
        demonstrateFix();
    }

    /**
     * Demonstrates how an object can disappear if the strong reference is no
     * longer used. Note: This is hard to trigger reliably without specific
     * JVM flags or heavy GC pressure, but it is a real-world bug.
     */
    static void demonstratePotentialIssue() {
        Object obj = new Object();
        WeakReference<Object> ref = new WeakReference<>(obj);

        // POTENTIAL BUG: obj is not used after this point.
        // A JIT compiler can decide obj's lifetime ends right here.
        // If GC runs now, ref.get() will be null.
        try {
            System.out.println("Hashcode: " + ref.get().hashCode());
        } catch (NullPointerException e) {
            System.out.println("Object was collected prematurely!");
        }
    }

    /**
     * Demonstrates the correct way to handle reachability using Java 9+ tools.
     */
    static void demonstrateFix() {
        Object obj = new Object();
        WeakReference<Object> ref = new WeakReference<>(obj);

        // Proper way 1: Capture in a local variable and null check
        Object strongObj = ref.get();
        if (strongObj != null) {
            System.out.println("Safe use: " + strongObj.hashCode());
        }

        // Proper way 2: Use reachabilityFence to extend the life of 'obj'
        // This guarantees 'obj' is not collected until after this call.
        Reference.reachabilityFence(obj);
        System.out.println("Reachability fence ensured obj stayed alive.");
    }
}