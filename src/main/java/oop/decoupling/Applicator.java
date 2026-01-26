package oop.decoupling;

import java.util.*;

//Read order: top -> bottom.

// Read next "Example 2" : (Filter + Waveform) → Shows the problem: structurally similar types can’t be reused
// because of *strong coupling*, path = "oop/decoupling/filters/Waveform.java"

//Example 1 : (Processor as class + Applicator) → Strategy pattern, but tightly coupled.
// Step 1: Define a general-purpose "Processor" abstraction.
// This is the Strategy base type: subclasses override process() to provide behavior.
class Processor {

    // Returns the simple class name of the Processor
    public String name() {
        return getClass().getSimpleName();
    }

    // Default implementation: returns input unchanged.
    // Subclasses override this to transform input.
    public Object process(Object input) {
        return input;
    }
}

// Step 2: Define specific strategies by subclassing Processor.
class Upcase extends Processor {
    @Override // Covariant return:
    public String process(Object input) {
        return ((String) input).toUpperCase();
    }
}

class Downcase extends Processor {
    @Override
    public String process(Object input) {
        return ((String) input).toLowerCase();
    }
}

class Splitter extends Processor {
    @Override
    public String process(Object input) {
        // split() divides a String into pieces, Arrays.toString formats them
        return Arrays.toString(((String) input).split(" "));
    }
}

// Step 3: Define Applicator, which applies any Processor strategy.
// This is the Context in the Strategy pattern.
public class Applicator {

    // Fixed part of the algorithm:
    // - Announce which Processor is used
    // - Delegate transformation to the Processor
    public static void apply(Processor p, Object s) {
        System.out.println("Using Processor " + p.name());
        System.out.println(p.process(s));
    }

    public static void main(String[] args) {
        String s = "We are such stuff as dreams are made on";

        // Step 4: Apply three different strategies to the same input.
        apply(new Upcase(), s); // Strategy: convert to uppercase
        apply(new Downcase(), s); // Strategy: convert to lowercase
        apply(new Splitter(), s); // Strategy: split into words
    }
}