# Proxy - Object Structural

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](ProxyDemonstration.java)**

## Intent & Core Problem
Provide a surrogate or placeholder for another object to control access to it. 

> **Also Known As:** Surrogate 

**☕ Java Note:**
> In modern Java, Proxy is arguably one of the most foundational patterns for enterprise frameworks. It is the underlying magic that enables Spring's aspect-oriented programming (AOP), `@Transactional` boundaries, `@Cacheable` method interception, and Hibernate's lazy loading.

---

## Motivation & Real-World Analogy
One reason for controlling access to an object is to defer the full cost of its creation and initialization until we actually need to use it.  Consider a document editor that can embed graphical objects in a document.  Some graphical objects, like large raster images, can be expensive to create.  But opening a document should be fast, so we should avoid creating all the expensive objects at once when the document is opened.  This isn't necessary anyway, because not all of these objects will be visible in the document at the same time. 

These constraints would suggest creating each expensive object on demand, which in this case occurs when an image becomes visible.  But what do we put in the document in place of the image?  And how can we hide the fact that the image is created on demand so that we don't complicate the editor's implementation?  This optimization shouldn't impact the rendering and formatting code, for example. 

The solution is to use another object, an image proxy, that acts as a stand-in for the real image.  The proxy acts just like the image and takes care of instantiating it when it's required. 

```text
┌───────────────┐        
│ aTextDocument │        ┌───────────────┐                  
├───────────────┤        │ anImageProxy  │                  ┌───────────────┐
│ image o───────┼───────>├───────────────┤                  │    anImage    │
└───────────────┘        │ fileName o────┼─ - - - - - - - ->├───────────────┤
  [ in memory ]          └───────────────┘                  │ data          │
                           [ in memory ]                    └───────────────┘ 
                                                             [ on disk ]
```
*Diagram Description: An object diagram depicting a proxy acting as a lightweight placeholder in memory. `aTextDocument` holds an `image` reference that points to `anImageProxy`. Instead of loading the massive image into memory, the proxy simply stores the `fileName`. The dashed arrow indicates that it conceptually targets `anImage` stored on disk, delaying actual instantiation.* 

The image proxy creates the real image only when the document editor asks it to display itself by invoking its `Draw` operation.  The proxy forwards subsequent requests directly to the image.  It must therefore keep a reference to the image after creating it.  Assuming images are stored in separate files, we can use the file name as the reference to the real object. 

The proxy also stores its **extent**, that is, its width and height.  The extent lets the proxy respond to requests for its size from the formatter without actually instantiating the image. 

```text
┌────────────────┐      ┌─────────────────┐
│ DocumentEditor │─────>│   «interface»   │
└────────────────┘      │     Graphic     │
                        ├─────────────────┤
                        │ draw()          │
                        │ getExtent()     │
                        │ store()         │
                        │ load()          │
                        └─────────────────┘
                                 △
                                 │
                 ┌───────────────┴───────────────┐
                 │                               │
        ┌─────────────────┐             ┌─────────────────┐
        │      Image      │<─ - image - ┤   ImageProxy    │
        ├─────────────────┤             ├─────────────────┤
        │ imageImp        │             │ fileName        │
        │ extent          │             │ extent          │
        ├─────────────────┤             ├─────────────────┤
        │ draw()          │             │ draw() o────────┼──┐ if (image == null) {
        │ getExtent()     │             │ getExtent() o───┼┐ │     image = load(fileName);
        │ store()         │             │ store()         ││ │ }
        │ load()          │             │ load()          ││ │ image.draw();
        └─────────────────┘             └─────────────────┘│ └────────────────────────────┘
                                                           │ if (image == null) return extent;
                                                           │ else return image.getExtent();
                                                           └──────────────────────────────────┘
```
*Diagram Description: A class diagram illustrating the virtual proxy design. The `DocumentEditor` programs against the `Graphic` interface. Both the heavy `Image` and the lightweight `ImageProxy` implement `Graphic`. The `ImageProxy` holds a reference to the real `Image` (instantiated only when `draw()` is called) and returns cached dimensions when `getExtent()` is invoked, saving the cost of loading the actual image data.* 

---

## Applicability
Proxy is applicable whenever there is a need for a more versatile or sophisticated reference to an object than a simple pointer.  Common situations include:

1. **Remote Proxy (Ambassador):** Provides a local representative for an object in a different address space. 
2. **Virtual Proxy:** Creates expensive objects on demand (like the `ImageProxy` described above). 
3. **Protection Proxy:** Controls access to the original object.  Useful when objects should have different access rights. 
4. **Smart Reference:** A replacement for a bare pointer that performs additional actions when an object is accessed.  Typical uses include:
    * Counting the number of references to the real object so it can be freed automatically when there are no more references (smart pointers). 
    * Loading a persistent object into memory when it's first referenced. 
    * Checking that the real object is locked before it's accessed to ensure thread safety. 

---

## Structure & Participants

### Structure

```text
┌───────────────┐        ┌───────────────┐
│    Client     │───────>│  «interface»  │
└───────────────┘        │    Subject    │
                         ├───────────────┤
                         │ request()     │
                         └───────┬───────┘
                                 △
                                 │
                 ┌───────────────┴───────────────┐
                 │                               │
         ┌───────────────┐               ┌───────────────┐
         │  RealSubject  │<─ realSubject ┤     Proxy     │
         ├───────────────┤               ├───────────────┤
         │ request()     │               │ request() o───┼─── realSubject.request();
         └───────────────┘               └───────────────┘
```
*Diagram Description: The common structure of the Proxy pattern. The `Client` interacts exclusively with the `Subject` interface. The `Proxy` intercepts the `request()` call, potentially applying logic like lazy loading or access control, before forwarding the call to the actual `RealSubject`.* 

### Typical Object Interconnection

```text
┌───────────────┐        
│    aClient    │        ┌───────────────┐        
├───────────────┤        │    aProxy     │        ┌───────────────┐
│ subject o─────┼───────>├───────────────┤        │ aRealSubject  │
└───────────────┘        │ realSubject o─┼───────>├───────────────┤
                         └───────────────┘        │               │
                                                  └───────────────┘
```
*Diagram Description: A run-time object diagram illustrating that the client holds a reference to the proxy, which in turn manages the reference to the real subject, creating an invisible layer of indirection.* 

### Participants
* **Proxy** (`ImageProxy`):
    * Maintains a reference that lets the proxy access the real subject. 
    * Provides an interface identical to Subject's so that a proxy can be substituted for the real subject. 
    * Controls access to the real subject and may be responsible for creating and deleting it. 
    * Other responsibilities depend on the kind of proxy (e.g., encoding requests across networks for remote proxies, caching data for virtual proxies, or checking permissions for protection proxies). 
* **Subject** (`Graphic`):
    * Defines the common interface for `RealSubject` and `Proxy` so that a `Proxy` can be used anywhere a `RealSubject` is expected. 
* **RealSubject** (`Image`):
    * Defines the real object that the proxy represents. 

---

## Collaborations
* Proxy forwards requests to `RealSubject` when appropriate, depending on the kind of proxy. 

---

## Consequences (Trade-offs)
The Proxy pattern introduces a level of indirection when accessing an object.  This additional indirection has many uses: 
1. A **remote proxy** can hide the fact that an object resides in a different address space. 
2. A **virtual proxy** can perform optimizations such as creating an object on demand. 
3. Both **protection proxies** and **smart references** allow additional housekeeping tasks when an object is accessed. 

**Copy-on-Write (CoW) Optimization:**  
There's another optimization that the Proxy pattern can hide from the client. It's called copy-on-write, and it's related to creation on demand.  Copying a large and complicated object can be an expensive operation.  If the copy is never modified, then there's no need to incur this cost.  By using a proxy to postpone the copying process, we ensure that we pay the price of copying the object only if it's modified.  

***Example (CoW as Lazy Optimization):***  
Copy-on-Write is a form of **Lazy Loading**. Imagine a heavy image or a large document. If multiple Proxies point to the same Subject, they can all "read" from it without overhead. The expensive "Copy" operation is only triggered if a client attempts to "Write."
    
```text
    ┌──────────┐       ┌──────────────┐            ┌───────────────┐
    │  Client  │──────>│    Proxy     │───shared──>│  RealSubject  │
    └──────────┘       └──────┬───────┘            └───────────────┘
                              │
                      (On writeRequest())
                              │
                              ▼
                        ┌──────────────┐           ┌───────────────┐
                        │    Proxy     │───owns───>│  NewCopyRef   │
                        └──────────────┘           └───────────────┘
```
*Diagram Description: The Proxy maintains a reference to a shared resource. Upon a mutation request, the Proxy checks if it is the sole owner. If not, it duplicates the resource before applying the change, ensuring other proxies remain unaffected.*

**☕ Java Insights:**
> * **Memory Efficiency:** This is rarely implemented manually for simple objects anymore; the JVM's G1 or ZGC garbage collectors handle memory so well that the overhead of the Proxy often outweighs the benefit unless the object is massive (e.g., an in-memory database or a 1GB bitmask).
> * **Virtual Threads (Project Loom):** Since Java 21, if the "write" involves I/O, using Virtual Threads allows you to handle these blocking copy operations without pinning OS threads, making the Proxy pattern even more efficient in high-concurrency environments.
```java
// Mock Implementation: CoW Proxy (Java)
public interface DataBuffer {
    byte read(int index);
    void write(int index, byte value);
}

/**
 * Modern Java CoW Proxy.
 * Uses 'instanceof' pattern matching and records for metadata if needed.
 */
public class CopyOnWriteProxy implements DataBuffer {
    private RealDataBuffer realSubject;
    private boolean isModified = false;

    public CopyOnWriteProxy(RealDataBuffer subject) {
        this.realSubject = subject;
    }

    @Override
    public byte read(int index) {
        return realSubject.read(index);
    }

    @Override
    public void write(int index, byte value) {
        ensureOwnCopy();
        realSubject.write(index, value);
    }

    private void ensureOwnCopy() {
        if (!isModified) {
            // In a real GoF scenario, we'd check a reference counter.
            // Here, we simplify: copy on the very first write.
            this.realSubject = new RealDataBuffer(this.realSubject);
            this.isModified = true;
            System.out.println("Deep copy performed for local mutation.");
        }
    }
}

// Simple Mock for the Real Subject
class RealDataBuffer implements DataBuffer {
    private final byte[] data;

    public RealDataBuffer(int size) { this.data = new byte[size]; }
    
    // Copy Constructor
    public RealDataBuffer(RealDataBuffer other) {
        this.data = other.data.clone();
    }

    public byte read(int index) { return data[index]; }
    public void write(int index, byte value) { data[index] = value; }
}
```

***Understanding Reference Counting in CoW:***  
The GoF book was written when **C++** was the primary language for these patterns. In C++, you had to manage memory manually. The "Reference Count" was a way to let multiple Proxy objects share the same heavy "RealSubject" without duplicating it in memory until absolutely necessary.

Think of the reference count as a **"How many proxies are currently using me?"** tracker.

**The Lifecycle of a Shared Subject**
1. **Initial State:** Proxy A is created with a heavy `RealSubject`. The count is **1**.
2. **The "Copy":** You "copy" Proxy A to create Proxy B. Instead of copying the heavy data, Proxy B just points to the same `RealSubject`. The count becomes **2**.
3. **The Modification (The "Write"):** The client calls a `write()` method on Proxy B.
   - Proxy B sees the count is **2**. This means someone else (Proxy A) is also looking at this data.
   - If Proxy B modifies the data directly, Proxy A’s data would change too—which we don't want!
   - **The Action:** Proxy B creates a brand new, private copy of the data.
   - **The Decrement:** Since Proxy B is no longer using the original shared data, it must tell the original: *"I'm done with you."* It **decrements** the original's count back to **1**.
4. **The Deletion:** If the count ever hits **0**, it means no proxies are left using that specific instance, and it can be safely removed from memory (deleted).
```text
STEP 1: SHARING
┌─────────┐      ┌───────────────┐      ┌─────────┐
│ Proxy A │─────>│ SharedSubject │<─────│ Proxy B │
└─────────┘      │ (Count: 2)    │      └─────────┘
                 └───────────────┘


STEP 2: PROXY B WRITES (The "Copy-on-Write" Event)
            ┌───────────────────────┐
            │ 1. Decrement Original │
            │ 2. Create New Copy    │
            └───────────┬───────────┘
                        │
                        ▼
┌─────────┐      ┌───────────────┐      ┌─────────┐
│ Proxy A │─────>│ SharedSubject │      │ Proxy B │
└─────────┘      │ (Count: 1)    │      └────┬────┘
                 └───────────────┘           │
                                             │ 3. Points to new
                                             ▼
                                    ┌──────────────────┐
                                    │ PrivateSubject   │
                                    │ (Count: 1)       │
                                    └──────────────────┘
```
*Diagram Description: When Proxy B decides to write, it "detaches" from the original subject. By decrementing the original count, it ensures that if Proxy A eventually disappears, the original subject knows it can be deleted.*

**☕ Java Insights & Best Practices**
> In modern Java (21+), we rarely manage reference counts for memory (the Garbage Collector does that for us). However, the **logic** of CoW remains vital for performance and thread safety.
> - **Garbage Collection vs. Manual Deletion:** In Java, you don't "delete" the subject when the count is zero. You simply null out the reference, and the GC reclaims it.
> - **Thread Safety:** If you implement a counter today, you should use `java.util.concurrent.atomic.AtomicInteger` to ensure that incrementing and decrementing are thread-safe.
> - **The "Loner" Optimization:** If a Proxy checks the count and sees it is already **1**, it knows it is the *only* owner. It doesn't need to copy anything! It can just modify the original subject directly. This is a massive performance win.
> - **Value Types (Project Valhalla):** In the near future, Java's Value Types will make the "copying" of small-to-medium objects so cheap that this pattern will mostly be reserved for truly massive heap objects (like multi-GB buffers).
```java
// Mock Implementation: Atomic Counter (Java)
class SharedHeavyData {
    private final AtomicInteger refCount = new AtomicInteger(1);
    private byte[] data = new byte[1024 * 1024]; // 1MB

    public void addRef() { refCount.incrementAndGet(); }

    public void release() {
        if (refCount.decrementAndGet() == 0) {
            // In Java, we don't 'delete', but we might clear resources
            this.data = null; 
            System.out.println("Resource cleared.");
        }
    }

    public int getRefCount() { return refCount.get(); }
    
    public SharedHeavyData copy() { return new SharedHeavyData(); }
}
```


---

## Implementation Hints & Modern Java Context

The original text discusses overloading the member access operator (`->`) in C++ or using `doesNotUnderstand:` in Smalltalk to automatically forward requests.  In modern Java, this concept has evolved into highly sophisticated dynamic proxy mechanisms:

1. **`java.lang.reflect.Proxy` (JDK Dynamic Proxies):**
   Java provides built-in support for generating proxy classes at runtime. This behaves remarkably like the Smalltalk `doesNotUnderstand:` hook.  Instead of manually writing a proxy class that implements every method of an interface to forward calls, you use an `InvocationHandler`. When a method is called on the proxy, it is routed to the `invoke()` method of the handler, allowing you to seamlessly intercept, augment, or forward the method.

2. **Bytecode Manipulation (CGLIB / ByteBuddy):**
   A limitation of JDK Dynamic Proxies is that they only work if the `Subject` is an interface.  If you need to proxy a concrete class, frameworks utilize libraries like CGLIB or ByteBuddy. These generate a subclass of the `RealSubject` at runtime and override all non-final methods to insert interception logic.

3. **Knowing the Subject Type:**
   A proxy doesn't always have to know the concrete type of the real subject. If a proxy can deal with its subject solely through an abstract interface, there's no need to make a proxy class for each `RealSubject` class; it can deal with all of them uniformly.  However, virtual proxies that actually *instantiate* the real subject must know the concrete class. 

---

## Known Uses (Modern Java)
* **Hibernate ORM:** The epitome of the Virtual Proxy. When you fetch an entity that has relationships, Hibernate doesn't load the massive object graph. Instead, it injects bytecode-generated Proxy collections. The actual database query to fetch those associated entities is executed lazily, only when you interact with the proxy.
* **Spring Framework (AOP):** Spring relies entirely on dynamic proxies. When you annotate a class with `@Transactional`, Spring doesn't execute your code directly. It wraps your bean in a Proxy. The Proxy opens a database transaction, invokes your actual method, and then commits or rolls back based on the outcome.
* **Java RMI (Remote Method Invocation):** Represents the classic Remote Proxy. The client interacts with a local "Stub" object (the proxy) that handles the complex network serialization to execute methods on a remote server.

---

## Related Patterns
* **Adapter:** An adapter provides a different interface to the object it adapts.  In contrast, a proxy provides the same interface as its subject.  However, a proxy used for access protection might refuse to perform an operation, meaning its interface may be effectively a subset. 
* **Decorator:** Although decorators can have similar implementations as proxies, they have a different purpose.  A decorator adds one or more responsibilities to an object dynamically, whereas a proxy controls access to an object.  A virtual proxy starts off with an indirect reference (like a file name) but eventually obtains a direct reference, unlike a decorator which always wraps an existing initialized object. 