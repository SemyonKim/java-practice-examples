package patterns.caveofprogramming.adapter;

/**
 * <h1>Adapter Pattern</h1>
 * <p>
 * <b>Definition:</b>
 * Convert the interface of a class into another interface clients expect.
 * Adapter lets classes work together that couldn't otherwise because of
 * incompatible interfaces.
 * <p>
 * <b>Official Recommendations & Best Practices:</b>
 * <ul>
 * <li><b>Composition over Inheritance:</b> It is generally recommended to use
 * "Object Adapters" (using a reference to the adaptee) rather than "Class Adapters"
 * (using multiple inheritance), as Java does not support multiple inheritance of classes anyway.</li>
 * <li><b>Single Responsibility:</b> You can separate the interface or data conversion
 * code from the primary business logic of the program.</li>
 * <li><b>Transparency:</b> The client should not be aware that an adapter is being used;
 * it should just interact with the target interface.</li>
 * <li><b>Usage:</b> Use this when you want to use an existing class, but its interface
 * does not match the one you need, or when you need to use several existing subclasses
 * that lack some common functionality.</li>
 * </ul>
 */
interface VideoSource {
    void provideVideoSignal();
}

/**
 * The "Adaptee": An old class with a specific interface we want to use,
 * but it doesn't match our 'VideoSource' interface.
 */
class LegacyMonitor {
    public void displaySerialSignal(String signalData) {
        System.out.println("Legacy Monitor displaying: " + signalData);
    }
}

/**
 * The "Adapter": This class implements the modern interface (Target)
 * and wraps the old class (Adaptee).
 */
public class VideoAdapter implements VideoSource {
    private LegacyMonitor legacyMonitor;

    public VideoAdapter(LegacyMonitor legacyMonitor) {
        this.legacyMonitor = legacyMonitor;
    }

    @Override
    public void provideVideoSignal() {
        // The Adapter translates the request:
        String convertedSignal = "Converted VGA to Serial Data";
        legacyMonitor.displaySerialSignal(convertedSignal);
    }
}

/**
 * The Client: Only knows how to work with 'VideoSource'.
 */
class Computer {
    public void connect(VideoSource source) {
        source.provideVideoSignal();
    }
}