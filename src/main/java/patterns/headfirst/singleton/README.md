# Singleton Pattern

> 💡 **DESIGN Principles:**
> * Encapsulate what varies.
> * Favor composition over inheritance.
> * Program to interfaces, not implementations.
> * Strive for loosely coupled designs between objects that interact.
> * **Open Closed Principle:** Classes should be open for extension but closed for modification.
> * **Dependency Inversion Principle:** Depend on abstractions. Do not depend on concrete classes.

> 🧩 **DESIGN PATTERN:**
> 
> **Singleton Pattern:** The Singleton Pattern ensures a class has only one instance, and provides a global point of access to it.

---

### Phase 1: Naive/Initial State - Classic Lazy Instantiation

```mermaid
classDiagram
    class Singleton {
        -static Singleton uniqueInstance
        -Singleton()
        +static getInstance() Singleton
    }
    
    Singleton --> Singleton : "Manages and returns single instance"
    
    note for Singleton "getInstance() provides a global access point.\nLazy instantiation: only created when needed."
```

```java
public class Singleton {
    private static Singleton uniqueInstance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}
```

---

### Phase 2: Intermediate Evolution - The Multithreading Collision

```mermaid
sequenceDiagram
    participant Thread A
    participant Thread B
    participant Singleton Class
    
    Thread A->>Singleton Class: call getInstance()
    Note over Thread A,Singleton Class: Context Switch
    Thread B->>Singleton Class: call getInstance()
    
    Thread A->>Singleton Class: check if (uniqueInstance == null) -> TRUE
    Thread B->>Singleton Class: check if (uniqueInstance == null) -> TRUE
    
    Thread A->>Singleton Class: uniqueInstance = new Singleton()
    Thread B->>Singleton Class: uniqueInstance = new Singleton()
    
    Note over Thread A,Thread B: CRITICAL FAILURE: Two instances created!
```

---

### Phase 3: Intermediate Evolution - Synchronized (Thread-Safe, High Overhead)

```mermaid
flowchart LR
    Thread1[Thread 1] -->|Locks Method| Method[getInstance]
    Thread2[Thread 2] -.->|Waits| Method
    Thread3[Thread 3] -.->|Waits| Method
    Method -->|Returns| Instance[uniqueInstance]
    
    style Method fill:#f96,stroke:#333,stroke-width:2px
```

```java
public class Singleton {
    private static Singleton uniqueInstance;

    private Singleton() {}

    // Synchronized forces every thread to wait its turn.
    // Issue: Synchronization is only needed on the first pass, causing unnecessary runtime overhead.
    public static synchronized Singleton getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}
```

---

### Phase 4: Intermediate Evolution - Eager Instantiation (Thread-Safe, Low Overhead, Not Lazy)

```mermaid
flowchart TD
    JVM[JVM Class Loader] -->|Creates Instance Immediately| Instance[uniqueInstance = new Singleton]
    ThreadA[Thread A] -->|Requests| Method[getInstance]
    ThreadB[Thread B] -->|Requests| Method
    Method --> Instance
```

```java
public class Singleton {
    // Rely on JVM class loading to guarantee thread safety during initialization.
    private static Singleton uniqueInstance = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return uniqueInstance;
    }
}
```

---

### Phase 5: Final Pattern-Refined - Double-Checked Locking

```mermaid
flowchart TD
    Start[Call getInstance] --> Check1{Is uniqueInstance null?}
    Check1 -->|No| Return[Return uniqueInstance]
    Check1 -->|Yes| Lock[Enter synchronized block]
    Lock --> Check2{Is uniqueInstance null?}
    Check2 -->|No| Release1[Release Lock & Return]
    Check2 -->|Yes| Create[uniqueInstance = new Singleton]
    Create --> Release2[Release Lock & Return]
```

```java
public class Singleton {
    // Volatile ensures multiple threads handle the variable correctly during initialization.
    private volatile static Singleton uniqueInstance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (uniqueInstance == null) { 
            synchronized (Singleton.class) { 
                if (uniqueInstance == null) { 
                    uniqueInstance = new Singleton();
                }
            }
        }
        return uniqueInstance;
    }
}
```

---

### Phase 6: Modern Java Standard - Enum Singleton

```mermaid
classDiagram
    class Singleton {
        <<enumeration>>
        UNIQUE_INSTANCE
        +usefulMethod()
    }
    
    note for Singleton "JVM guarantees enum values are instantiated only once.\nSolves synchronization, class loading, reflection, and serialization issues natively."
```

```java
public enum Singleton {
    UNIQUE_INSTANCE

    // useful fields and methods here
}

public class SingletonClient {
    public static void main(String[] args) {
        Singleton singleton = Singleton.UNIQUE_INSTANCE;
    }
}
```