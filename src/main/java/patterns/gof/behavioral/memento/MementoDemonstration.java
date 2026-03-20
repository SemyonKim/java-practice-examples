package patterns.gof.behavioral.memento;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ============================================================================
 * DESIGN PATTERN: Memento
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Token
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Capture and externalize an object's internal state without violating
 * encapsulation, allowing the object to be restored to this state later.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * When building undo functionality (e.g., in a text editor or a game), you need
 * to save the system's state before applying a change. However, saving the state
 * externally breaks the principle of encapsulation.
 * Analogy: Taking a save-state snapshot in a video game. The game engine
 * (Originator) bundles all its hidden memory variables into a single save file
 * (Memento) and hands it to the memory card manager (Caretaker). The manager
 * holds the file but cannot read or alter the player's internal health values.
 * <p>
 * 3. APPLICABILITY
 * - When you need to implement a robust undo/redo or rollback mechanism.
 * - When exposing the direct state fields would break object encapsulation.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Originator: The object whose state needs saving. Creates the Memento.
 * - Memento: A value object/record storing the state snapshot.
 * - Caretaker: Manages the Memento's lifecycle but never inspects its contents.
 * <p>
 * 5. COLLABORATIONS
 * The Caretaker requests a Memento from the Originator, holds it temporarily,
 * and passes it back to the Originator to trigger a rollback.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - PRO: Prevents encapsulation leakage. Simplifies Originator logic.
 * - CON: Can consume a lot of RAM if snapshots are large and the history
 * stack is deep.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * Use Java 16+ `record` types to create lightweight, immutable Mementos.
 * Use nested classes and marker interfaces to strictly enforce the "narrow"
 * and "wide" interface split.
 */
public class MementoDemonstration {

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * In the naive approach, the object exposes its state directly to the
     * external caller to facilitate backups. This strictly breaks encapsulation.
     */
    static class NaiveTextEditor {
        public String text = "";
        public int cursorPosition = 0;

        // Encapsulation broken: anyone can manipulate these internal details freely.
        public void write(String words) {
            text += words;
            cursorPosition += words.length();
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * Using strict encapsulation. The Caretaker only knows about a generic
     * marker interface, completely ignorant of the actual snapshot data.
     */

    // Narrow interface for the Caretaker.
    public interface EditorState {}

    // Originator
    static class RobustTextEditor {
        private StringBuilder text;
        private int cursorPosition;

        public RobustTextEditor() {
            this.text = new StringBuilder();
            this.cursorPosition = 0;
        }

        public void write(String words) {
            text.append(words);
            cursorPosition += words.length();
        }

        public void print() {
            System.out.println("Editor Output: '" + text.toString() + "' | Cursor at: " + cursorPosition);
        }

        // Creates a Memento holding a snapshot of current state
        public EditorState save() {
            // Modern Java: Using an inner record to implicitly handle immutability
            // and hide the wide interface from the outside world.
            return new Snapshot(text.toString(), cursorPosition);
        }

        // Restores state from a Memento
        public void restore(EditorState state) {
            if (!(state instanceof Snapshot snap)) {
                throw new IllegalArgumentException("Unknown state format!");
            }
            // Safe extraction, completely managed by Originator
            this.text = new StringBuilder(snap.textContent());
            this.cursorPosition = snap.cursorPos();
        }

        // Wide interface: Hidden inside the Originator.
        // A record is perfect for an immutable data carrier.
        private record Snapshot(String textContent, int cursorPos) implements EditorState {}
    }

    // Caretaker
    static class HistoryManager {
        // Bounded Deque for Undo history to prevent memory leaks
        private final Deque<EditorState> history = new ArrayDeque<>();
        private final RobustTextEditor editor;

        public HistoryManager(RobustTextEditor editor) {
            this.editor = editor;
        }

        public void backup() {
            history.push(editor.save());
        }

        public void undo() {
            if (!history.isEmpty()) {
                editor.restore(history.pop());
            } else {
                System.out.println("Nothing to undo.");
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Memento Naive Approach ---");
        NaiveTextEditor naiveEditor = new NaiveTextEditor();
        naiveEditor.write("Hello ");
        // To back-up, the client has to manually rip out internal state:
        String backupText = naiveEditor.text;
        int backupCursor = naiveEditor.cursorPosition;

        naiveEditor.write("World!");
        // To restore, the client manually overwrites internals:
        naiveEditor.text = backupText;
        naiveEditor.cursorPosition = backupCursor;
        System.out.println("Restored naive editor: '" + naiveEditor.text + "'");

        System.out.println("\n--- Memento Pattern Approach ---");
        RobustTextEditor editor = new RobustTextEditor();
        HistoryManager history = new HistoryManager(editor);

        editor.write("Design ");
        editor.print();

        history.backup(); // Caretaker saves a memento
        editor.write("Patterns ");
        editor.print();

        history.backup(); // Caretaker saves a memento
        editor.write("Are Boring!"); // Uh oh, a mistake!
        editor.print();

        System.out.println("\nExecuting Undo...");
        history.undo();
        editor.print(); // "Design Patterns "

        System.out.println("Executing Undo...");
        history.undo();
        editor.print(); // "Design "

        System.out.println("\n(Notice how HistoryManager managed the undo states without ever knowing what 'text' or 'cursorPosition' are!)");
    }
}