package patterns.caveofprogramming.facade;

/**
 * <h1>Facade Pattern</h1>
 * <p>
 * <b>Definition:</b>
 * Provide a unified interface to a set of interfaces in a subsystem.
 * Facade defines a higher-level interface that makes the subsystem easier to use.
 * <p>
 * <b>Official Recommendations & Best Practices:</b>
 * <ul>
 * <li><b>Simplification:</b> Use a Facade when you want to provide a simple interface
 * to a complex subsystem. It shouldn't prevent you from using the complex subsystem
 * directly if you need to.</li>
 * <li><b>Loose Coupling:</b> It reduces the dependencies between the client code
 * and the inner workings of a library or API.</li>
 * <li><b>Principle of The Least Knowledge (Law of Demeter):</b> Talk only to your immediate
 * friends. The client only talks to the Facade, not the 10 classes behind it.</li>
 * <li><b>Layering:</b> Use Facades to define an entry point to each subsystem level.
 * If subsystems are dependent, they can communicate through their Facades to simplify
 * dependencies.</li>
 * </ul>
 */
public class HomeTheaterFacade {
    private Light lights;
    private AudioSystem audio;
    private VideoProjector projector;

    public HomeTheaterFacade(Light lights, AudioSystem audio, VideoProjector projector) {
        this.lights = lights;
        this.audio = audio;
        this.projector = projector;
    }

    /**
     * The simplified "One-Click" method.
     * The client doesn't need to know the order of operations or the specific APIs
     * of the lights, audio, or projector.
     */
    public void watchMovie() {
        System.out.println("Get ready to watch a movie...");
        lights.dim(10);
        projector.on();
        projector.setWideScreenMode();
        audio.on();
        audio.setVolume(5);
        System.out.println("Movie started.");
    }

    public void endMovie() {
        System.out.println("Shutting down movie theater...");
        lights.on();
        projector.off();
        audio.off();
    }
}

// --- Subsystem Classes (The "Complexity" hidden by the Facade) ---

class Light {
    void on() { System.out.println("Lights are on."); }
    void dim(int level) { System.out.println("Lights dimmed to " + level + "%."); }
}

class AudioSystem {
    void on() { System.out.println("Audio system on."); }
    void off() { System.out.println("Audio system off."); }
    void setVolume(int level) { System.out.println("Volume set to " + level); }
}

class VideoProjector {
    void on() { System.out.println("Projector on."); }
    void off() { System.out.println("Projector off."); }
    void setWideScreenMode() { System.out.println("Projector in widescreen mode (16:9)."); }
}