package generics.interfaces;

public class MultipleInterfaceVariants {
}

// Example: Implementing Parameterized Interfaces and Type Erasure
// ---------------------------------------------------------------
// This example demonstrates why a class cannot implement two different
// parameterizations of the same generic interface in Java.
//
// The issue comes from *type erasure*. At compile time, Java removes
// generic type parameters, so Payable<Employee> and Payable<Hourly>
// both erase to just Payable. That means the compiler sees the same
// interface implemented twice, which is illegal.
// ---------------------------------------------------------------

// A simple generic interface
interface Payable<T> {}

// Employee implements Payable<Employee>
class Employee implements Payable<Employee> {}

// Hourly tries to implement Payable<Hourly> as well,
// but this will NOT compile because of erasure.
// After erasure, both Payable<Employee> and Payable<Hourly>
// reduce to the same raw interface Payable.
// The compiler interprets this as:
//      class Hourly extends Employee implements Payable, Payable {}
// Which is nonsense — you cannot implement the same interface twice.
//class Hourly extends Employee implements Payable<Hourly> {} // ❌ WillNotCompile

/*
---------------------------------------------------------------
📘 Explanation:

- Generics in Java are implemented via *type erasure*.
- Erasure removes type parameters at compile time.
- So Payable<Employee> → Payable
  and Payable<Hourly> → Payable
- The compiler sees Hourly as implementing Payable twice
  (class Hourly extends Employee implements Payable, Payable {}), which is not allowed.

This limitation shows up with fundamental interfaces like Comparable<T>:

    class MyClass implements Comparable<MyClass>, Comparable<String> {}

This won’t compile, because after erasure both are just Comparable.

---------------------------------------------------------------
✨ Rule of Thumb:

A class cannot implement two different parameterizations of the same
generic interface, because type erasure reduces them to the same raw interface.

---------------------------------------------------------------
✅ Workarounds:

- Implement only one variant of the interface.
- Use composition instead of inheritance if you need multiple behaviors.
- Redesign the interface with bounded type parameters to avoid clashes.

---------------------------------------------------------------
*/