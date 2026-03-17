# Facade - Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](FacadeDemonstration.java)**

## Intent & Core Problem
Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.

**☕ Java Note:**
> In modern Java architectures (like microservices or modular monoliths), Facades are critical. They act as the entry points to distinct bounded contexts or modules, hiding complex orchestration logic, database transactions, and deeply nested dependencies from the caller.

---

## Motivation & Real-World Analogy
Structuring a system into subsystems helps reduce complexity. A common design goal is to minimize the communication and dependencies between subsystems. One way to achieve this goal is to introduce a facade object that provides a single, simplified interface to the more general facilities of a subsystem.

**Clients Interacting Directly vs. Through a Facade**
```text
  Direct Coupling (Messy)                         Facade Pattern (Clean)
  
  Clients       Subsystem Classes                 Clients        Facade        Subsystem Classes
 ┌────────┐        ┌─────────┐                   ┌────────┐                       ┌─────────┐
 │ Client │───. .─>│ Class A │                   │ Client │───┐                   │ Class A │
 └────────┘    X   └─────────┘                   └────────┘   │   ┌──────────┐ ┌─>└─────────┘
              / \  ┌─────────┐                                └──>│          │─┘  ┌─────────┐
 ┌────────┐  /   ─>│ Class B │                   ┌────────┐       │          │───>│ Class B │
 │ Client │─´ .───>└─────────┘                   │ Client │──────>│  Facade  │    └─────────┘
 └────────┘  /     ┌─────────┐                   └────────┘       │          │─┐  ┌─────────┐
            / .───>│ Class C │                                ┌──>│          │ └─>│ Class C │
 ┌────────┐/ /     └─────────┘                   ┌────────┐   │   └──────────┘    └─────────┘
 │ Client │─´                                    │ Client │───┘
 └────────┘                                      └────────┘
```
*Description: The left side illustrates tightly coupled clients interacting directly with various subsystem classes, creating a tangled web of dependencies. The right side introduces a Facade, which acts as a central hub. Clients only interact with the Facade, and the Facade manages the routing to the subsystem classes.*

Consider a programming environment that gives applications access to its compiler subsystem. This subsystem contains classes such as `Scanner`, `Parser`, `ProgramNode`, `BytecodeStream`, and `ProgramNodeBuilder` that implement the compiler. Some specialized applications might need to access these classes directly. But most clients of a compiler generally don't care about details like parsing and code generation; they merely want to compile some code. For them, the powerful but low-level interfaces only complicate their task.

To provide a higher-level interface that shields clients from these classes, the compiler subsystem introduces a `Compiler` class. This class defines a unified interface to the compiler's functionality. The `Compiler` class acts as a facade: It glues together the classes that implement compiler functionality without hiding them entirely.

**Compiler Subsystem Facade**
```text
                 ┌───────────────────────────────────────────────────────────┐
                 │                     Compiler Subsystem                    │
                 │                                                           │
 ┌────────┐      │  ┌──────────┐     ┌──────────────────┐     ┌─────────────┐│
 │ Client │────────>│ Compiler │────>│ProgramNodeBuilder│---->│ ProgramNode ││
 └────────┘      │  └──────────┘     └──────────────────┘     └─────────────┘│
                 │   │  │    │                                         │     │
                 │   │  │    v                                         │     │
                 │   │  │ ┌─────────┐    ┌─────────┐                   │     │
                 │   │  │ │ Scanner │--->│  Token  │<------------------┘     │
                 │   │  │ └─────────┘    └─────────┘                   │     │
                 │   │  │  ┌────────┐     ┌────────┐                   │     │
                 │   │  └─>│ Parser │     │ Symbol │<------------------┘     │
                 │   │     └────────┘     └────────┘                         │
                 │   │  ┌───────────────┐    ┌────────────────┐              │
                 │   └─>│ CodeGenerator │--->│ BytecodeStream │              │
                 │      └───────────────┘    └────────────────┘              │ 
                 └───────────────────────────────────────────────────────────┘
```
*Description: The `Compiler` class provides a single point of entry for the `Client`. Internally, the `Compiler` orchestrates the `Scanner`, `Parser`, `ProgramNodeBuilder`, `ProgramNode`, and `BytecodeStream` to produce the final executable, abstracting this pipeline away from the caller.*

---

## Applicability
Use the Facade pattern when:
- **You want to provide a simple interface to a complex subsystem.** Subsystems often get more complex as they evolve. Most patterns result in more and smaller classes. This makes the subsystem more reusable but harder to use for clients that don't need customization. A facade provides a default view of the subsystem that is good enough for most clients.
- **There are many dependencies between clients and the implementation classes of an abstraction.** Introduce a facade to decouple the subsystem from clients and other subsystems, promoting subsystem independence and portability.
- **You want to layer your subsystems.** Use a facade to define an entry point to each subsystem level. If subsystems are dependent, you can simplify dependencies by forcing them to communicate only through their facades.

---

## Structure & Participants

**Common Facade Structure**
```text
 ┌────────┐
 │ Client │
 └────────┘
     │
     │       ┌─────────────────┐
     └──────>│     Facade      │
             └─────────────────┘
               │      │      │
     ┌─────────┘      │      └──────────┐
     │                │                 │
     v                v                 v
 ┌─────────┐     ┌─────────┐       ┌─────────┐
 │ SubsysA │     │ SubsysB │       │ SubsysC │
 └─────────┘     └─────────┘       └─────────┘
     │                │                 │
     └────────────────┼─────────────────┘
                      v
                 ┌─────────┐
                 │ SubsysD │
                 └─────────┘
```
*Description: The generic UML representation. A `Client` invokes methods on the `Facade`. The `Facade` translates these requests and forwards them to the appropriate subsystem classes (`SubsysA`, `SubsysB`, etc.), which may also communicate amongst themselves to fulfill the request.*

### Participants
- **Facade** (`Compiler`):
  - Knows which subsystem classes are responsible for a request.
  - Delegates client requests to appropriate subsystem objects
- **Subsystem classes** (`Scanner`, `Parser`, `ProgramNode`, etc.):
  - Implement subsystem functionality. 
  - Handle work assigned by the Facade object. 
  - Have no knowledge of the facade; they keep no references to it.

---

## Collaborations
- Clients communicate with the subsystem by sending requests to Facade, which forwards them to the appropriate subsystem object(s). The facade may have to translate its interface to subsystem interfaces.
- Clients that use the facade don't have to access its subsystem objects directly.

---

## Consequences
The Facade pattern offers the following benefits:
1. **Shields clients from subsystem components:** This reduces the number of objects clients deal with and makes the subsystem easier to use.
2. **Promotes weak coupling:** Weak coupling lets you vary the components of the subsystem without affecting its clients. It also avoids complex circular dependencies.
3. **Doesn't prevent direct access:** It doesn't restrict applications from using subsystem classes directly if they need sophisticated, low-level control.

---

## Implementation Hints & Modern Java Context

1. **Reducing Client-Subsystem Coupling:** You can make the coupling between clients and the subsystem even weaker by making the Facade an `interface` or an `abstract class`. Concrete subclasses can then implement different ways of interacting with the subsystem. In modern Java, this is easily achieved using interfaces and dependency injection.
2. **Public vs. Private Subsystem Classes:** A subsystem is akin to a module. In Java 9+, the module system (`module-info.java`) or package-private visibility allows you to strictly enforce the Facade. You can make the Facade `public` while keeping the subsystem classes package-private or hidden within the module, physically preventing clients from bypassing the Facade.

---

## Known Uses (Historical & Modern Java)

**Historical OS Example (Domain)**  

In older operating system frameworks, a `Domain` class often acted as a facade for memory management subsystems.

**Domain Facade Interaction**
```text
 ┌─────────┐          ┌───────────────┐            ┌──────────────┐
 │ Process │─────────>│    Domain     │-----┐----->│ MemoryObject │
 └─────────┘          +───────────────+     │      +──────────────+       ┌───────────────────┐
                      │ repairFault() │     │      │ buildCache() │------>│ MemoryObjectCache │
                      └───────────────┘     │      └──────────────┘       └───────────────────┘
                                            │
                                            │
                                            │      ┌─────────────────────┐                    
                                            └----->│ AddressTranslation  │
                                                   +─────────────────────+         
                                                   │ findMemory(Address) │         
                                                   └─────────────────────┘         
```
*Description: The `Domain` acts as a facade, calling `repairFault()` operations when page faults occur. The Domain finds the memory object at the address causing the fault and delegates the `repairFault()` operation to the cache associated with that memory object.*

**Modern Java Uses**
- **Spring Framework (`JdbcTemplate`):** The `JdbcTemplate` class is an excellent example of a Facade. It hides the immense boilerplate of pure JDBC (acquiring connections, creating statements, handling `ResultSet` iteration, catching `SQLException`, and closing resources) behind a simple interface.
- **SLF4J (Simple Logging Facade for Java):** As the name implies, it acts as a facade for various underlying logging frameworks (Logback, Log4j, etc.), allowing the end-user to log without worrying about the complex configurations of the specific backend.
- **Microservices (API Gateway):** In cloud architectures, an API Gateway acts as a Facade for a cluster of microservices, hiding the internal network structure from external clients and handling cross-cutting concerns like authentication and routing.

---

## Related Patterns
- **Abstract Factory** can be used with Facade to provide an interface for creating subsystem objects in a subsystem-independent way. It can also act as an alternative to hide platform-specific classes.
- **Mediator** abstracts functionality of existing classes, but its purpose is to abstract arbitrary communication between colleague objects (centralizing functionality). A facade merely abstracts the interface to subsystem objects to make them easier to use; it doesn't define new functionality, and subsystem classes don't know about it.
- Usually, only one Facade object is required. Thus, Facades are often implemented as **Singletons**.