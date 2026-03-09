package puzzlers.effectivejava.ch9.item67;

import java.awt.Dimension;

/**
 * <h2>Optimize judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> Strive to write good programs rather than fast ones.
 * Performance should be a consideration during design (especially for APIs and protocols),
 * but actual optimization should only follow measurement. "Premature optimization
 * is the root of all evil."
 * </p>
 *
 * <h3>Guidelines for Optimization</h3>
 * <ul>
 * <li><b>Rule 1:</b> Don't do it.</li>
 * <li><b>Rule 2 (Experts only):</b> Don't do it yet—wait until you have a clear,
 * correct, but unoptimized solution.</li>
 * <li><b>Design for Performance:</b> While you shouldn't optimize code prematurely,
 * don't ignore performance in architecture. APIs, wire protocols, and data formats
 * are nearly impossible to change later.</li>
 * <li><b>Measure Twice, Cut Once:</b> Use profiling tools (like JMH or metal detectors
 * for your "haystack") to find the 10% of code where 90% of time is spent.</li>
 * </ul>
 *
 * <h3>Risks of Premature Optimization</h3>
 * <ul>
 * <li><b>Architectural Damage:</b> Warping an API for speed can lead to permanent
 * maintenance headaches and "computing sins."</li>
 * <li><b>Obscure Code:</b> Optimization often involves breaking abstractions,
 * making the system brittle and difficult to evolve.</li>
 * <li><b>Counter-productivity:</b> In Java’s complex VM environment, "optimized"
 * code can sometimes run slower than clean code due to JIT compiler interference.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 InformationHiding
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item18 CompositionOverInheritance
 * @see puzzlers.effectivejava.ch8.item50 DefensiveCopies
 * @see puzzlers.effectivejava.ch9.item64 InterfacesOverImplementations
 */
public class OptimizationStrategy {

    private int width = 100;
    private int height = 200;

    /**
     * POOR DESIGN: Similar to java.awt.Component.getSize().
     * Because Dimension is mutable, the implementation MUST allocate a new
     * instance every time to prevent the caller from modifying internal state.
     */
    public Dimension getSizePoorly() {
        return new Dimension(width, height);
    }

    /**
     * BETTER DESIGN: Return primitive values or use an immutable object.
     * This avoids unnecessary object allocation on every call.
     */
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * REASONING:
     * Good programs embody information hiding. By localizing design decisions,
     * you can change an implementation (e.g., switching an algorithm) without
     * affecting the rest of the system.
     */
    public void executeTask() {
        // Step 1: Write a clear, well-structured algorithm.
        // Step 2: Measure performance with a profiler or JMH.
        // Step 3: Only if it's too slow, optimize the specific bottleneck.
        System.out.println("Executing task with clean architecture...");
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        OptimizationStrategy strategy = new OptimizationStrategy();

        System.out.println("--- Performance Pitfall Example ---");
        // Calling this millions of times creates millions of Dimension objects
        Dimension d = strategy.getSizePoorly();
        System.out.println("Current Size: " + d.width + "x" + d.height);

        System.out.println("\n--- Better Approach ---");
        // No object allocation needed here
        System.out.println("Width: " + strategy.getWidth());
        System.out.println("Height: " + strategy.getHeight());

        System.out.println("\nRemember: Measure with tools like JMH before making changes!");
    }
}