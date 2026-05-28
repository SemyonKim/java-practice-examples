# Chapter 14 - Implementing data persistence with Spring Data

## 1. The Core Problem & The Spring Data Solution
Without Spring Data, developers must learn different APIs, libraries, and abstractions for every database technology (e.g., pure JDBC, Hibernate, MongoDB). Spring Data solves this by providing a **unified abstraction layer** over all persistence technologies.

* **Less Code:** You write simple interfaces, and Spring Data generates the implementation at runtime.
* **Modular:** You only import the specific dependency you need (e.g., `spring-data-jdbc`, `spring-data-jpa`, `spring-data-mongodb`).

```mermaid
graph TD
    classDef spring fill:#6db33f,stroke:#2e7d32,stroke-width:2px,color:#fff;
    classDef tech fill:#e2e3e5,stroke:#383d41,stroke-width:2px;
    classDef db fill:#cce5ff,stroke:#004085,stroke-width:2px;

    App[Your Spring Boot App] --> SD[Spring Data Abstraction Layer]:::spring
    
    SD --> S_JDBC[Spring Data JDBC]:::spring
    SD --> S_JPA[Spring Data JPA]:::spring
    SD --> S_Mongo[Spring Data Mongo]:::spring
    
    S_JDBC --> JDBC[JDBC API]:::tech
    S_JPA --> Hibernate[Hibernate / ORM]:::tech
    S_Mongo --> MongoDriver[Mongo Driver]:::tech
    
    JDBC --> RelationalDB[(Relational DB)]:::db
    Hibernate --> RelationalDB
    MongoDriver --> NoSQL[(MongoDB)]:::db
```

---

## 2. The Repository Interface Hierarchy
Spring Data utilizes **interface segregation**. Instead of one massive contract, it breaks operations down into a hierarchy. You simply extend the interface that matches your app's needs.

* **`Repository<T, ID>`:** A marker interface. It provides no operations by default.
* **`CrudRepository<T, ID>`:** Adds basic CREATE, READ, UPDATE, and DELETE operations.
* **`PagingAndSortingRepository<T, ID>`:** Adds capabilities to fetch data in chunks (pages) and sort results.
* **Tech-Specific Repositories:** Interfaces like `JpaRepository` or `MongoRepository` add operations unique to their underlying technologies.

```mermaid
classDiagram
    direction BT
    class Repository {
        <<Marker_Interface>>
    }
    class CrudRepository {
        +save(entity)
        +findById(id)
        +findAll()
        +deleteById(id)
    }
    class PagingAndSortingRepository {
        +findAll(Sort)
        +findAll(Pageable)
    }
    class JpaRepository {
        +flush()
        +saveAllAndFlush()
    }
    class MongoRepository {
        +insert(entity)
    }

    CrudRepository --|> Repository
    PagingAndSortingRepository --|> CrudRepository
    JpaRepository --|> PagingAndSortingRepository
    MongoRepository --|> PagingAndSortingRepository
```

---

## 3. Defining Custom Operations
When basic CRUD isn't enough, you can define custom repository operations. Spring Data offers two ways to do this:

### A. Method Name Translation (The Magic Way)
Spring Data parses your method name based on specific rules and generates the SQL query automatically.

```mermaid
graph LR
    classDef syntax fill:#fff3cd,stroke:#856404,stroke-width:2px;
    classDef sql fill:#d4edda,stroke:#28a745,stroke-width:2px;
    
    Method["findAccountsByName(String name)"]:::syntax --> Parser{Spring Data Parser}
    Parser -->|Translates to| Query["SELECT * FROM account WHERE name = ?"]:::sql
```

### B. The `@Query` Annotation (The Recommended Way)
Relying on method names can lead to extremely long names for complex queries, performance hits during startup, and accidental breakage if refactored. It is highly recommended to explicitly write your queries using `@Query`.

| Feature                | Method Name Translation               | `@Query` Annotation        |
|:-----------------------|:--------------------------------------|:---------------------------|
| **Readability**        | Poor for complex queries              | Clean and explicit         |
| **Performance**        | Slower app startup (parsing overhead) | Faster                     |
| **Safety**             | Fragile against IDE refactoring       | Stable against refactoring |
| **Data Modifications** | Not supported for updates             | Handled via `@Modifying`   |

---

## 4. Standard Spring Data JDBC Architecture
To implement this in a real app, you define a model class with an `@Id`, extend a Repository interface, and inject it into your `@Transactional` service layer.

```mermaid
sequenceDiagram
    actor Client
    participant Controller as AccountController
    participant Service as TransferService (@Transactional)
    participant Repo as AccountRepository (Interface)
    participant Spring as Spring Data Framework
    participant DB as Database

    Client->>Controller: POST /transfer
    Controller->>Service: transferMoney(idSender, idReceiver, amount)
    
    activate Service
    Service->>Repo: findById(idSender)
    Repo->>Spring: Intercept method call
    Spring->>DB: Execute SELECT Query
    DB-->>Service: Return Account Object
    
    Service->>Repo: changeAmount(idSender, newAmount)
    note right of Repo: Requires @Modifying & @Query annotations
    Repo->>Spring: Intercept method call
    Spring->>DB: Execute UPDATE Query
    
    Service-->>Controller: Transaction Commits
    deactivate Service
    
    Controller-->>Client: 200 OK
```

### Repository Implementation Example
```java
public interface AccountRepository extends CrudRepository<Account, Long> {
    
    // Explicit query for fetching records
    @Query("SELECT * FROM account WHERE name = :name")
    List<Account> findAccountsByName(String name);
    
    // Explicit query for modifying records (requires @Modifying)
    @Modifying
    @Query("UPDATE account SET amount = :amount WHERE id = :id")
    void changeAmount(long id, BigDecimal amount);
}
```