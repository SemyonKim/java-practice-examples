package generics.wildcards;

import java.util.HashMap;
import java.util.Map;

/*
READ ORDER : WILDCARDS
1. UnboundedWildcards.java
2. UnboundedWildcards2.java
3. Wildcards.java (+ Holder.java)
*/

// Rule of thumb:
// Raw Map = "unsafe, warnings allowed"
// Map<?,?> = "safe, both key and value unknown"
// Map<String,?> = "safe, key fixed as String, value unknown"
public class UnboundedWildcards2 {

    // Raw type: no generics
    static Map rawMap;

    // Both key and value are unknown types
    static Map<?, ?> wildcardMap;

    // Key must be String, value can be any type
    static Map<String, ?> stringKeyMap;

    // Accepts raw Map (unsafe)
    static void assignRaw(Map map) {
        rawMap = map;          // OK
        wildcardMap = map;     // OK
        //stringKeyMap = map; // ❌ warning: unchecked conversion
    }

    // Accepts Map<?,?>
    static void assignWildcard(Map<?, ?> map) {
        rawMap = map;          // OK
        wildcardMap = map;     // OK
        //stringKeyMap = map;  // ❌ Not allowed: compiler cannot guarantee that ? is String
                               // Even if the runtime map has String keys, the type system doesn’t know it.
    }

    // Accepts Map<String,?>
    static void assignStringKey(Map<String, ?> map) {
        rawMap = map;          // OK
        wildcardMap = map;     // OK
        stringKeyMap = map;    // OK
    }

    public static void main(String[] args) {
        // Raw HashMap (no type parameters)
        assignRaw(new HashMap());       // works, but unsafe
        assignWildcard(new HashMap());  // works, treated as Map<?,?>
        // assignStringKey(new HashMap()); // ❌ warning: unchecked

        // Safe, parameterized HashMap
        assignRaw(new HashMap<>());       // still unsafe (raw accepted)
        assignWildcard(new HashMap<>());  // safe
        assignStringKey(new HashMap<>()); // safe
    }
}