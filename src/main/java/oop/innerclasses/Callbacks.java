package oop.innerclasses;

// The Incrementable interface defines a contract with a single method.
// It will be implemented either directly in an outer class or indirectly via an inner class.
interface Incrementable {
    void increment();
}

// Very simple case: implementing the interface directly in an outer class.
// Callee1 is the simpler solution in terms of code.
class Callee1 implements Incrementable {
    private int i = 0;

    @Override
    public void increment() {
        i++;
        System.out.println(i);
    }
}

// MyIncrement has its own increment() method that does something unrelated
// to the one expected by the Incrementable interface.
class MyIncrement {
    public void increment() {
        System.out.println("Other operation");
    }

    static void f(MyIncrement mi) {
        mi.increment();
    }
}

// Callee2 inherits from MyIncrement, which already has a different increment().
// Because of this, increment() cannot be overridden for use by Incrementable.
// Therefore, a separate implementation using an inner class is required.
class Callee2 extends MyIncrement {
    private int i = 0;

    @Override
    public void increment() {
        // Calls the inherited increment() from MyIncrement.
        super.increment();
        i++;
        System.out.println(i);
    }

    // Inner class Closure provides a safe hook back into Callee2.
    // It implements Incrementable, but only exposes increment().
    // Whoever gets the Incrementable reference can only call increment(),
    // without access to other members of Callee2.
    private class Closure implements Incrementable {
        @Override
        public void increment() {
            // Must specify the outer-class method explicitly,
            // otherwise infinite recursion would occur.
            Callee2.this.increment();
        }
    }

    // Provides a connection to the outside world.
    // Everything else in Callee2 is private, so this method is essential.
    Incrementable getCallbackReference() {
        return new Closure();
    }
}

// Caller takes an Incrementable reference in its constructor.
// Later, it uses this reference to "call back" into the Callee class.
// This demonstrates how interfaces allow separation of interface from implementation.
class Caller {
    private Incrementable callbackReference;

    Caller(Incrementable cbh) {
        callbackReference = cbh;
    }

    void go() {
        callbackReference.increment();
    }
}

// Demonstration of the distinction between implementing an interface
// in an outer class (Callee1) versus in an inner class (Callee2).
// The value of the callback is in its flexibility: methods can be
// dynamically decided at runtime. In user interfaces, callbacks are
// widely used to implement GUI functionality.
public class Callbacks {
    public static void main(String[] args) {
        Callee1 c1 = new Callee1();
        Callee2 c2 = new Callee2();

        // Calls MyIncrement's version of increment().
        MyIncrement.f(c2);

        // Caller using Callee1's direct implementation.
        Caller caller1 = new Caller(c1);

        // Caller using Callee2's inner class callback reference.
        Caller caller2 = new Caller(c2.getCallbackReference());

        // Demonstrates callback flexibility.
        caller1.go();
        caller1.go();
        caller2.go();
        caller2.go();
    }
}