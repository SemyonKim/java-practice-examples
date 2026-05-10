# Java Concurrency in Practice: Summary of Part II

## Structuring Concurrent Applications (Chapters 6-9)

Part II transitions from the low-level primitives of thread safety (Part I) to the architectural patterns needed to build robust, scalable concurrent applications. It focuses on task abstraction, lifecycle management, and execution policies.

## The Architectural Flow (Part II Ecosystem)

```mermaid
graph TD
    classDef main fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef executor fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;
    classDef cancel fill:#ffebee,stroke:#d32f2f,stroke-width:2px;
    classDef gui fill:#fff3e0,stroke:#f57c00,stroke-width:2px;

    subgraph Ch6["Chapter 6: Task Execution"]
        T1[Runnable/Callable Tasks]:::main --> Exec[Executor Framework]:::executor
        Exec --> Decouple[Decouples Submission<br/>from Execution]:::executor
    end

    subgraph Ch8["Chapter 8: Applying Thread Pools"]
        Exec --> Config[ThreadPoolExecutor<br/>Tuning & Sizing]:::executor
        Config --> Sat[Saturation Policies<br/>Abort, CallerRuns]:::executor
        Config --> Deadlock[Avoid Thread Starvation<br/>Deadlock]:::executor
    end

    subgraph Ch7["Chapter 7: Cancellation & Shutdown"]
        Exec --> Lifecycle[ExecutorService<br/>Lifecycle]:::cancel
        T1 -.-> Interrupt["Thread Interruption<br/>Thread.currentThread().interrupt()"]:::cancel
        Lifecycle --> Await[awaitTermination / shutdownNow]:::cancel
    end

    subgraph Ch9["Chapter 9: GUI Applications"]
        GUI[Single-Threaded UI<br/>Event Dispatch Thread]:::gui
        GUI -.-> |Spawns| T1
        Exec -.-> |Posts Results via<br/>invokeLater| GUI
    end
```

## Chapter 6: Task Execution Core Concepts

Chapter 6 teaches us to stop managing raw `Thread` objects (`new Thread(r).start()`) and instead think in terms of logical **Tasks** mapped to an **Execution Policy**.

```mermaid
graph LR
    subgraph Submission
        P[Producer] -->|Submits Callable| E(ExecutorService)
    end
    
    subgraph Execution Policy
        E -->|Manages| Q[(Work Queue)]
        Q -->|Polled by| T1[Worker Thread 1]
        Q -->|Polled by| T2[Worker Thread 2]
    end
    
    subgraph Result
        T1 -->|Returns| F[Future Object]
        P -.->|"Calls get()"| F
    end
    style E fill:#d4edda,stroke:#28a745
    style F fill:#cce5ff,stroke:#007bff
```

* **Key Takeaway:** `Executor` separates *what* is done from *how* and *when* it is done.
* **Modern Equivalent:** Replaced/Enhanced by `CompletableFuture` for non-blocking asynchronous pipelines, and `Executors.newVirtualThreadPerTaskExecutor()` in Java 21.

## Chapter 7: Cancellation and Shutdown

There is no safe way to preemptively stop a thread in Java (`Thread.stop()` is deprecated). Chapter 7 focuses on **Cooperative Interruption**.

```mermaid
sequenceDiagram
    participant Controller
    participant TargetThread
    
    Controller->>TargetThread: target.interrupt()
    Note over TargetThread: Interrupt Status Flag set to TRUE
    
    alt Thread is Blocking (sleep, wait, join)
        TargetThread-->>TargetThread: Throws InterruptedException
        Note over TargetThread: Flag is CLEARED!
        TargetThread->>TargetThread: Catch block must restore flag<br/>Thread.currentThread().interrupt()
    else Thread is Computing
        TargetThread->>TargetThread: Periodically checks<br/>Thread.interrupted()
        TargetThread-->>Controller: Exits gracefully
    end
```

* **Key Takeaway:** Interruption is a request, not a command. Code must be written to actively check for and respond to interrupt requests. Swallowing `InterruptedException` without restoring the interrupt status is a critical antipattern.

## Chapter 8: Applying Thread Pools

A deep dive into `ThreadPoolExecutor`. It exposes the implicit coupling between tasks and pools.

```mermaid
graph TD
    classDef pool fill:#fff,stroke:#333,stroke-width:2px;
    classDef policy fill:#ffe0b2,stroke:#ef6c00,stroke-width:2px;
    
    Task[Incoming Task] --> Core{Core Pool Full?}
    Core -- No --> CreateCore[Create New Thread]
    Core -- Yes --> Queue{Work Queue Full?}
    
    Queue -- No --> Enqueue[(Bounded Queue)]
    Queue -- Yes --> Max{Max Pool Full?}
    
    Max -- No --> CreateMax[Create Temp Thread]
    Max -- Yes --> Reject[Saturation Policy Handler]:::policy
    
    Reject --> Abort["AbortPolicy (Throws Exception)"]:::policy
    Reject --> Caller["CallerRunsPolicy (Throttles Producer)"]:::policy
    Reject --> Discard[DiscardOldestPolicy]:::policy
```

* **Key Takeaway:** Never use unbounded queues in production. Always configure core/max threads, keep-alive times, bounded queues, and a sensible Saturation Policy to prevent `OutOfMemoryError`.
* **Danger:** **Thread Starvation Deadlock**. Submitting tasks to a bounded pool that then submit *sub-tasks* to the exact same pool and wait for them to finish.

## Chapter 9: GUI Applications

Deals with the constraint that GUI frameworks are single-threaded to prevent deadlocks and data races in the view components.

```mermaid
graph LR
    subgraph "Event Dispatch Thread (EDT)"
        UI[UI Components]
        Loop[Event Loop]
    end
    
    subgraph Background Pools
        Net[Network Call]
        DB[Database Query]
    end
    
    UI -- "User Click (Blocks EDT if long)" --> Net
    Net -- "BAD: Updates UI directly" --> UI
    style UI stroke:#d32f2f,stroke-width:3px
    
    Net -- "GOOD: SwingUtilities.invokeLater" --> Loop
    Loop --> UI
    style Loop stroke:#388e3c,stroke-width:3px
```

* **Key Takeaway:** Long-running tasks must be strictly confined to background threads, but UI mutations must be strictly confined to the Event Dispatch Thread. Use `Future`s or callbacks to bridge the gap.