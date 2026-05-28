# Chapter 15 - Testing your Spring app

## 1. The Purpose of Testing & CI/CD
Tests are short pieces of logic validating that your app works as expected. They are essential for **regression testing** (ensuring new changes don't break existing features) and act as living documentation. In modern development, tests are automated via a Continuous Integration (CI) pipeline.

```mermaid
graph LR
    classDef actor fill:#f8f9fa,stroke:#6c757d,stroke-width:2px;
    classDef process fill:#cce5ff,stroke:#004085,stroke-width:2px;
    classDef pass fill:#d4edda,stroke:#28a745,stroke-width:2px;
    classDef fail fill:#f8d7da,stroke:#dc3545,stroke-width:2px;

    Dev((Developer)):::actor -->|Commits Code| Repo[Source Control]:::process
    Repo --> CI[CI Tool / Jenkins]:::process
    CI --> Build[Build App]:::process
    Build --> Test[Run Automated Tests]:::process
    
    Test -->|Fail| Alert[Notify Developer of Broken Logic]:::fail
    Test -->|Pass| Success[Ready for Production]:::pass
```

---

## 2. The Anatomy of a Test
Whether unit or integration, every correctly implemented test follows a strict three-step lifecycle (often referred to as Arrange-Act-Assert or Given-When-Then).

```mermaid
stateDiagram-v2
    direction LR
    
    state "1. Assumptions (Arrange)" as Step1
    state "2. Call/Execution (Act)" as Step2
    state "3. Validations (Assert)" as Step3
    
    Step1 : Define inputs & control mock behavior
    Step2 : Invoke the specific method being tested
    Step3 : Verify return values & mock interactions
    
    Step1 --> Step2
    Step2 --> Step3
```

---

## 3. Unit Tests vs. Spring Integration Tests
Spring applications primarily rely on two layers of testing.

* **Unit Tests:** Lightning-fast, strictly isolated. They test *only* the business logic of a single class. All external dependencies (like databases or other services) are replaced with **Mocks** (fake objects you control).
* **Integration Tests:** Slower, context-aware. They test how multiple real components interact with each other and with the Spring Framework (e.g., verifying `@Transactional` or database queries work).

```mermaid
graph TD
    classDef test fill:#e2e3e5,stroke:#383d41,stroke-width:2px;
    classDef target fill:#cce5ff,stroke:#004085,stroke-width:2px;
    classDef mock fill:#fff3cd,stroke:#856404,stroke-width:2px,stroke-dasharray: 5 5;
    classDef real fill:#d4edda,stroke:#28a745,stroke-width:2px;

    subgraph "Unit Testing (Strict Isolation)"
        UT[Unit Test]:::test -->|Calls| Svc1[TransferService]:::target
        Svc1 -.->|Fake Interaction| MockRepo[Mocked AccountRepository]:::mock
    end

    subgraph "Spring Integration Testing (Context Loaded)"
        IT[Integration Test]:::test -->|Calls| Svc2[TransferService]:::target
        Svc2 -->|Real Interaction| RealRepo[Real AccountRepository]:::real
        RealRepo -->|SQL Queries| DB[(H2 In-Memory DB)]:::real
    end
```

---

## 4. Key Testing Annotations
Using the right annotations dictates whether you are spinning up a lightweight unit test or a heavy Spring context integration test.

| Annotation                            | Test Type   | Purpose                                                                                                                                 |
|:--------------------------------------|:------------|:----------------------------------------------------------------------------------------------------------------------------------------|
| `@ExtendWith(MockitoExtension.class)` | Unit        | Enables Mockito annotations in the test class without loading Spring.                                                                   |
| `@Mock`                               | Unit        | Creates a fake, controllable object (a mock) of a dependency.                                                                           |
| `@InjectMocks`                        | Unit        | Creates the actual object you are testing and automatically injects the `@Mock` dependencies into it.                                   |
| `@SpringBootTest`                     | Integration | Boots up the full Spring Application Context for the test.                                                                              |
| `@Autowired`                          | Integration | Injects a *real* bean from the Spring Context into the test.                                                                            |
| `@MockBean`                           | Integration | Replaces a real bean in the Spring Context with a mock. Useful if you want to test context integration but still skip a specific layer. |

---

## 5. Happy Paths vs. Exception Flows
A robust test suite covers both successful executions and expected failures.

### The Exception Flow (Testing for Errors)
You must verify that your application throws the correct exceptions and halts execution when faced with invalid states (e.g., trying to transfer money from an account that doesn't exist).

```mermaid
sequenceDiagram
    actor Test
    participant Service as TransferService
    participant Mock as MockRepository

    Test->>Mock: given(findById(99)).willReturn(Optional.empty())
    Note right of Test: 1. Assumptions (Arrange)
    
    Test->>Service: assertThrows(AccountNotFoundException.class)
    Note right of Test: 2 & 3. Act & Assert simultaneously
    
    activate Service
    Service->>Mock: findById(99)
    Mock-->>Service: Empty Result
    Service-->>Test: Throws AccountNotFoundException
    deactivate Service
    
    Test->>Mock: verify(changeAmount).never()
    Note right of Test: Verify database was never updated
```