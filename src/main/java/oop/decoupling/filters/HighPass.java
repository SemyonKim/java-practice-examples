package oop.decoupling.filters;

/*
 * Read order:
 * 1. Waveform.java
 * 2. Filter.java
 * 2.1. LowPass.java
 * 2.2. HighPass.java
 * 2.3. BandPass.java
 */

// Another filter type: HighPass.
// Same interface elements as Processor, but not interchangeable.
public class HighPass extends Filter {
    double cutoff;

    public HighPass(double cutoff) {
        this.cutoff = cutoff;
    }

    @Override
    public Waveform process(Waveform input) {
        return input; // Dummy processing
    }
}
