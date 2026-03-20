package patterns.gof.behavioral.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ============================================================================
 * DESIGN PATTERN: Strategy
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Policy
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Define a family of algorithms, encapsulate each one, and make them
 * interchangeable. Strategy lets the algorithm vary independently from clients
 * that use it. It addresses the problem of hard-coded, inflexible logic and
 * massive conditional statements.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A text formatting engine needs to break text into lines. Depending on the
 * context, it might need a simple ragged-right algorithm or an advanced
 * TeX-style justified algorithm.
 * + Analogy: Traveling to the airport. The Context is your journey. The Strategy
 * is the mode of transport: taking a Taxi, driving your own Car, or using
 * public Transit. The choice of strategy can change dynamically, but the goal
 * (getting to the airport) remains the same.
 * <p>
 * 3. APPLICABILITY
 * Use when you have many related classes differing only in behavior, or when
 * you need to isolate complex algorithmic logic (and its data structures) from
 * the classes that use it. It's the primary refactoring tool to replace 'if-else'
 * behavior selection.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Strategy (Compositor): The abstract interface defining the algorithm.
 * - ConcreteStrategy (SimpleCompositor, TeXCompositor): Specific implementations.
 * - Context (Composition): Maintains a reference to a Strategy and delegates
 * the work to it.
 * <p>
 * 5. COLLABORATIONS
 * The Context receives requests from the client and forwards them to the
 * Strategy. It may pass data to the Strategy or pass a reference to itself.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Open/Closed Principle: Introduce new strategies without changing context.
 * + Isolates algorithmic details.
 * + Replaces inheritance with composition.
 * - Clients must understand the differences between strategies to pick one.
 * - Communication overhead between Context and Strategy.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * + MODERN UPDATE: Java 8 Lambdas and Method References effectively turn
 * any method matching a Functional Interface signature into a Concrete Strategy
 * without the need for boilerplate class declarations.
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - java.util.Comparator (Sorting strategies).
 * - java.util.concurrent.ThreadPoolExecutor.AbortPolicy (Rejection strategies).
 * - Spring's PlatformTransactionManager.
 * <p>
 * 9. RELATED PATTERNS
 * - Flyweight: Strategies with no internal state can be shared.
 * - State: Similar structure, but State alters the core behavior of the object
 * dynamically based on internal transitions, while Strategy is usually set
 * by the client to perform a specific task.
 * ============================================================================
 */
public class StrategyDemonstration {

    // ========================================================================
    // MOCKED ENTITIES
    // ========================================================================

    // Simulates a visual component to be formatted
    static class Component {
        String text;
        public Component(String text) { this.text = text; }
        @Override public String toString() { return text; }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * A monolithic class that uses an enum flag and a giant switch statement
     * to determine which algorithm to run. Adding a new algorithm requires
     * modifying this class, violating the Open/Closed Principle.
     */
    static class NaiveComposition {
        enum FormatType { SIMPLE, TEX, ARRAY }
        private List<Component> components = new ArrayList<>();

        public void addComponent(Component c) { components.add(c); }

        public void repair(FormatType type) {
            System.out.println("Running naive repair...");
            switch (type) {
                case SIMPLE:
                    System.out.println("  -> Formatting using simple ragged-right logic.");
                    break;
                case TEX:
                    System.out.println("  -> Formatting using complex TeX optimal paragraph logic.");
                    break;
                case ARRAY:
                    System.out.println("  -> Formatting using fixed array alignment logic.");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown format type");
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * The algorithm is extracted into the Strategy interface and its
     * concrete implementations.
     */

    /**
     * 1. Strategy Interface
     */
    interface Compositor {
        void compose(List<Component> content);
    }

    /**
     * 2. Concrete Strategy A
     */
    static class SimpleCompositor implements Compositor {
        @Override
        public void compose(List<Component> content) {
            System.out.println("  [SimpleCompositor] Breaking text with simple ragged-right logic.");
        }
    }

    /**
     * 2. Concrete Strategy B
     */
    static class TeXCompositor implements Compositor {
        @Override
        public void compose(List<Component> content) {
            System.out.println("  [TeXCompositor] Breaking text using TeX optimal paragraph logic.");
        }
    }

    /**
     * 3. Context
     */
    static class Composition {
        private Compositor compositor;
        private List<Component> components = new ArrayList<>();

        // Context is configured with a strategy
        public Composition(Compositor compositor) {
            this.compositor = compositor;
        }

        // Allows changing strategy at runtime
        public void setCompositor(Compositor compositor) {
            this.compositor = compositor;
        }

        public void addComponent(Component c) {
            components.add(c);
        }

        public void repair() {
            System.out.println("Context initiating repair...");
            // Context delegates to the Strategy
            compositor.compose(components);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: MODERN JAVA APPROACH (FUNCTIONAL STRATEGY)
     * ========================================================================
     * Using Java's built-in Functional Interfaces (like Function or Consumer)
     * to eliminate the need for boilerplate Concrete Strategy classes entirely.
     */
    static class ModernComposition {
        private List<Component> components = new ArrayList<>();

        public void addComponent(Component c) { components.add(c); }

        // The strategy is passed dynamically as a lambda or method reference
        public void repair(Consumer<List<Component>> strategy) {
            System.out.println("Modern Context initiating functional repair...");
            strategy.accept(components);
        }
    }

    /**
     * ========================================================================
     * PHASE 4: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Strategy Pattern: Naive Approach ---");
        NaiveComposition naive = new NaiveComposition();
        naive.addComponent(new Component("Hello World"));
        naive.repair(NaiveComposition.FormatType.TEX);

        System.out.println("\n--- Strategy Pattern: GoF Object-Oriented Approach ---");
        // 1. Client instantiates the specific strategy
        Compositor texStrategy = new TeXCompositor();

        // 2. Client passes the strategy to the Context
        Composition composition = new Composition(texStrategy);
        composition.addComponent(new Component("Design Patterns"));

        // 3. Context executes
        composition.repair();

        // 4. Client changes strategy dynamically
        composition.setCompositor(new SimpleCompositor());
        composition.repair();

        System.out.println("\n--- Strategy Pattern: Modern Java Functional Approach ---");
        ModernComposition modern = new ModernComposition();
        modern.addComponent(new Component("Functional Programming"));

        // Passing the algorithm directly as a lambda expression
        modern.repair(_ -> System.out.println("  [Lambda Strategy] Reversing text logic applied."));

        // Passing another algorithm dynamically
        modern.repair(_ -> System.out.println("  [Lambda Strategy] Converting to uppercase alignment applied."));
    }
}