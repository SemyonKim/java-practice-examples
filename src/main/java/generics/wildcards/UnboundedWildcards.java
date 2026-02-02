package generics.wildcards;

import java.util.ArrayList;
import java.util.List;

/*
READ ORDER : WILDCARDS
1. UnboundedWildcards.java
2. UnboundedWildcards2.java
3. Wildcards.java (+ Holder.java)
*/

// Rule of thumb:
// The compiler accepts assignments when the declared type is generic-aware (List<?> or List<? extends Object>),
// but rejects or warns when mixing raw types (List) with parameterized types, because raw types erase type safety.
// In practice: raw List can go into anything with warnings, List<?> is always safe, and List<? extends Object> is
// equivalent to List<?> but may trigger extra warnings if given a raw input.
//
// Mnemonic rule:
// Raw List = "unsafe, warnings allowed"
// List<?> = "safe, always accepted"
// List<? extends Object> = "same as <?>, but stricter with raw inputs"
public class UnboundedWildcards {

    // Raw type: no generics at all
    static List rawList;

    // Unbounded wildcard: generic-aware, but type unknown
    static List<?> wildcardList;

    // Equivalent to List<?>, but written explicitly
    static List<? extends Object> extendsObjectList;

    /**
     * Accepts a raw List (dangerous, no type safety).
     */
    static void assignRaw(List list) {
        rawList = list;          // OK: raw to raw
        wildcardList = list;     // OK: raw can be assigned to wildcard
        // extendsObjectList = list; // ❌ compiler warning: unchecked conversion
    }

    /**
     * Accepts a List<?> (safe, but type unknown).
     */
    static void assignWildcard(List<?> list) {
        rawList = list;          // OK: wildcard to raw
        wildcardList = list;     // OK: wildcard to wildcard
        extendsObjectList = list;// OK: wildcard fits <? extends Object>
    }

    /**
     * Accepts a List<? extends Object> (same as List<?>).
     */
    static void assignExtendsObject(List<? extends Object> list) {
        rawList = list;          // OK
        wildcardList = list;     // OK
        extendsObjectList = list;// OK
    }

    public static void main(String[] args) {
        // Using raw ArrayList (no type parameter)
        assignRaw(new ArrayList());       // works, but unsafe
        assignWildcard(new ArrayList());  // works, treated as List<?>
        // assignExtendsObject(new ArrayList()); // ❌ warning: unchecked

        // Using parameterized ArrayList (safe)
        assignRaw(new ArrayList<>());       // still unsafe (raw accepted)
        assignWildcard(new ArrayList<>());  // safe
        assignExtendsObject(new ArrayList<>()); // safe

        // Explicit wildcard usage
        List<?> wildList = new ArrayList<>();
        assignRaw(wildList);
        assignWildcard(wildList);
        assignExtendsObject(wildList);
    }
}