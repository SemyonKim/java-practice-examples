package patterns.caveofprogramming.observermvc.view;

/**
 * An Event Object used to encapsulate data passed from the View to the Observer.
 * This decouples the View's internal UI components (like JTextFields) from the listener.
 */
public class LoginFormEvent {
    private String name;
    private String password;

    public LoginFormEvent(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}