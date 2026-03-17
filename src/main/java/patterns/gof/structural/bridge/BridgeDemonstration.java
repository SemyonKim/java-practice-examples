package patterns.gof.structural.bridge;

/**
 * ============================================================================
 * DESIGN PATTERN: Bridge
 * CATEGORY:       Structural
 * ALSO KNOWN AS:  Handle/Body
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Decouple an abstraction from its implementation so that the two can vary
 * independently. It resolves the issue of combinatorial explosions in class
 * hierarchies caused by permanent bindings between abstractions and implementations.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Consider a UI toolkit. If you subclass a Window for different types
 * (IconWindow, TransientWindow) and also subclass for different platforms
 * (Windows, Linux, macOS), you end up with MacIconWindow, LinuxIconWindow,
 * MacTransientWindow, etc.
 * The Bridge pattern splits this into two hierarchies: Window Types (Abstraction)
 * and Window Implementations (Implementor). The Window holds a reference to an
 * Implementor, delegating the low-level rendering to it.
 * <p>
 * 3. APPLICABILITY
 * Use when you want to avoid permanent bindings between an abstraction and
 * its implementation (e.g., swapping implementations at runtime). Use when
 * both abstractions and implementations should be extensible independently
 * without causing a nested class hierarchy explosion.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Abstraction (Window): Defines high-level operations and holds an Implementor.
 * - RefinedAbstraction (IconWindow): Extends Abstraction, adding specific features.
 * - Implementor (WindowImp): Defines the interface for low-level primitive operations.
 * - ConcreteImplementor (XWindowImp): Platform-specific low-level implementations.
 * <p>
 * 5. COLLABORATIONS
 * The Client calls operations on the Abstraction. The Abstraction translates
 * these high-level calls into one or more primitive calls on the Implementor.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * Decouples interface and implementation. Improves extensibility by allowing
 * independent hierarchies. Hides implementation details. The trade-off is a
 * slight increase in complexity due to the indirection layer.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * In modern Java, Implementor is usually an `interface`. Passing the Implementor
 * into the Abstraction is perfectly suited for constructor-based Dependency Injection
 * (e.g., via Spring or standard Java).
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - JDBC: `java.sql.Driver` and database specific drivers.
 * - SLF4J: `org.slf4j.Logger` (Abstraction) and Logback/Log4j implementations.
 * <p>
 * 9. RELATED PATTERNS
 * - Abstract Factory: Often used to create and configure the proper Bridge elements.
 * - Adapter: Adapter changes the interface of an existing object, while Bridge
 * is used up-front to keep abstractions and implementations separate.
 */
public class BridgeDemonstration {

    // ========================================================================
    // MOCKED ENTITIES
    // ========================================================================

    /** Represents an X-Y coordinate. */
    record Point(int x, int y) {}

    /** Represents the content view of a window. */
    static class View {
        public void drawOn(Window window) {
            System.out.println("View: Requesting window to draw contents.");
        }
    }

    /** Mock abstract factory to supply Implementors based on platform. */
    static class WindowSystemFactory {
        private static final WindowSystemFactory instance = new WindowSystemFactory();
        public static WindowSystemFactory getInstance() { return instance; }

        public WindowImp makeWindowImp() {
            // In reality, this would read system properties to return XWindowImp or PMWindowImp
            return new XWindowImp();
        }
    }

    // ========================================================================
    // PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
    // ========================================================================

    /**
     * The naive approach uses static inheritance, creating a combinatorial explosion
     * if we add new platforms (e.g., Windows, Mac) or new Window types.
     */
    abstract static class NaiveWindow {
        abstract void drawRect(Point p1, Point p2);
    }

    static class XWindow extends NaiveWindow {
        @Override void drawRect(Point p1, Point p2) { System.out.println("X11 Naive Drawing"); }
    }

    static class PMWindow extends NaiveWindow {
        @Override void drawRect(Point p1, Point p2) { System.out.println("PM Naive Drawing"); }
    }

    // To support an IconWindow on both platforms, we MUST create two more classes:
    static class XIconWindow extends XWindow { /* X11 specific icon drawing */ }
    static class PMIconWindow extends PMWindow { /* PM specific icon drawing */ }

    // ========================================================================
    // PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
    // ========================================================================

    // 1. The Implementor Hierarchy

    /**
     * Implementor: Defines the interface for implementation classes.
     * Provides primitive operations.
     */
    interface WindowImp {
        void deviceRect(int x0, int y0, int x1, int y1);
        void deviceBitmap(String bitmapName, int x, int y);
    }

    /**
     * ConcreteImplementor 1: X Window System implementation.
     */
    static class XWindowImp implements WindowImp {
        @Override
        public void deviceRect(int x0, int y0, int x1, int y1) {
            System.out.printf("XWindowImp: XDrawRectangle at (%d,%d) to (%d,%d)%n", x0, y0, x1, y1);
        }

        @Override
        public void deviceBitmap(String bitmapName, int x, int y) {
            System.out.printf("XWindowImp: Drawing bitmap '%s' at (%d,%d)%n", bitmapName, x, y);
        }
    }

    /**
     * ConcreteImplementor 2: Presentation Manager implementation.
     */
    static class PMWindowImp implements WindowImp {
        @Override
        public void deviceRect(int x0, int y0, int x1, int y1) {
            System.out.printf("PMWindowImp: GpiPolyLine for rect at (%d,%d) to (%d,%d)%n", x0, y0, x1, y1);
        }

        @Override
        public void deviceBitmap(String bitmapName, int x, int y) {
            System.out.printf("PMWindowImp: Drawing bitmap '%s' via PM primitives%n", bitmapName);
        }
    }

    // 2. The Abstraction Hierarchy

    /**
     * Abstraction: Defines the abstraction's interface and maintains a reference
     * to the Implementor.
     */
    abstract static class Window {
        private WindowImp imp;
        private final View contents;

        public Window(View contents) {
            this.contents = contents;
        }

        // Lazy initialization using a Factory, as suggested in the text.
        // In modern Java, we'd more likely pass the WindowImp via the constructor (DI).
        protected WindowImp getWindowImp() {
            if (imp == null) {
                imp = WindowSystemFactory.getInstance().makeWindowImp();
            }
            return imp;
        }

        protected View getView() {
            return contents;
        }

        public abstract void drawContents();

        // High-level operation that delegates to primitive Implementor operations
        public void drawRect(Point p1, Point p2) {
            WindowImp impCurrent = getWindowImp();
            impCurrent.deviceRect(p1.x(), p1.y(), p2.x(), p2.y());
        }
    }

    /**
     * RefinedAbstraction 1: Application Window
     */
    static class ApplicationWindow extends Window {
        public ApplicationWindow(View contents) {
            super(contents);
        }

        @Override
        public void drawContents() {
            getView().drawOn(this);
            System.out.println("ApplicationWindow: Drawing border rect...");
            drawRect(new Point(0, 0), new Point(100, 100));
        }
    }

    /**
     * RefinedAbstraction 2: Icon Window
     */
    static class IconWindow extends Window {
        private final String bitmapName;

        public IconWindow(View contents, String bitmapName) {
            super(contents);
            this.bitmapName = bitmapName;
        }

        @Override
        public void drawContents() {
            WindowImp imp = getWindowImp();
            if (imp != null) {
                imp.deviceBitmap(bitmapName, 0, 0);
            }
        }
    }

    // ========================================================================
    // PHASE 3: EXECUTION (MAIN METHOD)
    // ========================================================================

    public static void main(String[] args) {
        System.out.println("--- Bridge Pattern: Naive Approach ---");
        NaiveWindow naiveWin = new XIconWindow();
        naiveWin.drawRect(new Point(0,0), new Point(10,10));
        System.out.println("Notice how a new subclass is needed for every platform/type combination.");

        System.out.println("\n--- Bridge Pattern: Pattern Approach ---");
        View defaultView = new View();

        // The factory defaults to returning an XWindowImp here.
        System.out.println("Rendering an ApplicationWindow:");
        Window appWindow = new ApplicationWindow(defaultView);
        appWindow.drawContents();

        System.out.println("\nRendering an IconWindow:");
        Window iconWindow = new IconWindow(defaultView, "terminal_icon.bmp");
        iconWindow.drawContents();

        System.out.println("\n(Demonstrating independence) Let's forcibly swap the implementation to PMWindowImp:");
        // In a real framework, you inject the Implementor. Here we simulate the change.
        iconWindow.imp = new PMWindowImp();
        iconWindow.drawContents();
    }
}