package oop.decoupling.filters;

/*
 * Read order:
 * 1. Waveform.java
 * 2. Filter.java
 * 2.1. LowPass.java
 * 2.2. HighPass.java
 * 2.3. BandPass.java
 */

// A specific filter type: LowPass.
// It has a cutoff frequency and processes Waveforms.
public class LowPass extends Filter {
    double cutoff;

    public LowPass(double cutoff) {
        this.cutoff = cutoff;
    }

    @Override
    public Waveform process(Waveform input) {
        return input; // Dummy processing
    }
}
