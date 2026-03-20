package patterns.gof.behavioral.mediator;

/**
 * ============================================================================
 * DESIGN PATTERN: Mediator
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Controller, Middleman
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Define an object that encapsulates how a set of objects interact, promoting
 * loose coupling by keeping objects from referring to each other explicitly.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Complex UIs often have tangled logic where checking a box disables a button
 * and clears a text field. Hardcoding these dependencies into the widgets
 * creates unmaintainable code.
 * Analogy: A chat room server (Mediator) coordinates messages between multiple
 * users (Colleagues) so users don't need direct P2P connections to everyone.
 * <p>
 * 3. APPLICABILITY
 * - When a set of objects communicate in complex, unstructured ways.
 * - When reusing an object is difficult due to its numerous dependencies.
 * - When behavior distributed among several classes needs to be localized.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Mediator: The interface for communication (often omitted if only one exists).
 * - ConcreteMediator: The central hub containing the routing/coordination logic.
 * - Colleague: The individual components (e.g., UI widgets or services).
 * <p>
 * 5. CONSEQUENCES (TRADE-OFFS)
 * + Single Responsibility: Extracts communication logic into one place.
 * + Open/Closed Principle: Easy to introduce new mediators without changing colleagues.
 * - Complexity Risk: The Mediator can easily become a bloated "God Object."
 * <p>
 * 6. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * In modern Java, this pattern is heavily utilized in UI frameworks (JavaFX
 * Controllers) and backend routing (Spring MVC DispatcherServlet). Using interfaces
 * or standard Java Functional Interfaces (e.g., Runnables or Consumers) can make
 * the Mediator-Colleague linkage very clean.
 * ============================================================================
 */
public class MediatorDemonstration {

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Widgets have direct references to each other. Modifying one widget requires
     * changing the code in all interacting widgets. High coupling.
     */
    static class NaiveCheckbox {
        private boolean isChecked;
        private final NaiveButton submitButton; // Tight coupling
        private final NaiveTextField inputField; // Tight coupling

        public NaiveCheckbox(NaiveButton submitButton, NaiveTextField inputField) {
            this.submitButton = submitButton;
            this.inputField = inputField;
        }

        public void toggle() {
            this.isChecked = !this.isChecked;
            System.out.println("NaiveCheckbox toggled to: " + isChecked);
            // Tangled business logic embedded right in the widget
            submitButton.setEnabled(isChecked);
            if (!isChecked) {
                inputField.clear();
            }
        }
    }

    static class NaiveButton {
        private boolean isEnabled = false;
        public void setEnabled(boolean state) { this.isEnabled = state; }
    }

    static class NaiveTextField {
        private String text = "";
        public void clear() { this.text = ""; }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * Using a Mediator to centralize the interaction logic. The widgets only
     * know about the Mediator, not about each other.
     */

    // Abstract Colleague
    abstract static class UIWidget {
        protected FormMediator mediator;

        public UIWidget(FormMediator mediator) {
            this.mediator = mediator;
        }

        // Colleagues notify the mediator when their state changes
        protected void changed() {
            mediator.notifyWidgetChanged(this);
        }
    }

    // Concrete Colleague 1
    static class Checkbox extends UIWidget {
        private boolean checked = false;

        public Checkbox(FormMediator mediator) { super(mediator); }

        public void toggle() {
            checked = !checked;
            System.out.println("Checkbox toggled to: " + checked);
            changed(); // Notify mediator instead of acting directly
        }

        public boolean isChecked() { return checked; }
    }

    // Concrete Colleague 2
    static class Button extends UIWidget {
        private boolean enabled = false;

        public Button(FormMediator mediator) { super(mediator); }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            System.out.println("Button enabled state is now: " + enabled);
        }
    }

    // Concrete Colleague 3
    static class TextField extends UIWidget {
        private String text = "";

        public TextField(FormMediator mediator) { super(mediator); }

        public void setText(String text) {
            this.text = text;
            System.out.println("TextField text is now: '" + text + "'");
            changed();
        }

        public void clear() {
            this.text = "";
            System.out.println("TextField was cleared.");
        }

        public String getText() { return text; }
    }

    // The Mediator Interface (Optional if only one exists, but good practice)
    interface FormMediator {
        void notifyWidgetChanged(UIWidget sender);
    }

    // Concrete Mediator
    static class AuthenticationDialogMediator implements FormMediator {
        private Checkbox termsCheckbox;
        private Button loginButton;
        private TextField usernameField;

        // The Mediator handles the registration/setup of colleagues
        public void registerWidgets(Checkbox cb, Button btn, TextField tf) {
            this.termsCheckbox = cb;
            this.loginButton = btn;
            this.usernameField = tf;
        }

        @Override
        public void notifyWidgetChanged(UIWidget sender) {
            // Centralized interaction logic
            if (sender == termsCheckbox) {
                // Button is only enabled if terms are checked AND username is not empty
                boolean isValid = termsCheckbox.isChecked() && !usernameField.getText().isEmpty();
                loginButton.setEnabled(isValid);

                if (!termsCheckbox.isChecked()) {
                    usernameField.clear();
                }
            } else if (sender == usernameField) {
                // Typing in the field re-evaluates button state
                boolean isValid = termsCheckbox.isChecked() && !usernameField.getText().isEmpty();
                loginButton.setEnabled(isValid);
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Mediator Naive Approach (Mental Exercise) ---");
        System.out.println("Imagine a UI where 20 widgets all hold references to each other. Maintenance nightmare!\n");

        System.out.println("--- Mediator Pattern Approach ---");

        // 1. Instantiate the Mediator
        AuthenticationDialogMediator dialogDirector = new AuthenticationDialogMediator();

        // 2. Instantiate Colleagues, passing the Mediator reference
        Checkbox termsCb = new Checkbox(dialogDirector);
        Button loginBtn = new Button(dialogDirector);
        TextField userField = new TextField(dialogDirector);

        // 3. Register Colleagues with the Mediator
        dialogDirector.registerWidgets(termsCb, loginBtn, userField);

        // 4. Simulate user interaction
        System.out.println("\n[User inputs username...]");
        userField.setText("john_doe");
        // Output: TextField text is now: 'john_doe'.
        // Mediator evaluates button: Terms not checked, button remains false.

        System.out.println("\n[User checks 'Agree to terms' box...]");
        termsCb.toggle();
        // Output: Checkbox toggled to: true.
        // Mediator evaluates: terms checked & username filled. Button enabled!

        System.out.println("\n[User unchecks 'Agree to terms' box...]");
        termsCb.toggle();
        // Output: Checkbox toggled to: false.
        // Mediator evaluates: button disabled, clears text field.
    }
}