package oop.decoupling.interfaceprocessor;

/*
* Read order:
* 1. Processor.java
* 2. Applicator.java
* 3. StringProcessor.java
*/

//Example 3 (Processor as interface) → Loosens constraints, enabling reuse and client-defined conformity.
// Refactor Processor into an interface.
// This loosens constraints compared to "Example 1" (class-based Processor).
// Now, any class can conform to Processor without being forced into inheritance.
public interface Processor {

    // Default method provides a name() implementation.
    // This keeps convenience from the original class version.
    default String name() {
        return getClass().getSimpleName();
    }

    // Core contract: process() must be implemented by conforming classes.
    Object process(Object input);
}
