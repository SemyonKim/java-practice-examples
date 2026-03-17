package patterns.gof.structural.decorator;

/**
 * ============================================================================
 * DESIGN PATTERN: Decorator
 * CATEGORY:       Structural
 * ALSO KNOWN AS:  Wrapper
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Attach additional responsibilities to an object dynamically. Decorators
 * provide a flexible alternative to subclassing for extending functionality.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Imagine buying a coffee. You start with a basic coffee (ConcreteComponent).
 * You might want to add milk (Decorator), and then sugar (another Decorator).
 * Instead of creating classes like MilkSugarCoffee, SugarCoffee, MilkCoffee,
 * you dynamically wrap the coffee object with the desired condiments at runtime.
 * <p>
 * 3. APPLICABILITY
 * Use when you need to add or remove responsibilities to objects dynamically
 * at runtime without affecting other instances. Use to prevent "class explosion"
 * caused by combining multiple optional features via inheritance.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Component (VisualComponent): The base interface.
 * - ConcreteComponent (TextView): The core object being wrapped.
 * - Decorator: Abstract class implementing the Component and holding a reference
 * to a Component instance.
 * - ConcreteDecorators (BorderDecorator, ScrollDecorator): Add specific behavior.
 * <p>
 * 5. COLLABORATIONS
 * Client calls the outermost Decorator. The Decorator executes its custom logic
 * either before or after delegating the core call to the wrapped Component.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - Pros: Extremely flexible; follows Open/Closed Principle; keeps base classes
 * lightweight.
 * - Cons: Can result in many small objects; makes object instantiation more
 * complex (often paired with Builder or Factory); object identity (==) is lost.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * Always use an interface for the Component to keep it lightweight. The abstract
 * Decorator class is useful to handle the boilerplate delegation, but can be
 * omitted if you only have one decorator type.
 */
public class DecoratorDemonstration {

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Subclassing for every possible combination leads to an exponential
     * explosion of classes (Class Explosion).
     */
    static class NaiveTextView {
        public void draw() { System.out.println("Drawing basic text."); }
    }

    static class BorderedTextView extends NaiveTextView {
        @Override public void draw() {
            super.draw();
            System.out.println(" -> Adding Border.");
        }
    }

    static class ScrolledTextView extends NaiveTextView {
        @Override public void draw() {
            super.draw();
            System.out.println(" -> Adding Scrollbars.");
        }
    }

    static class BorderedAndScrolledTextView extends NaiveTextView {
        // Redundant logic duplication
        @Override public void draw() {
            super.draw();
            System.out.println(" -> Adding Border.");
            System.out.println(" -> Adding Scrollbars.");
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * The elegant, refactored code using the GoF structure.
     */

    // 1. Component
    public interface VisualComponent {
        void draw();
    }

    // 2. ConcreteComponent
    public static class TextView implements VisualComponent {
        private final String text;

        public TextView(String text) {
            this.text = text;
        }

        @Override
        public void draw() {
            System.out.println("Drawing TextView containing: '" + text + "'");
        }
    }

    // 3. Decorator (Abstract base for wrappers)
    public abstract static class Decorator implements VisualComponent {
        // Protected visibility so concrete decorators can access if necessary,
        // though delegation is generally preferred.
        protected final VisualComponent component;

        public Decorator(VisualComponent component) {
            this.component = component;
        }

        @Override
        public void draw() {
            // Default behavior is pure delegation
            component.draw();
        }
    }

    // 4. ConcreteDecorator A
    public static class BorderDecorator extends Decorator {
        private final int borderWidth;

        public BorderDecorator(VisualComponent component, int borderWidth) {
            super(component);
            this.borderWidth = borderWidth;
        }

        @Override
        public void draw() {
            // Delegate first
            super.draw();
            // Then add extra responsibility
            drawBorder();
        }

        private void drawBorder() {
            System.out.println("  -> [Decorator] Rendering Border of width: " + borderWidth + "px");
        }
    }

    // 5. ConcreteDecorator B
    public static class ScrollDecorator extends Decorator {
        public ScrollDecorator(VisualComponent component) {
            super(component);
        }

        @Override
        public void draw() {
            // Delegate first
            super.draw();
            // Then add extra responsibility
            drawScrollBars();
        }

        private void drawScrollBars() {
            System.out.println("  -> [Decorator] Rendering Vertical & Horizontal Scrollbars");
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Decorator Pattern: Naive Approach ---");
        BorderedAndScrolledTextView naiveComponent = new BorderedAndScrolledTextView();
        naiveComponent.draw();
        System.out.println("(Notice how rigid this is: if we add a 'Shadow' feature, we need 4 new classes.)");

        System.out.println("\n--- Decorator Pattern: Pattern Approach ---");

        // 1. Base Component
        System.out.println("1. Rendering basic component:");
        VisualComponent basicText = new TextView("Hello World");
        basicText.draw();

        // 2. Wrap with a border
        System.out.println("\n2. Rendering component with a border:");
        VisualComponent borderedText = new BorderDecorator(basicText, 2);
        borderedText.draw();

        // 3. Wrap the bordered component with scrollbars (Recursive composition)
        System.out.println("\n3. Rendering component with border AND scrollbars:");
        VisualComponent fullyDecoratedText = new ScrollDecorator(
                new BorderDecorator(
                        new TextView("Design Patterns in Java"), 5
                )
        );

        // The client simply calls draw() on the outermost interface.
        fullyDecoratedText.draw();

        System.out.println("\n(We can mix and match dynamically at runtime without creating new classes!)");
    }
}