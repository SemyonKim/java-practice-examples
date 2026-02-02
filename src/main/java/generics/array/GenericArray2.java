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

// Next step in exploring generic arrays.
// Because of the problems we saw with GenericArray (T[] facade over Object[]),
// it’s better to use an Object[] internally and cast to T only when retrieving.
// This makes the runtime type explicit and reduces the chance of forgetting
// that the underlying array is really Object[].
public class GenericArray2<T> {
    // Internal representation is now Object[] instead of T[].
    // This avoids the illusion that the array is truly of type T[].
    private Object[] array;

    public GenericArray2(int sz) {
        // Create a plain Object[] of the given size.
        array = new Object[sz];
    }

    // Store an element of type T into the Object[].
    public void put(int index, T item) {
        array[index] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        // Cast the Object back to T when retrieving.
        // This cast is safe because we only ever put T into the array.
        return (T) array[index];
    }

    @SuppressWarnings("unchecked")
    public T[] rep() {
        // Attempt to expose the underlying array as T[].
        // This is still an unchecked cast, and incorrect at runtime,
        // because the actual array type is Object[].
        return (T[]) array;
    }

    public static void main(String[] args) {
        // Create a GenericArray2 of Integer with size 10.
        GenericArray2<Integer> gai = new GenericArray2<>(10);

        // Fill the array with integers 0–9.
        for(int i = 0; i < 10; i++)
            gai.put(i, i);

        // Retrieve and print the values using get().
        // This works fine: get() casts Object to T (Integer),
        // which is correct because we only stored Integers.
        for(int i = 0; i < 10; i++)
            System.out.print(gai.get(i) + " ");
        System.out.println();

        try {
            // Attempt to capture the underlying array as Integer[].
            // At compile time, this looks correct (rep() returns T[]).
            // At runtime, the array is still Object[], so the cast fails.
            Integer[] ia = gai.rep();
        } catch(Exception e) {
            // Prints the runtime error message:
            // "java.lang.ClassCastException: [Ljava.lang.Object;
            //  cannot be cast to [Ljava.lang.Integer;"
            System.out.println(e);
        }
    }
}

/* Output:
0 1 2 3 4 5 6 7 8 9
java.lang.ClassCastException: [Ljava.lang.Object;
cannot be cast to [Ljava.lang.Integer;
*/

/*
Explanation:
- Initially, this doesn’t look very different from GenericArray, just that the cast moved.
- Without @SuppressWarnings, you still get “unchecked” warnings.
- Internal representation is now Object[] rather than T[].
- When get() is called, it casts the Object to T, which is the correct type, so that is safe.
- However, rep() tries to cast Object[] to T[], which is still incorrect,
  producing a warning at compile time and an exception at runtime.
- Thus, there’s no way to subvert the type of the underlying array — it can only be Object[].
- The advantage of treating the array internally as Object[] instead of T[]
  is that it’s less likely you’ll forget the runtime type and accidentally introduce a bug.
- This step follows the previous example: after seeing the facade illusion with T[],
  we now make the runtime type explicit (Object[]) and only cast when retrieving,
  which makes the design safer and clearer.

  ---

Why we did this step
- In GenericArray, the internal array was declared as T[], but really was an Object[] cast to T[].
  This created a facade illusion that confused compile-time vs runtime types.
- In GenericArray2, we make the runtime type explicit: the array is stored as Object[].
- This way, every retrieval (get()) requires a cast to T, which is safe because we only store T.
- The design is clearer: you can’t forget that the underlying array is really Object[].
*/
