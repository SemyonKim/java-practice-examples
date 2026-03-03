package patterns.caveofprogramming.observermvc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Model represents the data/state of the application.
 * In the MVC pattern, the Model is independent of the View and Controller.
 * It usually notifies observers (the View or Controller) when its data changes.
 * <p>
 * The Model now acts as a simple in-memory database.
 * It provides methods to manipulate and store data.
 */
public class Model {
    // A list to simulate a database table
    private List<String> userList = new ArrayList<>();

    /**
     * Business logic method to save a user.
     * @param name The username to save.
     */
    public void saveUser(String name) {
        userList.add(name);
        System.out.println("Model: Saved user '" + name + "' to the database.");
        System.out.println("Model: Total users now: " + userList.size());
    }
}
