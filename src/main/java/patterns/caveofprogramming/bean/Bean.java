package patterns.caveofprogramming.bean;

import java.io.Serializable;

/**
 * Represents a standard JavaBean entity.
 * * Conventions followed:
 * 1. Must have a public, no-argument constructor.
 * 2. Properties must be private.
 * 3. Properties are accessed via public "getter" and "setter" methods.
 * 4. (Recommended) Should implement Serializable for persistent storage.
 */
public class Bean implements Serializable {

    // The serialVersionUID is recommended when implementing Serializable
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String password;

    /**
     * Default no-arg constructor.
     * Required so frameworks can instantiate the class using reflection.
     */
    public Bean() {
    }

    /**
     * Standard getter for id.
     * @return the unique identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Standard setter for id.
     * @param id the unique identifier to set.
     */
    public void setId(int id) {
        this.id = id;
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

// Best Practices & Recommendations
/**
 * 1. Implement equals() and hashCode()
 * If you plan to put these beans into a Set or use them as keys in a Map, you must override these methods.
 * Without them, Java compares memory addresses rather than the actual data (id, name, etc.).
 * <p>
 * 2. Override toString()
 * Adding a toString() method makes debugging much easier, as it allows you to see the actual values
 * of the object in the console or logs rather than the class name and hash code (e.g., Bean@74a14482).
 * <p>
 * 3. Consider Java Records (JDK 16+)
 * If your Bean is purely used to carry data and doesn't need to change (immutability), use a Record.
 * It generates the constructor, getters, equals, hashCode, and toString automatically in one line:
 */
record UserRecord(int id, String name, String password) {}

/*
 * 4. Use Lombok to Reduce Boilerplate
 * In professional environments, we rarely write getters and setters manually. We use the Lombok library
 * to keep the code clean:
 * * @Data // Generates getters, setters, toString, equals, and hashCode
 * * @NoArgsConstructor
 * * public class Bean {
 * *    private int id;
 * *    private String name;
 * *    private String password;
 * * }
 */
