package oop.decoupling.filters;

/*
 * Read order:
 * 1. Waveform.java
 * 2. Filter.java
 * 2.1. LowPass.java
 * 2.2. HighPass.java
 * 2.3. BandPass.java
 */

// Filter looks structurally similar to Processor:
// - name() identifies the filter
// - process() transforms input into output
// BUT: Filter does not inherit from Processor.
// This means Applicator.apply() cannot accept a Filter,
// even though conceptually it could work the same way.
// This is the problem of *strong coupling*.
// Notice: inputs and outputs here are Waveforms, not Object.
public class Filter {
    public String name() {
        return getClass().getSimpleName();
    }

    public Waveform process(Waveform input) {
        return input;
    }
}