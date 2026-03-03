package patterns.caveofprogramming.dao.multidb;

import patterns.caveofprogramming.bean.Bean;
import patterns.caveofprogramming.dao.basic.UserDAO;
import patterns.caveofprogramming.dao.factory.DAOFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL-specific implementation of the UserDAO.
 */
class MySqlUserDAO implements UserDAO {
    @Override
    public void save(Bean user) {
        System.out.println("Running: INSERT INTO mysql_users ...");
    }
    // ... other CRUD methods implemented with MySQL syntax
    public Optional<Bean> getById(int id) { return Optional.empty(); }
    public List<Bean> getAll() { return new ArrayList<>(); }
    public void update(Bean user) {}
    public void delete(int id) {}
}

/**
 * Oracle-specific implementation of the UserDAO.
 */
class OracleUserDAO implements UserDAO {
    @Override
    public void save(Bean user) {
        System.out.println("Running: INSERT INTO oracle_schema.users ...");
    }
    // ... other CRUD methods implemented with Oracle syntax
    public Optional<Bean> getById(int id) { return Optional.empty(); }
    public List<Bean> getAll() { return new ArrayList<>(); }
    public void update(Bean user) {}
    public void delete(int id) {}
}

/**
 * In this phase, we demonstrate how to support MySQL and Oracle using the same interface.
 * This is where the pattern truly shines: the "Client" code never changes, even
 * if the backend database does.
 * <p>
 * The ultimate "Switchboard" Factory.
 */
public class MultiDbFactory extends DAOFactory {
    private String dbType;

    public MultiDbFactory(String dbType) {
        this.dbType = dbType;
    }

    @Override
    public UserDAO getUserDAO() {
        return switch (dbType.toUpperCase()) {
            case "MYSQL" -> new MySqlUserDAO();
            case "ORACLE" -> new OracleUserDAO();
            default -> throw new IllegalArgumentException("Unknown DB type");
        };
    }
}