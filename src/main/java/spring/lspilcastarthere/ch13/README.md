# Chapter 13 - Using transactions in Spring apps

## 1. The Core Problem: Data Inconsistency
When an app executes multiple mutable operations (e.g., transferring money), partial execution leads to corrupted data. If a withdrawal succeeds but the deposit fails, money is lost from the system.

```mermaid
graph TD
    classDef success fill:#d4edda,stroke:#28a745,stroke-width:2px;
    classDef error fill:#f8d7da,stroke:#dc3545,stroke-width:2px;
    
    A[Transfer $100] --> B(Step 1: Subtract $100 from John)
    B -->|Success| C(Step 2: Add $100 to Jane)
    C -->|Fails!| D[Inconsistent State: John is missing $100, Jane never got it]:::error
```

---

## 2. The Solution: Transactions & Atomicity
A transaction ensures **atomicity**—operations either execute altogether or not at all.
* **Commit:** If all steps succeed, changes are permanently stored.
* **Rollback:** If any step fails, data reverts exactly to how it was before starting.

```mermaid
stateDiagram-v2
    [*] --> Transaction_Starts : Begin
    
    state "Mutable Operations" as Ops 
    state Ops {
        Step1 : Withdraw
        Step2 : Deposit
        Step1 --> Step2
    }
    
    Transaction_Starts --> Ops
    Ops --> COMMIT : All steps successful
    Ops --> ROLLBACK : Exception occurs
    
    COMMIT --> [*] : Changes Persisted
    ROLLBACK --> [*] : Changes Reverted (Data Restored)
```

---

## 3. How Spring Manages Transactions (AOP)
Declaring a transaction in Spring is done via the `@Transactional` annotation. Spring uses an **Aspect** behind the scenes to intercept the method call and wrap it in transaction logic.

```mermaid
sequenceDiagram
    actor Client
    participant Aspect as Spring Transaction Aspect
    participant Service as Service Method (@Transactional)
    
    Client->>Aspect: Calls method
    activate Aspect
    Aspect->>Aspect: Starts Transaction
    Aspect->>Service: Forwards call
    activate Service
    Service-->>Aspect: Returns successfully
    deactivate Service
    Aspect->>Aspect: COMMITS Transaction
    Aspect-->>Client: Returns result
    deactivate Aspect
```

---

## 4. The Rules of Exception Handling
Spring relies entirely on exceptions to know when to roll back.
1. **Uncaught Runtime Exceptions:** Trigger an automatic rollback.
2. **Swallowed Exceptions:** If you catch an exception inside your method using `try-catch` and don't throw it further, Spring won't see it, and will accidentally commit.
3. **Checked Exceptions:** By default, Spring **does not** roll back for checked exceptions (like `SQLException`), as they are expected to be handled by the developer.

```mermaid
graph TD
    classDef aspect fill:#e2e3e5,stroke:#383d41,stroke-width:2px;
    classDef commit fill:#d4edda,stroke:#28a745,stroke-width:2px;
    classDef rollback fill:#f8d7da,stroke:#dc3545,stroke-width:2px;

    Exception[Exception occurs in Method] --> Caught{Is it caught inside the method?}
    
    Caught -->|Yes| Hidden[Exception is swallowed]
    Hidden --> Commit1[Aspect doesn't know -> COMMITS]:::commit
    
    Caught -->|No| Thrown[Exception is thrown further]
    Thrown --> Intercept[Aspect intercepts exception]:::aspect
    
    Intercept --> CheckType{What type of Exception?}
    CheckType -->|RuntimeException| ActionRB[Aspect -> ROLLS BACK]:::rollback
    CheckType -->|Checked Exception| ActionCM[Aspect -> COMMITS 'Default']:::commit
```

---

## 5. Standard Application Architecture
In a standard Spring setup, business logic sits in the Service layer, which uses Repositories for data access. The `@Transactional` annotation is placed on the Service method to encompass all downstream database operations.

```mermaid
graph LR
    classDef component fill:#cce5ff,stroke:#004085,stroke-width:2px;
    classDef tx fill:#fff3cd,stroke:#856404,stroke-width:2px;
    classDef db fill:#e2e3e5,stroke:#383d41,stroke-width:2px;

    REST[REST Endpoint] -->|1. Request| Ctrl[Controller]:::component
    Ctrl -->|2. Calls Service| Svc["Service Layer<br>(@Transactional)"]:::tx
    Svc -->|3. Queries| Repo[Repository]:::component
    Repo -->|4. SQL| DB[(Database)]:::db
```