package patterns.caveofprogramming.observermvc.controller;

import patterns.caveofprogramming.observermvc.model.Model;
import patterns.caveofprogramming.observermvc.view.LoginFormEvent;
import patterns.caveofprogramming.observermvc.view.LoginListener;
import patterns.caveofprogramming.observermvc.view.View;

/**
 * The Controller is the Observer.
 * It implements LoginListener to "watch" the View. When a login event occurs,
 * it decides what to do with the data (e.g., validate against the Model).
 */
public class Controller implements LoginListener {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /**
     * The Observer method.
     * When the "OK" button is clicked in the View, this method is triggered.
     */
    @Override
    public void loginPerformed(LoginFormEvent event) {
        // 1. Extract data from the event
        String name = event.getName();
        String password = event.getPassword();

        // 2. Perform some logic (e.g., validation)
        if (password.length() < 4) {
            System.out.println("Controller: Password too short! Not saving.");
            return;
        }

        // 3. Update the Model
        model.saveUser(name);
    }
}
