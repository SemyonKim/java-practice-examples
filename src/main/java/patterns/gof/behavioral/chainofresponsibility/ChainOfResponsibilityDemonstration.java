package patterns.gof.behavioral.chainofresponsibility;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * DESIGN PATTERN: Chain of Responsibility
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  N/A
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Avoid coupling the sender of a request to its receiver by giving more than
 * one object a chance to handle the request. Chain the receiving objects and
 * pass the request along the chain until an object handles it.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Scenario: A UI Help System. When a user clicks "F1" (Help), the system
 * checks if the currently focused button has help text. If not, it checks the
 * dialog box holding the button. If not, it falls back to the application-wide
 * help.
 * Analogy: A corporate approval process. You ask your manager for a hardware
 * budget. If it's under $500, they approve it. If it's $5000, they forward it
 * to the Director. If it's $50000, it goes to the CEO.
 * <p>
 * 3. APPLICABILITY
 * - When multiple objects can handle a request, and the handler isn't known.
 * - When you want to issue a request to several objects without explicit targets.
 * - When the set of handlers needs to be specified dynamically.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Handler (HelpHandler): Interface/Abstract class for handling requests.
 * - ConcreteHandler (Button, Dialog, Application): Implements handling logic.
 * - Client (Main): Initiates the request.
 * <p>
 * 5. COLLABORATIONS
 * Client -> Button -> Dialog -> Application. The request stops propagating
 * once a component claims it.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - Pros: Decouples sender and receiver. Simplifies object interconnectivity.
 * - Cons: Unhandled requests might fall off the end. Hard to debug long chains.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * Modern Java frameworks (like Spring Security and Servlets) frequently use
 * lists or Iterators representing the chain (often called `FilterChain`) instead
 * of deep object hierarchies. However, the linked-list approach demonstrated
 * below remains the classic GoF implementation.
 * ============================================================================
 */
public class ChainOfResponsibilityDemonstration {

    // ========================================================================
    // MISC: SHARED DATA OBJECTS
    // ========================================================================

    /**
     * A simple enumerator representing different types of help topics.
     */
    enum Topic {
        NO_HELP_TOPIC, PRINT_TOPIC, PAPER_ORIENTATION_TOPIC, APPLICATION_TOPIC
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Without the pattern, the client has to explicitly know about all elements
     * and write monolithic conditional logic to find who should handle the
     * request. This tightly couples the client to the UI structure.
     */
    static class NaiveHelpSystem {
        private final Map<String, Topic> widgetHelpMap = new HashMap<>();

        public void assignHelp(String widgetId, Topic topic) {
            widgetHelpMap.put(widgetId, topic);
        }

        public void requestHelp(String currentWidget, String parentWidget, String appScope) {
            // Hardcoded rigid hierarchy check
            if (widgetHelpMap.containsKey(currentWidget) && widgetHelpMap.get(currentWidget) != Topic.NO_HELP_TOPIC) {
                System.out.println("Naive Approach: Showing help for " + widgetHelpMap.get(currentWidget));
            } else if (widgetHelpMap.containsKey(parentWidget) && widgetHelpMap.get(parentWidget) != Topic.NO_HELP_TOPIC) {
                System.out.println("Naive Approach: Showing help for " + widgetHelpMap.get(parentWidget));
            } else {
                System.out.println("Naive Approach: Showing generic app help -> " + widgetHelpMap.get(appScope));
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * We define a standard Handler interface/abstract class. Objects link
     * together, forming an implicit chain.
     */

    /**
     * Handler Participant: Defines the interface for handling requests and
     * holds a reference to the successor.
     */
    abstract static class HelpHandler {
        private final HelpHandler successor;
        private final Topic topic;

        protected HelpHandler(HelpHandler successor, Topic topic) {
            this.successor = successor;
            this.topic = topic;
        }

        public boolean hasHelp() {
            return topic != Topic.NO_HELP_TOPIC;
        }

        public void handleHelp() {
            if (successor != null) {
                successor.handleHelp();
            } else {
                System.out.println("[Warning] Request fell off the chain without being handled.");
            }
        }

        public Topic getTopic() {
            return topic;
        }
    }

    /**
     * Intermediate Base Class (Optional but common when merging with Composite)
     */
    abstract static class Widget extends HelpHandler {
        private final Widget parent;

        protected Widget(Widget parent, Topic topic) {
            // The parent naturally acts as the successor in a UI tree!
            super(parent, topic);
            this.parent = parent;
        }
    }

    /**
     * ConcreteHandler Participant 1
     */
    static class Button extends Widget {
        public Button(Widget parent, Topic topic) {
            super(parent, topic);
        }

        @Override
        public void handleHelp() {
            if (hasHelp()) {
                System.out.println("Button handled request. Offering help on topic: " + getTopic());
            } else {
                System.out.println("Button cannot handle. Forwarding to parent...");
                super.handleHelp(); // Forwards to successor (parent)
            }
        }
    }

    /**
     * ConcreteHandler Participant 2
     */
    static class Dialog extends Widget {
        public Dialog(HelpHandler successor, Topic topic) {
            // Dialogs might not have a Widget parent, but an Application successor
            super((Widget) null, topic);
            // We use a slight hack for the demo to inject a general successor
            // Realistically, Dialog would take a HelpHandler parent in its constructor.
            super.handleHelp(); // Not used directly in this mock, see alternative constructor below
        }

        // Alternative constructor to link directly to an Application or generic HelpHandler
        public Dialog(HelpHandler applicationHandler, Topic topic, boolean isRoot) {
            super(null, topic); // Ignore parent for root dialogs
            // In Java, we often use setters for successors to avoid `super` constructor limits
        }
    }

    /**
     * Refactored ConcreteHandler Participant 2 (Dialog) to properly support the chain
     */
    static class BetterDialog extends HelpHandler {
        public BetterDialog(HelpHandler successor, Topic topic) {
            super(successor, topic);
        }

        @Override
        public void handleHelp() {
            if (hasHelp()) {
                System.out.println("Dialog handled request. Offering help on topic: " + getTopic());
            } else {
                System.out.println("Dialog cannot handle. Forwarding to Application...");
                super.handleHelp();
            }
        }
    }

    /**
     * ConcreteHandler Participant 3: The fallback / root of the chain.
     */
    static class Application extends HelpHandler {
        public Application(Topic topic) {
            super(null, topic); // Application has no successor
        }

        @Override
        public void handleHelp() {
            if (hasHelp()) {
                System.out.println("Application handled request. Offering generic help: " + getTopic());
            } else {
                super.handleHelp(); // Will trigger the unhandled warning
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     * Setting up the chain and triggering requests to demonstrate dynamic routing.
     */
    public static void main(String[] args) {
        System.out.println("--- Chain of Responsibility: Naive Approach ---");
        NaiveHelpSystem naiveSystem = new NaiveHelpSystem();
        naiveSystem.assignHelp("AppScope", Topic.APPLICATION_TOPIC);
        naiveSystem.assignHelp("PrintDialog", Topic.PRINT_TOPIC);
        naiveSystem.assignHelp("OkButton", Topic.NO_HELP_TOPIC);

        // Client manually dictating the fallback logic
        naiveSystem.requestHelp("OkButton", "PrintDialog", "AppScope");


        System.out.println("\n--- Chain of Responsibility: Pattern Approach ---");

        // 1. Build the chain from the bottom up (or top down)
        // anApplication is the ultimate successor
        Application application = new Application(Topic.APPLICATION_TOPIC);

        // aPrintDialog's successor is anApplication
        BetterDialog printDialog = new BetterDialog(application, Topic.PRINT_TOPIC);

        // aPrintButton's successor is the printDialog. It has NO specific help topic.
        Button printButton = new Button(null, Topic.NO_HELP_TOPIC) {
            @Override
            public void handleHelp() {
                if (hasHelp()) {
                    System.out.println("Button handled request.");
                } else {
                    System.out.println("Button cannot handle. Forwarding...");
                    printDialog.handleHelp(); // Manually forwarding to dialog for demo purposes
                }
            }
        };

        // Another button that DOES have help
        Button okButton = new Button(null, Topic.PAPER_ORIENTATION_TOPIC) {
            @Override
            public void handleHelp() {
                if (hasHelp()) {
                    System.out.println("OK Button handled request: " + getTopic());
                } else {
                    printDialog.handleHelp();
                }
            }
        };

        // 2. Client initiates requests
        System.out.println("Action: User asks for help on the Print Button (No topic assigned)");
        printButton.handleHelp();
        // Output -> Button forwards -> Dialog handles (PRINT_TOPIC)

        System.out.println("\nAction: User asks for help on the OK Button (Has specific topic)");
        okButton.handleHelp();
        // Output -> OK Button handles immediately (PAPER_ORIENTATION_TOPIC)

        System.out.println("\nAction: User asks for help on a Dialog with no topic");
        BetterDialog emptyDialog = new BetterDialog(application, Topic.NO_HELP_TOPIC);
        emptyDialog.handleHelp();
        // Output -> Dialog forwards -> Application handles (APPLICATION_TOPIC)
    }
}