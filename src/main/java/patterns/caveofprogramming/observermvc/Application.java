package patterns.caveofprogramming.observermvc;


import patterns.caveofprogramming.observermvc.controller.Controller;
import patterns.caveofprogramming.observermvc.model.Model;
import patterns.caveofprogramming.observermvc.view.View;

import javax.swing.*;

/**
 * Main application class.
 * This is responsible for "Dependency Injection": creating the components
 * and linking them together.
 */
public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Application::runApp);
    }

    private static void runApp(){
        Model model = new Model();
        View view = new View(model);
        Controller controller = new Controller(model, view);

        // The critical step: Connecting the Controller as an Observer of the View
        view.setLoginListener(controller);
    }
}
