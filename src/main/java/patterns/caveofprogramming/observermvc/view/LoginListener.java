package patterns.caveofprogramming.observermvc.view;

/**
 * The Observer Interface.
 * Any class that wants to respond to login events (the Controller) must implement this.
 */
public interface LoginListener {
    void loginPerformed(LoginFormEvent event);
}