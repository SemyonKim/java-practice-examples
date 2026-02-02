package generics.wildcards;

// Demonstrates "capture conversion" in Java generics.
// Capture conversion = the compiler takes an unbounded wildcard (<?>)
// and "captures" it as a fresh type variable, so it can be treated
// as an exact type parameter inside another method call.
/*
✨ Rule of Thumb for Capture Conversion:
Passing a Holder<?> into a method lets the compiler “capture” the unknown type
and treat it as a concrete type variable inside that method call — enabling safe reads, but not writes.
*/
public class CaptureConversion {

    // Method with an exact type parameter <T>.
    // It knows exactly what type Holder contains.
    static <T> void f1(Holder<T> holder) {
        // Safe: we can get() a T and use it as T.
        T t = holder.get();
        System.out.println(t.getClass().getSimpleName());
    }

    // Method with an unbounded wildcard.
    // At first glance, Holder<?> seems "unknown".
    static void f2(Holder<?> holder) {
        // 📘 Explanation in Comments:
        // - f1(Holder<T>) requires an exact type parameter.
        //
        // - f2(Holder<?>) accepts an unbounded wildcard.
        //  - Normally, <?> means “unknown type” → you can’t set() and can only get() as Object.
        //  - But when you pass it to f1(holder), the compiler *captures* the unknown type
        //  and treats it as a fresh type variable.
        //  - This is capture conversion: <?> becomes a usable type parameter inside the call.
        //
        // - Why warnings vs. no warnings?
        //  - Passing a raw type directly to f1 → warnings, because type info is erased.
        //  - Passing a raw type to f2 → no warnings, because f2 accepts <?> and capture conversion
        // recovers type info for f1.
        //
        // - Limitations
        //  - Capture conversion only works for reading (like get()).
        //  - You cannot use it for writing (set()), because <?> still means “unknown type.”
        //  - You also cannot return T from f2, because T is not known at the level of f2.
        f1(holder);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // Raw type: no generics, unsafe.
        Holder raw = new Holder<>(1);

        // Direct call to f1(raw) produces warnings,
        // because raw loses type information.
        f1(raw); // ⚠️ unchecked warnings

        // But calling f2(raw) produces no warnings,
        // because f2 takes Holder<?> and capture conversion
        // recovers the type information for f1.
        f2(raw); // ✅ no warnings

        // Another raw Holder, storing Object.
        Holder rawBasic = new Holder();
        rawBasic.set(new Object()); // ⚠️ unchecked warning
        f2(rawBasic);               // ✅ no warnings

        // Explicit wildcard Holder.
        Holder<?> wildcarded = new Holder<>(1.0);
        f2(wildcarded); // ✅ capture conversion infers Double
    }
}

/* Expected Output:
Integer   // from f1(raw)
Integer   // from f2(raw)
Object    // from f2(rawBasic)
Double    // from f2(wildcarded)
*/