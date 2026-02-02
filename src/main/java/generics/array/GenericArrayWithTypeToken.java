package generics.array;

import java.lang.reflect.Array;

/*
==========================
READ ORDER: GENERIC ARRAYS
1. ArrayOfGeneric
2. GenericArray
3. GenericArray2
4. GenericArrayWithTypeToken
==========================
*/

// Final step in handling generic arrays safely.
// For new code, we pass in a type token (Class<T>) to recover from type erasure.
// This allows us to create the actual runtime type of array we need.
public class GenericArrayWithTypeToken<T> {
    // Internal representation is now truly T[].
    private T[] array;

    @SuppressWarnings("unchecked")
    public GenericArrayWithTypeToken(Class<T> type, int sz) {
        // Use Array.newInstance() with the type token to create an array of the exact type T[].
        // This recovers from erasure, because Class<T> carries the runtime type information.
        // The cast must still be suppressed with @SuppressWarnings.
        array = (T[]) Array.newInstance(type, sz);
    }

    // Store an element of type T into the array.
    public void put(int index, T item) {
        array[index] = item;
    }

    // Retrieve an element of type T from the array.
    public T get(int index) {
        return array[index];
    }

    // Expose the underlying representation.
    // Now the runtime type of the array is truly T[].
    public T[] rep() {
        return array;
    }

    public static void main(String[] args) {
        // Create a GenericArrayWithTypeToken of Integer with size 10.
        // Pass Integer.class as the type token to recover the runtime type.
        GenericArrayWithTypeToken<Integer> gai =
                new GenericArrayWithTypeToken<>(Integer.class, 10);

        // This now works: rep() returns an Integer[] at runtime,
        // because the array was created with the correct type.
        Integer[] ia = gai.rep();
    }
}

/*
Explanation:
- The type token Class<T> is passed into the constructor to recover from erasure.
- With Array.newInstance(type, sz), we can create the actual type of array we need.
- The cast must still be suppressed with @SuppressWarnings, but now the runtime type
  of the array is the exact type T[].
- Once we get the actual type, we can safely return it and produce the desired results.
- In main(), rep() returns an Integer[] without error, because the array was created
  as Integer[] at runtime.
*/

/*
Progression Recap:
1. ArrayOfGeneric: Tried to cast Object[] to Generic<T>[], but runtime rejected the facade.
2. GenericArray: Wrapped array in a generic class, but still had facade illusion (T[] was really Object[]).
3. GenericArray2: Made runtime type explicit as Object[], casting only on retrieval. Safer, but rep() still failed.
4. GenericArrayWithTypeToken: Final solution. Pass in Class<T> type token to recover runtime type.
   Now the array is truly T[], and rep() works correctly. This is the canonical solution in Java
   for creating arrays of generic types safely.
*/