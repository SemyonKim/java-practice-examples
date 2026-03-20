package patterns.gof.behavioral.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * ============================================================================
 * DESIGN PATTERN: Command
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Action, Transaction
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Encapsulate a request as an object, thereby letting you parameterize clients
 * with different requests, queue or log requests, and support undoable
 * operations.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * UI toolkits provide generic components (menus, buttons) that need to execute
 * application-specific logic. By wrapping the logic in a Command object, the
 * UI component simply calls `execute()` without coupling itself to the domain.
 * Analogy: A customer (Client) hands an order slip (Command) to a waiter
 * (Invoker). The waiter queues it for the chef (Receiver) who fulfills it.
 * <p>
 * 3. APPLICABILITY
 * - Parameterize objects with actions.
 * - Specify, queue, and execute requests at different times.
 * - Support robust undo/redo functionality and logging.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Command: Declares the execution interface.
 * - ConcreteCommand: Binds a Receiver to an action.
 * - Client: Instantiates ConcreteCommands and assigns Receivers.
 * - Invoker: Asks the command to execute the request.
 * - Receiver: The domain object performing the actual work.
 * <p>
 * 5. COLLABORATIONS
 * Client creates Command pointing to a Receiver. The Command is passed to an
 * Invoker. The Invoker calls Command.execute(), which calls Receiver.action().
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Decouples invoker from receiver.
 * + Commands become manipulatable, first-class objects.
 * + Easily extended via Composite (MacroCommand).
 * - Can bloat the system with many small concrete command classes.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * While simple commands can use `Runnable` and method references in Java,
 * undoable commands require an interface with multiple methods (execute/undo).
 * Because functional interfaces only allow one abstract method, undoable
 * commands must be implemented via concrete classes or anonymous inner classes.
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - java.lang.Runnable / java.util.concurrent.Callable
 * - javax.swing.Action
 * - Spring's Transactional boundaries and JdbcTemplate callbacks.
 * <p>
 * 9. RELATED PATTERNS
 * - Composite: For MacroCommands.
 * - Memento: To save state for undo operations.
 * - Prototype: To copy commands for history tracking.
 * ============================================================================
 */
public class CommandDemonstration {

    // ========================================================================
    // MOCKED ENTITIES
    // ========================================================================
    /**
     * Receiver Participant: Knows how to perform the actual operations.
     */
    static class Document {
        private String content = "";

        public void paste(String text) {
            content += text;
            System.out.println("Document pasted: " + text + " | Current: " + content);
        }

        public void deleteLast(int length) {
            if (content.length() >= length) {
                content = content.substring(0, content.length() - length);
            }
            System.out.println("Document deleted | Current: " + content);
        }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * A tight coupling between the UI Invoker and the Domain Receiver.
     * Adding undo functionality here would require hardcoding state management
     * directly inside the UI component.
     */
    static class NaiveImplementation {
        static class MenuItem {
            private final Document document;

            public MenuItem(Document document) {
                this.document = document;
            }

            public void click() {
                // Hardcoded, inflexible action
                document.paste("DefaultText ");
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     */

    // 1. Command Interface (Cannot be a FunctionalInterface due to undo())
    interface Command {
        void execute();
        void undo();
    }

    // 2. Concrete Command
    static class PasteCommand implements Command {
        private final Document document; // Receiver
        private final String textToPaste;

        public PasteCommand(Document document, String text) {
            this.document = document;
            this.textToPaste = text;
        }

        @Override
        public void execute() {
            document.paste(textToPaste);
        }

        @Override
        public void undo() {
            // Reversing the specific action applied in execute()
            document.deleteLast(textToPaste.length());
        }
    }

    // 3. Composite Command (MacroCommand)
    static class MacroCommand implements Command {
        private final List<Command> commands = new ArrayList<>();

        public void add(Command c) {
            commands.add(c);
        }

        @Override
        public void execute() {
            System.out.println("--- Executing Macro ---");
            for (Command c : commands) {
                c.execute();
            }
        }

        @Override
        public void undo() {
            System.out.println("--- Undoing Macro ---");
            // Must undo in reverse order
            for (int i = commands.size() - 1; i >= 0; i--) {
                commands.get(i).undo();
            }
        }
    }

    // 4. Invoker
    static class MenuItem {
        private final String label;
        private Command command;

        public MenuItem(String label) {
            this.label = label;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        public void click() {
            System.out.println("[" + label + "] clicked.");
            if (command != null) {
                command.execute();
            }
        }
    }

    // Advanced Invoker logic: Command History Manager
    static class ApplicationHistory {
        private final Deque<Command> history = new ArrayDeque<>();

        public void executeAndRecord(Command command) {
            command.execute();
            history.push(command); // Store for potential undo
        }

        public void undoLast() {
            if (!history.isEmpty()) {
                System.out.println("[System] Undoing last action...");
                Command command = history.pop();
                command.undo();
            } else {
                System.out.println("[System] No actions to undo.");
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        Document sharedDoc = new Document();

        System.out.println("--- Command Pattern: Naive Approach ---");
        NaiveImplementation.MenuItem naiveItem = new NaiveImplementation.MenuItem(sharedDoc);
        naiveItem.click();

        System.out.println("\n--- Command Pattern: Pattern Approach ---");
        // Reset state
        sharedDoc = new Document();
        ApplicationHistory historyManager = new ApplicationHistory();

        // Concrete Commands instantiated by the Client
        Command pasteHello = new PasteCommand(sharedDoc, "Hello ");
        Command pasteWorld = new PasteCommand(sharedDoc, "World! ");

        // UI Components (Invokers) initialized dynamically
        MenuItem helloItem = new MenuItem("Paste 'Hello'");
        MenuItem worldItem = new MenuItem("Paste 'World'");

        // Binding Invokers to Commands (Using anonymous blocks to integrate history)
        helloItem.setCommand(new Command() {
            @Override public void execute() { historyManager.executeAndRecord(pasteHello); }
            @Override public void undo() { /* Handled by history manager */ }
        });

        worldItem.setCommand(new Command() {
            @Override public void execute() { historyManager.executeAndRecord(pasteWorld); }
            @Override public void undo() { }
        });

        // 1. Standard execution
        helloItem.click();
        worldItem.click();

        // 2. Undo execution
        historyManager.undoLast(); // Removes "World! "
        historyManager.undoLast(); // Removes "Hello "

        // 3. Macro execution
        System.out.println("\n--- Macro Execution ---");
        MacroCommand macro = new MacroCommand();
        macro.add(pasteHello);
        macro.add(pasteWorld);

        MenuItem macroItem = new MenuItem("Run Macro");
        macroItem.setCommand(new Command() {
            @Override public void execute() { historyManager.executeAndRecord(macro); }
            @Override public void undo() { }
        });

        macroItem.click();

        // Undoes the entire sequence managed by the MacroCommand
        historyManager.undoLast();
    }
}