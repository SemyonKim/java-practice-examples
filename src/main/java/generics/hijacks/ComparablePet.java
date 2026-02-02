package generics.hijacks;

// Example: Base Class Hijacks an Interface
// ----------------------------------------
// This example demonstrates a subtle limitation in Java generics:
// Once a base class implements a parameterized interface, subclasses
// cannot change the type parameter of that interface.
//
// The issue comes from type erasure and interface inheritance rules.
// If the base class fixes Comparable<T> to a specific type parameter,
// subclasses are forced to use the same parameterization.
//
// ----------------------------------------

// Base class ComparablePet implements Comparable<ComparablePet>
public class ComparablePet implements Comparable<ComparablePet> {
    @Override
    public int compareTo(ComparablePet arg) {
        return 0;
    }
}

// Attempt to narrow the comparison type for a subclass.
// Cat tries to be Comparable<Cat>, but this will NOT compile.
// Error: Comparable cannot be inherited with different arguments
// (<Cat> vs <ComparablePet>).
// ----------------------------------------
// class Cat extends ComparablePet implements Comparable<Cat> {
//     @Override
//     public int compareTo(Cat arg) { return 0; }
// }
// ❌ WillNotCompile

// Once ComparablePet establishes Comparable<ComparablePet>,
// subclasses can only ever be compared to ComparablePet.
// They cannot redefine Comparable with a different type parameter.

// Hamster shows that you can reimplement Comparable,
// but only with the exact same parameterization as the base class.
class Hamster extends ComparablePet implements Comparable<ComparablePet> {
    @Override
    public int compareTo(ComparablePet arg) {
        return 0;
    }
}

// Gecko shows that this is effectively the same as just overriding
// the compareTo() method from the base class.
class Gecko extends ComparablePet {
    @Override
    public int compareTo(ComparablePet arg) {
        return 0;
    }
}

/*
---------------------------------------------------------------
📘 Explanation:

- ComparablePet fixes the Comparable interface to Comparable<ComparablePet>.
- Subclasses cannot change the type parameter (e.g., Cat cannot be Comparable<Cat>).
- This restriction exists because of type erasure: Comparable<Cat> and
  Comparable<ComparablePet> both erase to Comparable, so the compiler
  sees a conflict.
- Hamster demonstrates that you can reimplement Comparable, but only
  with the same parameterization as the base class.
- Gecko shows that this is equivalent to simply overriding the method
  in the base class.

---------------------------------------------------------------
✨ Rule of Thumb:

Once a base class implements a parameterized interface with a specific
type argument, subclasses cannot hijack or redefine that interface with
a different type parameter.

---------------------------------------------------------------
*/