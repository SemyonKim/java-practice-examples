package oop.decoupling.interfaceprocessor;

/*
 * Read order:
 * 1. Processor.java
 * 2. Applicator.java
 * 3. StringProcessor.java
 */

// Applicator remains the same, but now it accepts any Processor interface.
// This means client programmers can reuse Applicator.apply()
// with their own Processor implementations (not just subclasses).
public class Applicator {
    public static void apply(Processor p, Object s) {
        System.out.println("Using Processor " + p.name());
        System.out.println(p.process(s));
    }
}
