package oop.decoupling.filters;

/*
 * Read order:
 * 1. Waveform.java
 * 2. Filter.java
 * 2.1. LowPass.java
 * 2.2. HighPass.java
 * 2.3. BandPass.java
 */

//Example 2 : (Filter + Waveform) → Shows the problem: structurally similar types can’t be reused
// because of coupling.
// Waveform represents audio data. Each instance has a unique ID.
public class Waveform {
    private static long counter;
    private final long id = counter++;

    @Override
    public String toString() {
        return "Waveform " + id;
    }
}
