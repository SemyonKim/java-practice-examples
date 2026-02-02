package generics.array;

class Generic<T> {}

public class ArrayOfGeneric {
    static final int SIZE = 100;
    // Declare an array of Generic<Integer>.
    // At compile time, this looks like a strongly typed array.
    static Generic<Integer>[] gia;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            // ❌ Attempt to create an Object[] and cast it to Generic<Integer>[].
            // This sets up a *facade*: the compiler believes gia is a Generic<Integer>[].
            // → At compile time, we can use type checks: add Generic<Integer> elements,
            //    and reject others (like Object or Generic<Double>).
            // But at runtime, arrays are reified — they remember their true type.
            // The JVM sees this is actually an Object[], not a Generic[].
            // So the cast itself fails immediately with ClassCastException.
            gia = (Generic<Integer>[]) new Object[SIZE];
        } catch(ClassCastException e) {
            // Prints the runtime error message:
            // "[Ljava.lang.Object; cannot be cast to [LGeneric;"
            System.out.println(e.getMessage());
        }

        // ✅ Correct approach: create an array of the erased type (Generic[]).
        // Generics lose type information at runtime (type erasure),
        // so the array is really just Generic[].
        // Then cast it to Generic<Integer>[] to satisfy the compiler.
        gia = (Generic<Integer>[]) new Generic[SIZE];

        // Prints "Generic[]" because the runtime type of the array is Generic[].
        System.out.println(gia.getClass().getSimpleName());

        // Works fine: compiler allows adding Generic<Integer>.
        gia[0] = new Generic<>();

        // Compile-time error: cannot assign Object to Generic<Integer>.
        // gia[1] = new Object();

        // Compile-time error: cannot assign Generic<Double> to Generic<Integer>.
        // gia[2] = new Generic<Double>();
    }
}

/* Output:
[Ljava.lang.Object; cannot be cast to [LGeneric;
Generic[]
*/

/*
🔎 Discovery Recap:
- "gia = (Generic<Integer>[]) new Object[SIZE];" creates a facade:
  The compiler enforces type checks as if gia were a Generic<Integer>[].
- At compile time: you can add Generic<Integer> and reject other types.
- At runtime: the JVM sees the truth — it's still an Object[].
  Arrays are reified, so the cast fails immediately.
- This shows the tension between Object’s universality (can hold anything)
  and generics’ compile-time type safety. You can’t combine both illusions.
- The only safe way: create an array of the erased type (Generic[]),
  then cast it to Generic<Integer>[].
*/