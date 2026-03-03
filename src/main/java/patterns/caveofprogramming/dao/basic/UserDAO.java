package patterns.caveofprogramming.dao.basic;

import patterns.caveofprogramming.bean.Bean;

import java.util.List;
import java.util.Optional;

/**
 * This project focuses on the fundamental Data Access Object pattern
 * to separate the persistence logic from the business logic
 * <p>
 * The UserDAO interface defines the standard CRUD (Create, Read, Update, Delete)
 * operations for the User entity.
 */
public interface UserDAO {
    /** @param user The user to persist in the database. */
    void save(Bean user);

    /**
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the user if found.
     */
    Optional<Bean> getById(int id);

    /** @return A list of all users in the storage. */
    List<Bean> getAll();

    /** @param user The user with updated data. */
    void update(Bean user);

    /** @param id The ID of the user to remove. */
    void delete(int id);
}