package oop.decoupling.interfaceprocessor;

import oop.decoupling.filters.*;

// Read order: top -> bottom.

//Example 4 : (Adapter pattern) → Bridges existing, unmodifiable classes (Filter) into the Processor interface world.
// Problem: We discovered the Filter library (Example 2), but cannot modify it.
// Solution: Write an Adapter that takes the interface we have (Filter)
// and produces the interface we need (Processor).
class FilterAdapter implements Processor {
    Filter filter; // The adaptee: existing class we cannot change

    // Constructor accepts a Filter and wraps it
    FilterAdapter(Filter filter) {
        this.filter = filter;
    }

    // Delegation: forward name() to the wrapped Filter
    @Override
    public String name() {
        return filter.name();
    }

    // Delegation: forward process() to the wrapped Filter
    // Covariance: we can return a Waveform (more specific than Object)
    @Override
    public Waveform process(Object input) {
        return filter.process((Waveform) input);
    }
}

// Demonstration of Adapter in action
public class FilterProcessor {
    public static void main(String[] args) {
        Waveform w = new Waveform();

        // Wrap each Filter in a FilterAdapter
        // Now Applicator.apply() can reuse its logic with Filters,
        // even though Filter was not designed to be a Processor.
        Applicator.apply(new FilterAdapter(new LowPass(1.0)), w);
        Applicator.apply(new FilterAdapter(new HighPass(2.0)), w);
        Applicator.apply(new FilterAdapter(new BandPass(3.0, 4.0)), w);
    }
}