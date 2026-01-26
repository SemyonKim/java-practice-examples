package oop.decoupling.interfaceprocessor;

import java.util.*;

/*
 * Read order:
 * 1. Processor.java
 * 2. Applicator.java
 * 3. StringProcessor.java
 *
 * Read next final "Example 4" : (Adapter pattern) → Bridges existing, unmodifiable classes (Filter) into
 * the Processor interface world, path = "oop/decoupling/interfaceprocessor/FilterProcessor.java"
 */

// Client programmers can now write their own classes
// to conform to the Processor interface.
// StringProcessor refines Processor by promising a String return type.
interface StringProcessor extends Processor {
    @Override
    String process(Object input); // Covariant return: String instead of Object

    // S is automatically static and final because it's defined inside an interface.
    String S = "If she weighs the same as a duck, she's made of wood";

    // You can even define a main() inside an interface.
    // Demonstrates reuse of Applicator.apply() with custom strategies.
    static void main(String[] args) {
        Applicator.apply(new Upcase(), S);
        Applicator.apply(new Downcase(), S);
        Applicator.apply(new Splitter(), S);
    }
}

// Strategy implementations now conform to StringProcessor (which itself extends Processor).
class Upcase implements StringProcessor {
    @Override // Covariant return:
    public String process(Object input) {
        return ((String) input).toUpperCase();
    }
}

class Downcase implements StringProcessor {
    @Override
    public String process(Object input) {
        return ((String) input).toLowerCase();
    }
}

class Splitter implements StringProcessor {
    @Override
    public String process(Object input) {
        return Arrays.toString(((String) input).split(" "));
    }
}
