package generics.creator;

// Another approach is the Template Method design pattern.
// In this example, create() is the Template Method, overridden in the subclass
// to produce an object of that type.

abstract class GenericWithCreate<T> {
    // GenericWithCreate contains the element field.
    // 'final' ensures that element is initialized once and cannot be reassigned.
    final T element;

    // The zero-argument constructor forces initialization of 'element'
    // by calling the abstract create() method.
    // This ensures that subclasses must define how the object of type T is created.
    GenericWithCreate() {
        element = create();
    }

    // Abstract method create() acts as the Template Method.
    // Subclasses must override this to specify how to construct an object of type T.
    abstract T create();
}

// A simple class X, which will be the type created by the subclass.
class X {}

// XCreator extends GenericWithCreate<X>, establishing the type parameter T as X.
// This way, creation is defined in the subclass, while the type of T is established.
class XCreator extends GenericWithCreate<X> {
    // Overriding the Template Method create() to produce an object of type X.
    @Override
    X create() {
        return new X();
    }

    // Method f() demonstrates that the element has been properly created.
    // It prints the simple name of the class of the created object.
    void f() {
        System.out.println(
                element.getClass().getSimpleName());
    }
}

public class CreatorGeneric {
    public static void main(String[] args) {
        // Instantiating XCreator triggers the GenericWithCreate constructor,
        // which calls create() and initializes 'element' with a new X.
        XCreator xc = new XCreator();

        // Calling f() prints "X", showing that the Template Method pattern
        // successfully created an object of the correct type.
        xc.f();
    }
}