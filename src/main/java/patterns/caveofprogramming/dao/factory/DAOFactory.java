package patterns.caveofprogramming.dao.factory;

import patterns.caveofprogramming.bean.Bean;
import patterns.caveofprogramming.dao.basic.UserDAO;

import java.util.List;
import java.util.Optional;

/**
 * The Factory Pattern is used here to hide the complexity of DAO instantiation.
 * The business logic shouldn't know how to create a DAO;
 * it should just ask the Factory for one.
 * <p>
 * Abstract Factory class that defines which DAOs the system can produce.
 * This decouples the client from the specific database implementation.
 */
public abstract class DAOFactory {
    public abstract UserDAO getUserDAO();

    /**
     * Factory method to get a specific factory type.
     * @param type The type of storage (e.g., "SQL", "MONGODB").
     * @return The corresponding DAOFactory.
     */
    public static DAOFactory getFactory(String type) {
        if (type.equalsIgnoreCase("SQL")) {
            return new SqlDAOFactory();
        }
        // Additional factories can be added here
        return null;
    }
}

/**
 * Concrete implementation of the factory for SQL-based storage.
 */
class SqlDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new UserDAO() {
            @Override
            public void save(Bean user) {
                System.out.println("Save user");
            }

            @Override
            public Optional<Bean> getById(int id) {
                System.out.println("Get user by id");
                return Optional.empty();
            }

            @Override
            public List<Bean> getAll() {
                System.out.println("Get all users");
                return List.of();
            }

            @Override
            public void update(Bean user) {
                System.out.println("Update user");
            }

            @Override
            public void delete(int id) {
                System.out.println("Delete user by id");
            }
        }; // Returns the SQL implementation
    }
}