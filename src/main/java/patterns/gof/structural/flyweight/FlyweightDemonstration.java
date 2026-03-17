package patterns.gof.structural.flyweight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================
 * DESIGN PATTERN: Flyweight
 * CATEGORY:       Structural
 * ALSO KNOWN AS:  Cache / Pool
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Use sharing to support large numbers of fine-grained objects efficiently.
 * Solves the issue of OutOfMemory errors when millions of similar objects
 * need to be created.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Imagine a text editor representing a 100,000-character document.
 * Creating 100,000 distinct Character objects holding formatting data is
 * too heavy. Instead, create ~100 Character objects (one for each ASCII
 * letter). The position and formatting are passed in as extrinsic state.
 * <p>
 * 3. APPLICABILITY
 * Use when an application uses a massive number of objects, storage cost is
 * high, and object state can be split into Intrinsic (shared) and
 * Extrinsic (contextual) state.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Flyweight (Glyph): Interface for receiving extrinsic state.
 * - ConcreteFlyweight (Character): Stores intrinsic state.
 * - UnsharedConcreteFlyweight (Row): A composite that is not shared.
 * - FlyweightFactory: Manages the pool of shared flyweights.
 * <p>
 * 5. COLLABORATIONS
 * Client computes extrinsic state and passes it to the Flyweight via the
 * Factory.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * Pros: Saves massive amounts of RAM.
 * Cons: Increases CPU cycles slightly to compute/pass extrinsic state.
 * Code becomes more complex.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * + MODERN UPDATE: Use `ConcurrentHashMap` for thread-safe flyweight pools.
 * + Java's `Integer.valueOf()` and `String.intern()` use this exact pattern.
 */

public class FlyweightDemonstration {

    // ========================================================================
    // PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
    // ========================================================================
    // Each character stores its own font and position. Massive memory footprint.

    static class NaiveCharacter {
        private final char c;
        private final String font;
        private final int x;
        private final int y;

        public NaiveCharacter(char c, String font, int x, int y) {
            this.c = c;
            this.font = font;
            this.x = x;
            this.y = y;
        }

        public void draw() {
            System.out.println("Drawing " + c + " at " + x + "," + y + ". Using font " + font);
        }
    }

    // ========================================================================
    // PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
    // ========================================================================

    /**
     * Extrinsic State context passed to the Flyweight.
     * In modern Java, Records are perfect for encapsulating immutable extrinsic state.
     */
    public record GlyphContext(String font, int x, int y) {}

    /**
     * Flyweight Interface
     */
    interface Glyph {
        void draw(GlyphContext context);
    }

    /**
     * ConcreteFlyweight
     * Stores ONLY intrinsic state (the character code itself).
     */
    static class CharacterGlyph implements Glyph {
        private final char charCode; // Intrinsic state

        public CharacterGlyph(char charCode) {
            this.charCode = charCode;
        }

        @Override
        public void draw(GlyphContext context) {
            // Draws using both intrinsic (charCode) and extrinsic (context) state
            System.out.println("   [Render] '" + charCode + "' in " +
                    context.font() + " at (" + context.x() + "," + context.y() + ")");
        }
    }

    /**
     * FlyweightFactory
     * Ensures characters are shared and not duplicated.
     */
    static class GlyphFactory {
        private final Map<Character, Glyph> characterPool = new ConcurrentHashMap<>();

        public Glyph getCharacter(char c) {
            // Modern Java: computeIfAbsent cleanly handles the singleton pooling logic
            return characterPool.computeIfAbsent(c, CharacterGlyph::new);
        }

        public int getPoolSize() {
            return characterPool.size();
        }
    }

    // ========================================================================
    // PHASE 3: EXECUTION (MAIN METHOD)
    // ========================================================================
    public static void main(String[] args) {
        String documentText = "AABBAACCAA"; // 10 characters total, but only 3 unique (A, B, C)

        System.out.println("--- Flyweight Pattern: Naive Approach ---");
        List<NaiveCharacter> naiveDocument = new ArrayList<>();
        for (int i = 0; i < documentText.length(); i++) {
            // Instantiating a brand-new object every time!
            naiveDocument.add(new NaiveCharacter(documentText.charAt(i), "Arial", i * 10, 0));
        }
        System.out.println("Naive Approach created " + naiveDocument.size() + " distinct object instances.");

        System.out.println("\n--- Flyweight Pattern: Pattern Approach ---");
        GlyphFactory factory = new GlyphFactory();

        // Simulating the document rendering process
        for (int i = 0; i < documentText.length(); i++) {
            char c = documentText.charAt(i);

            // 1. Get shared flyweight (Intrinsic)
            Glyph flyweight = factory.getCharacter(c);

            // 2. Compute extrinsic state
            GlyphContext context = new GlyphContext("Times New Roman", i * 10, 0);

            // 3. Combine them at runtime
            flyweight.draw(context);
        }

        System.out.println("\nPattern Approach Results:");
        System.out.println("Total characters rendered: " + documentText.length());
        System.out.println("Total distinct objects in memory pool: " + factory.getPoolSize());
    }
}