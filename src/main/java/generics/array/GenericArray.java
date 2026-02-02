package generics.array;

/*
==========================
READ ORDER: GENERIC ARRAYS
1. ArrayOfGeneric
2. GenericArray
3. GenericArray2
4. GenericArrayWithTypeToken
==========================
*/

// Let’s look at a slightly more sophisticated example.
// This builds on the previous ArrayOfGeneric case.
// Now we wrap the array inside a generic class to manage it more cleanly.
public class GenericArray<T> {
    // Private array of type T.
    // We cannot directly say "new T[sz]" because Java forbids creation
    // of arrays of parameterized types.
    private T[] array;

    @SuppressWarnings("unchecked")
    public GenericArray(int sz) {
        // As before, we create an Object[] and cast it to T[].
        // This cast is unchecked, but necessary due to type erasure.
        array = (T[]) new Object[sz];
    }

    // Put an item of type T into the array at the given index.
    public void put(int index, T item) {
        array[index] = item;
    }

    // Get an item of type T from the array.
    public T get(int index) {
        return array[index];
    }

    // Method that exposes the underlying representation.
    // It returns a T[], but at runtime this is actually an Object[].
    public T[] rep() {
        return array;
    }

    public static void main(String[] args) {
        // Create a GenericArray of Integer with size 10.
        GenericArray<Integer> gai = new GenericArray<>(10);

        try {
            // Attempt to capture the result of rep() as an Integer[].
            // At compile time, this looks correct: rep() returns T[], and T is Integer.
            // But at runtime, the actual type is Object[], not Integer[].
            // So this cast fails with ClassCastException.
            Integer[] ia = gai.rep();
        } catch(ClassCastException e) {
            // Prints the runtime error message:
            // "[Ljava.lang.Object; cannot be cast to [Ljava.lang.Integer;"
            System.out.println(e.getMessage());
        }

        // This is OK: capturing the result as Object[] works fine,
        // because the runtime type of the array is indeed Object[].
        Object[] oa = gai.rep();
    }
}

/* Output:
[Ljava.lang.Object; cannot be cast to
[Ljava.lang.Integer;
*/

/*
Explanation:
- As before, we cannot say "T[] array = new T[sz]" because Java forbids arrays of parameterized types.
- Instead, we create an Object[] and cast it to T[].
- The rep() method returns T[], which the compiler interprets as Integer[] for gai.
- However, at runtime, the array is still an Object[], so trying to capture it as Integer[] fails.
- This step is a logical continuation of the previous ArrayOfGeneric example:
  Previously, we saw how arrays of generics cause problems with type reification.
  Here, we encapsulate the array inside a generic class to manage it,
  but the same runtime mismatch between compile-time facade and runtime reality remains.

  ---

Why this step was taken
- In the previous example, we directly experimented with arrays of generics (Generic<Integer>[]).
- In this next step, we encapsulate the array inside a generic class (GenericArray<T>).
- This makes the design more reusable and object-oriented, but it still demonstrates the same fundamental
  issue:
    Generics are erased at runtime, while arrays enforce their actual type.
*/
