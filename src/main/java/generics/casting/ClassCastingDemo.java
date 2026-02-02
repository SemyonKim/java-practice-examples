package generics.casting;

// Example: Casting from Object to a generic List
// ----------------------------------------------
// When deserializing or reading from Object, the JVM only knows
// "this is some Object." At runtime, type parameters are erased,
// so you cannot directly recover List<Widget>.
//
// The best you can do is cast to a raw List, then assign it to
// a List<Widget> reference. This gives you compile-time type
// checking when you *use* the list, even though the cast itself
// is unchecked.
//
// ----------------------------------------------

import java.util.*;

class Widget {}

public class ClassCastingDemo {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        // Simulate reading an object (normally from a file/stream)
        Object obj = new ArrayList<Widget>();
        ((List<Widget>) obj).add(new Widget()); // unchecked cast

        // At runtime, we only know it's a List
        List rawList = (List) obj; // raw cast
        // Assign raw list to a parameterized reference
        List<Widget> widgetList = rawList; // unchecked, but allows type checking later

        // Now the compiler enforces type safety when using widgetList
        widgetList.add(new Widget()); // ✅ allowed
        // widgetList.add("Not a Widget"); // ❌ compile-time error

        System.out.println("List contains " + widgetList.size() + " Widget(s).");
    }
}

/*
📘 Explanation:
- From Object, you can only recover a raw List at runtime.
- Assigning that raw List to a List<Widget> reference is unchecked,
  but once assigned, the compiler enforces type safety for future operations.
- This helps catch mistakes (like adding the wrong type) while writing code,
  even though the cast itself cannot be verified at runtime.

✨ Rule of Thumb:
From Object you can only get a raw List; assigning it to List<T> gives you
compile-time type checking, but the cast is unchecked at runtime.
*/