# Chapter 12 - Using data sources in Spring apps

### 1. Connection Pooling (DataSource)
```mermaid
graph LR
    App[Application Thread] -->|1. Borrow| DS(DataSource Pool)
    DS -->|2. Provide| Conn((Active Connection))
    Conn -->|3. Query| DB[(Database)]
    App -.->|"4. Call .close()"| DS
    DS -.->|5. Keep Alive & Recycle| Conn

    style DS fill:#f9f,stroke:#333
    style Conn fill:#bbf,stroke:#333
    style DB fill:#d4f7dc,stroke:#333
```

---

### 2. JDBC SPI Architecture
```mermaid
classDiagram
    class DataSource { <<interface>> }
    class Connection { <<interface>> }
    class HikariCP { <<Spring_Default_Implementation>> }
    class VendorDriver { <<MySQL_H2_Postgres>> }
    
    DataSource <|.. HikariCP
    HikariCP --> VendorDriver : Uses at Runtime
    DataSource ..> Connection : Produces
```

---

### 3. JdbcTemplate Execution Flow
```mermaid
sequenceDiagram
    participant Repo as Application
    participant JT as JdbcTemplate
    participant Pool as Connection Pool
    participant DB as Database

    Repo ->> JT: query(sql, RowMapper)
    activate JT
    
    JT ->> Pool: getConnection()
    Pool -->> JT: Connection Proxy
    
    JT ->> DB: executeQuery(sql)
    activate DB
    DB -->> JT: ResultSet
    deactivate DB
    
    JT ->> JT: Map rows to Java Objects
    JT ->> Pool: releaseConnection() (Auto-cleanup)
    
    alt Success
        JT -->> Repo: List<Object>
    else Error
        JT -->> Repo: Throws DataAccessException
    end
    deactivate JT
```

---

### 4. Spring Boot Auto-Configuration Logic
```mermaid
flowchart TD
    Start(Application Startup) --> CustomBean{"Custom DataSource @Bean?"}
    
    CustomBean -- Yes --> Backoff[Auto-Config Backs Off]
    CustomBean -- No --> Deps{JDBC Driver on Classpath?}
    
    Deps -- Yes --> Props[Read application.properties]
    Props --> AutoDS[Auto-create HikariDataSource]
    AutoDS --> AutoJT[Auto-create JdbcTemplate]
    
    Deps -- No --> Skip[Skip Database Setup]
    
    style CustomBean fill:#ffccd5,stroke:#333
    style Deps fill:#ffccd5,stroke:#333
    style AutoDS fill:#b5ead7,stroke:#333
```

---

### 5. Data Mapping
```mermaid
erDiagram
    DATABASE_TABLE {
        INT id PK "AUTO_INCREMENT"
        VARCHAR product 
        DECIMAL price 
    }
    
    JAVA_OBJECT {
        int id
        String product
        BigDecimal price
    }
    
    JAVA_OBJECT ||--|| DATABASE_TABLE : "JdbcTemplate RowMapper"
```