package patterns.gof.structural.proxy;

/**
 * ============================================================================
 * DESIGN PATTERN: Proxy
 * CATEGORY:       Structural
 * ALSO KNOWN AS:  Surrogate
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Provide a surrogate or placeholder for another object to control access to it.
 * It resolves the problem of expensive object creation, remote access, or
 * access protection by introducing an intermediary.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Imagine opening a text document with dozens of high-res images. If the app
 * loads all images into memory immediately, the app will freeze. Instead,
 * the app loads an 'ImageProxy' for each image. The proxy quickly loads a
 * bounding box or a low-res placeholder. Only when the user scrolls the
 * image into view does the proxy instantiate and draw the real, heavy Image.
 * <p>
 * 3. APPLICABILITY
 * Use when you need a more versatile or sophisticated reference to an object
 * than a simple pointer (Virtual Proxies for lazy loading, Protection Proxies
 * for security, Remote Proxies for network encapsulation).
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Subject (Graphic): Interface defining the operations.
 * - RealSubject (Image): The heavy/real object doing the actual work.
 * - Proxy (ImageProxy): Holds a reference to the RealSubject, implements Subject,
 * and controls access/lifecycle of the RealSubject.
 * <p>
 * 5. COLLABORATIONS
 * Client -> Subject (handled by Proxy) -> RealSubject.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - Pros: Controls access, defers heavy instantiation, manages memory efficiently.
 * - Cons: Can introduce latency (when the real object finally loads), makes
 * debugging slightly harder due to indirection.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * In modern Java, while static proxies (like this example) are useful,
 * Dynamic Proxies (java.lang.reflect.Proxy) and bytecode proxies (ByteBuddy, CGLIB)
 * are heavily used by Spring (for @Transactional, @Cacheable) and Hibernate
 * to dynamically intercept method calls at runtime.
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - Hibernate ORM: Uses lazy loading proxies for database entities.
 * - Spring AOP: Uses proxies to wrap beans with aspects.
 * - java.lang.reflect.Proxy: The standard JDK dynamic proxy API.
 * <p>
 * 9. RELATED PATTERNS
 * - Adapter: Changes an object's interface. Proxy keeps the same interface.
 * - Decorator: Adds functionality dynamically. Proxy controls access/lifecycle.
 * ============================================================================
 */
public class ProxyDemonstration {

    // ========================================================================
    // MOCKED ENTITIES
    // ========================================================================
    record Point(int x, int y) {}

    // ========================================================================
    // PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
    // ========================================================================
    /**
     * The naive approach loads heavy objects immediately, consuming time
     * and memory even if the user never scrolls to see them.
     */
    static class NaiveDocument {
        private final RealImage image;

        public NaiveDocument(String filename) {
            // NAIVE: The heavy image is loaded the moment the document is created.
            System.out.println("   [Document] Initialized. Loading resources natively...");
            this.image = new RealImage(filename);
        }

        public void render() {
            image.draw(new Point(0, 0));
        }
    }

    // ========================================================================
    // PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
    // ========================================================================

    // 1. Subject Interface
    interface Graphic {
        void draw(Point position);
        Point getExtent();
        void store();
        void load();
    }

    // 2. RealSubject
    static class RealImage implements Graphic {
        private final String filename;
        private final Point extent;

        public RealImage(String filename) {
            this.filename = filename;
            // Simulate an expensive operation (e.g., loading bytes from disk)
            simulateExpensiveLoad();
            this.extent = new Point(1920, 1080); // Mocked dimensions
        }

        private void simulateExpensiveLoad() {
            System.out.println("   [RealImage] Loading highly detailed image from disk: " + filename + " (Taking time...)");
            try {
                Thread.sleep(800); // Simulated delay
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void draw(Point position) {
            System.out.println("   [RealImage] Drawing high-res image '" + filename + "' at " + position);
        }

        @Override
        public Point getExtent() {
            return extent;
        }

        @Override
        public void store() {
            System.out.println("   [RealImage] Storing image '" + filename + "' to disk.");
        }

        @Override
        public void load() {
            System.out.println("   [RealImage] Loading image '" + filename + "' from disk.");
        }
    }

    // 3. Proxy (Virtual Proxy implementation)
    static class ImageProxy implements Graphic {
        private final String filename;
        private RealImage realImage; // Reference to the actual object
        private Point extent;        // Cached data to avoid early loading

        public ImageProxy(String filename) {
            this.filename = filename;
            this.realImage = null; // Lazy initialization
            this.extent = null;
        }

        // Internal method to fetch the real subject only when absolutely necessary
        private RealImage getRealImage() {
            if (realImage == null) {
                System.out.println("   [ImageProxy] Instantiating real object on demand...");
                realImage = new RealImage(filename);
            }
            return realImage;
        }

        @Override
        public void draw(Point position) {
            System.out.println("   [ImageProxy] Forwarding draw request...");
            getRealImage().draw(position);
        }

        @Override
        public Point getExtent() {
            // Virtual Proxy optimization: Check if we have cached the extent
            if (extent == null) {
                if (realImage != null) {
                    extent = realImage.getExtent();
                } else {
                    // Mock: Read just the file header to get dimensions without loading the full image
                    System.out.println("   [ImageProxy] Reading file headers to determine extent without heavy loading...");
                    extent = new Point(1920, 1080);
                }
            }
            return extent;
        }

        @Override
        public void store() {
            // Pass-through
            getRealImage().store();
        }

        @Override
        public void load() {
            // Pass-through
            getRealImage().load();
        }
    }

    /**
     * A simulated client that uses the Graphic interface.
     */
    static class PatternDocument {
        private final Graphic image;

        public PatternDocument(Graphic image) {
            System.out.println("   [Document] Initialized with a provided Graphic (Proxy).");
            this.image = image;
        }

        public void updateLayout() {
            Point dimensions = image.getExtent();
            System.out.println("   [Document] Layout updated using dimensions: " + dimensions);
        }

        public void render() {
            image.draw(new Point(50, 50));
        }
    }

    // ========================================================================
    // PHASE 3: EXECUTION (MAIN METHOD)
    // ========================================================================
    public static void main(String[] args) {
        System.out.println("--- Proxy Pattern: Naive Approach ---");
        // Document initialization immediately triggers the heavy image load,
        // blocking the main thread even if the user hasn't scrolled yet.
        NaiveDocument naiveDoc = new NaiveDocument("high_res_photo_naive.png");
        System.out.println("Action: User scrolls down...");
        naiveDoc.render();


        System.out.println("\n--- Proxy Pattern: Pattern Approach (Virtual Proxy) ---");
        // The client simply takes an ImageProxy. Notice how fast this initializes.
        PatternDocument patternDoc = new PatternDocument(new ImageProxy("high_res_photo_proxy.png"));

        System.out.println("\nAction: Formatter asks for document layout dimensions.");
        // Proxy handles this WITHOUT loading the heavy RealImage
        patternDoc.updateLayout();

        System.out.println("\nAction: User finally scrolls down to the image.");
        // NOW the proxy forwards the draw call, triggering the heavy instantiation
        patternDoc.render();

        System.out.println("\nAction: User forces a re-draw.");
        // The real image is already cached by the proxy, so it draws instantly now
        patternDoc.render();
    }
}