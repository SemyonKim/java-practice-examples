package generics.wildcards;

/*
READ ORDER : WILDCARDS
1. UnboundedWildcards.java
2. UnboundedWildcards2.java
3. Wildcards.java (+ Holder.java)
*/

/*
Summary Rule of Thumb for Holder<T> examples:
- Raw (Holder)        → Unsafe, compiler allows but only with warnings; set() accepts anything, get() returns Object.
- Unbounded (Holder<?>) → Read-only; cannot safely set(), get() returns Object.
- Exact (Holder<T>)   → Full type safety; both set() and get() work with T.
- Extends (Holder<? extends T>) → Producer; can read values as T, cannot write.
- Super (Holder<? super T>)     → Consumer; can write values of type T, cannot read them as T (only Object).
*/
public class Wildcards {

    // Raw argument: type safety lost, compiler only warns
    static void rawArgs(Holder holder, Object arg) {
        holder.set(arg);          // ⚠️ Allowed, but unchecked warning
        Object obj = holder.get();// ✅ Allowed, but always returns Object
    }

    // Unbounded wildcard: stricter, compiler errors on unsafe set()
    static void unboundedArg(Holder<?> holder, Object arg) {
        // holder.set(arg);       // ❌ Error: cannot guarantee type
        Object obj = holder.get();// ✅ Allowed, but always returns Object
    }

    // Exact type parameter: full type safety
    static <T> T exact1(Holder<T> holder) {
        return holder.get();      // ✅ Returns T
    }

    static <T> T exact2(Holder<T> holder, T arg) {
        holder.set(arg);          // ✅ Safe: type matches
        return holder.get();      // ✅ Returns T
    }

    // Upper-bounded wildcard: can read, cannot write
    static <T> T wildSubtype(Holder<? extends T> holder, T arg) {
        // holder.set(arg);       // ❌ Error: cannot guarantee subtype
        return holder.get();      // ✅ Safe: returns T
    }

    // Lower-bounded wildcard: can write, cannot read
    static <T> void wildSupertype(Holder<? super T> holder, T arg) {
        holder.set(arg);          // ✅ Safe: accepts T
        // T t = holder.get();    // ❌ Error: only returns Object
        Object obj = holder.get();// ✅ Safe, but type info lost
    }

    public static void main(String[] args) {
        Holder raw = new Holder();              // Raw type
        Holder<Long> qualified = new Holder<>();// Exact type
        Holder<?> unbounded = new Holder<>();   // Unbounded wildcard
        Holder<? extends Long> bounded = new Holder<>(); // Upper-bounded

        Long lng = 1L;

        // Raw: warnings
        rawArgs(raw, lng);

        // Exact type: safe
        rawArgs(qualified, lng);

        // Wildcards: stricter rules
        unboundedArg(unbounded, lng);
        Long r2 = exact1(qualified);
        Long r6 = exact2(qualified, lng);
        Long r10 = wildSubtype(qualified, lng);
        Long r12 = wildSubtype(bounded, lng);
        wildSupertype(qualified, lng);
    }
}