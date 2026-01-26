package oop.decoupling.filters;

/*
 * Read order:
 * 1. Waveform.java
 * 2. Filter.java
 * 2.1. LowPass.java
 * 2.2. HighPass.java
 * 2.3. BandPass.java
 *
 * Read next "Example 3" : (Processor as interface) → Loosens constraints,
 * enabling reuse and client-defined conformity, path = "oop/decoupling/interfaceprocessor/Processor.java"
 */

// BandPass filter: combines low and high cutoff frequencies.
public class BandPass extends Filter {
    double lowCutoff, highCutoff;

    public BandPass(double lowCut, double highCut) {
        lowCutoff = lowCut;
        highCutoff = highCut;
    }

    @Override
    public Waveform process(Waveform input) {
        return input;
    }
}
