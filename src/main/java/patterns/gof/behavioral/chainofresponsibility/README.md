# Chain of Responsibility - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](ChainOfResponsibilityDemonstration.java)**

## Intent & Core Problem
Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. This pattern chains the receiving objects and passes the request along the chain until an object successfully handles it.

**☕ Java Note:**
> In modern Java, this pattern is fundamental for building extensible pipelines and middleware. It shifts the design from rigid `if-else` or `switch` blocks into a sequence of modular, single-responsibility components that can be configured dynamically at runtime.

---

## Motivation & Real-World Analogy
Consider a context-sensitive help system in a graphical user interface (GUI). When a user clicks for help, the information displayed depends on what is selected. A specific button inside a dialog might offer different help than a generic button. If the specific button has no assigned help text, the system should fall back to describing the dialog box, and eventually, the application itself.

The system naturally organizes help from the most specific context to the most general. The core challenge is that the object initiating the request (the button) shouldn't be tightly coupled to the object that eventually provides the help.

Chain of Responsibility solves this by decoupling the sender from the receivers. We structure the potential handlers into a chain. When the button receives a request, it either handles it or forwards it to its container (the dialog), which may in turn forward it to the application.

### Object Motivation Diagram
The following object diagram illustrates a concrete chain of UI elements:

```text
┌──────────────┐                   ┌──────────────┐                   ┌───────────────┐
│ aPrintButton │---handleHelp()--->│ aPrintDialog │---handleHelp()--->│ anApplication │
└──────────────┘                   └──────────────┘                   └───────────────┘

─────────────────────────────────────────────────────────────────────────────────────>
specific                                                                       general
```
***Diagram Description:** An object interaction diagram illustrating the flow of a help request. The `aPrintButton` initiates the request, but lacking the necessary information, forwards it to its container, `aPrintDialog`. The dialog, also lacking specific help, passes it up to `anApplication`, which finally processes it.*

### Class Motivation Diagram
To make this work, all objects in the chain share a common interface for handling requests.

```text
       +-----------------+
       |   HelpHandler   |o----------+
       +-----------------+           | successor
       | handleHelp()    |           |
       | hasHelp()       |<----------+
       +-----------------+
               △
               |
      +--------+--------+
      |                 |
 +---------+    +-------------+
 | Widget  |    | Application |
 +---------+    +-------------+
      △
      |
  +---+---+
  |       |
+------+ +--------+
|Button| | Dialog |
+------+ +--------+
```
***Diagram Description:** A class diagram showing the inheritance structure. `HelpHandler` is the root abstraction containing the `successor` link and the `handleHelp()` operation. `Widget` and `Application` extend it. Specific UI components like `Button` and `Dialog` inherit from `Widget`.*

### Interaction Motivation Diagram
When the client fires a request, it propagates through the chain until it is consumed.

```text
  aClient          aPrintButton         aPrintDialog        anApplication
     │                  │                    │                    │
     │   handleHelp()   │                    │                    │
     │─────────────────>│                    │                    │
     │                  │                    │                    │
     │                  │    handleHelp()    │                    │
     │                  │───────────────────>│                    │
     │                  │                    │                    │
     │                  │                    │    handleHelp()    │
     │                  │                    │───────────────────>│
```
***Diagram Description:** A sequence of interactions. The client calls `handleHelp` on the button, which forwards it to the dialog, which finally forwards it to the application.*

**☕ Modern Java Insight:**
> Abstract classes with a `successor` field are the traditional way to build this. However, in modern Java, we often prefer utilizing `Consumer<T>` or a `List` of interfaces, iterating through them (like an Interceptor pattern) to reduce inheritance depth.
```java
public class HelpHandler {
    private HelpHandler successor;
    public void handle(Request req) {
        if (canHandle(req)) process(req);
        else if (successor != null) successor.handle(req);
    }
}
```

---

## Applicability
Use the Chain of Responsibility pattern when:
* More than one object can handle a request, and the handler isn't known ahead of time. The handler should be determined dynamically.
* You want to issue a request to one of several objects without specifying the exact receiver explicitly.
* The set of objects that can handle a request should be specified or altered dynamically at runtime.

---

## Structure & Participants

### Structure Class Diagram
```text
                                  +----------------+
                                  |                |
                                  V                |
                     +-----------------+ successor |
+--------+           |     Handler     |o----------+
| Client |---------->+-----------------+ 
+--------+           | handleRequest() |           
                     +-----------------+
                             △
                             |
                  +----------+----------+
                  |                     |
         +------------------+  +------------------+
         | ConcreteHandler1 |  | ConcreteHandler2 |
         +------------------+  +------------------+
         | handleRequest()  |  | handleRequest()  |
         +------------------+  +------------------+
```
***Diagram Description:** The generic GoF class structure. The `Client` interacts solely with the `Handler` interface. The `Handler` maintains a reference to a `successor` of the same type. `ConcreteHandler1` and `ConcreteHandler2` implement the processing logic and routing.*

### Structure Object Diagram
```text
┌─────────┐      ┌────────────────────┐      ┌────────────────────┐
│ aClient │─────>│  aConcreteHandler  │─────>│  aConcreteHandler  │
└─────────┘      └────────────────────┘      └────────────────────┘
```
***Diagram Description:** Generic runtime object interaction passing the request along.*

### Participants
* **Handler (`HelpHandler`):** Defines an interface for handling requests and optionally implements the successor link.
* **ConcreteHandler (`Button`, `Dialog`):** Handles the requests it is responsible for. If it cannot handle the request, it forwards it to its successor.
* **Client:** Initiates the request to a `ConcreteHandler` object on the chain.

---

## Collaborations
When a client issues a request, the request propagates along the chain until a `ConcreteHandler` assumes responsibility and processes it.

**☕ Java Best Practice:**
> Avoid creating excessively long chains, as they can cause performance hits and deep call stacks (stack overflow risks). A well-designed chain should be relatively shallow and explicitly managed.

---

## Consequences (Trade-offs)
**Benefits:**
1.  **Reduced Coupling:** The pattern frees an object from knowing which other object handles a request. An object only needs to know that a request will be handled "somewhere."
2.  **Added Flexibility:** You gain flexibility in distributing responsibilities among objects. You can dynamically alter responsibilities by adding, removing, or reordering members in the chain at runtime.

**Drawbacks:**
1.  **Receipt Isn't Guaranteed:** Since a request has no explicit receiver, there's no inherent guarantee it will be handled. A request can fall off the end of the chain if the chain is not configured properly or if no handler maps to the specific request condition.

---

## Implementation Hints & Modern Java Context

1. **Implementing the Successor Chain:**
   * *Using Existing Links:* You can leverage existing object references (e.g., a widget's `parent` reference in a UI tree) to form the chain. This saves redundant pointers.
   * *Defining New Links:* If no natural hierarchy exists, you must define the chain structure manually, typically by injecting a `successor` reference into the `Handler` base class.
2. **Connecting Successors:** If defining new links, provide default forwarding behavior in the base class.
    ```java
    public abstract class Handler {
        protected Handler successor;
        public void setSuccessor(Handler successor) { this.successor = successor; }
        public void handleRequest(Request request) {
            if (successor != null) successor.handleRequest(request);
        }
    }
    ```
3. **Representing Requests:**
   * *Method Invocations:* The simplest way is hard-coding specific operations (e.g., `handleHelp()`). However, this limits the chain to handling only that specific method.
   * *Request Objects:* To accommodate multiple types of requests, pass a generic `Request` object containing a request identifier or payload. In Java, this is heavily used.
     ```java
     // Example using a Request object and a switch statement (or instanceof in modern Java)
     public void handleRequest(Request req) {
         if (req instanceof PrintRequest pr) {
             // handle print
         } else {
             super.handleRequest(req);
         }
     }
     ```

---

## Known Uses in Modern Java frameworks
* **Java EE / Jakarta EE (`javax.servlet.Filter`):** The Servlet Filter chain is arguably the most famous example. Requests pass through a chain of filters (for logging, authentication, compression) before hitting the target servlet.
* **Spring Security (`SecurityFilterChain`):** Operates on a chain of distinct security filters (CORS, CSRF, Authentication, Authorization) to secure endpoints.
* **Java Logging Frameworks (`java.util.logging.Logger`):** Loggers are arranged in a namespace hierarchy. If a logger isn't configured with a specific level or handler, it forwards the logging request up to its parent.
* **Exception Handling:** The Java `try-catch` block natively uses this concept. An exception travels up the call stack until an appropriate `catch` block (handler) is found.

---

## Related Patterns
* **Composite:** Chain of Responsibility is frequently used alongside Composite. The parent component in the composite structure often acts as the successor handler for its children.