# Chapter 6 - Using aspects with Spring AOP (Architecture, Implementation, and Internals)

## 1. Core AOP Concepts & Terminology

Aspect-Oriented Programming helps extract and decouple cross-cutting concerns (like logging, transactions, and security) from business logic.

```mermaid
graph TD
    A[Aspect] -->|Defines what to execute| B(Aspect Logic)
    A -->|Defines when to execute| C(Advice)
    A -->|Defines where to execute| D(Pointcut)
    D -->|Matches| E(Join Point)
    E -->|In Spring, always a| F[Method Call]
    G[Target Object] -->|Declares the intercepted method| F
```
* **Aspect**: A modularization of a concern that cuts across multiple classes; it defines what code the framework should execute.
* **Advice**: The action taken by an aspect at a particular join point, specifying when the app should execute the logic (e.g., Before, After, Around).
* **Pointcut**: A predicate that tells Spring which method calls to intercept.
* **Join Point**: The event that triggers the aspect; in Spring AOP, this is exclusively a method call.
* **Target Object**: The bean declaring the method intercepted by the aspect.

## 2. Under the Hood: The Proxying Mechanism (Weaving)

*Spring Start Here* notes that Spring achieves AOP by providing a "proxy object instead of the real bean". This approach is named weaving. The proxy applies the aspect logic and delegates the call to the actual method. Below is the source-of-truth internal process.

### Proxy Creation Lifecycle
When the Spring `ApplicationContext` initializes, a `BeanPostProcessor` (specifically `AnnotationAwareAspectJAutoProxyCreator`) intercepts bean creation. It evaluates all beans against registered Pointcuts.

```mermaid
sequenceDiagram
    participant IoC as Spring IoC Container
    participant BPP as AbstractAutoProxyCreator
    participant Bean as Target Bean
    participant Proxy as AOP Proxy

    IoC->>Bean: 1. Instantiate Bean
    IoC->>Bean: 2. Inject Dependencies
    IoC->>BPP: 3. postProcessAfterInitialization()
    BPP->>BPP: 4. Scan for matching Pointcuts (convert to Advisors)
    alt Match Found
        BPP->>Proxy: 5. Generate Proxy (JDK or CGLIB)
        BPP-->>IoC: 6. Return Proxy Object
    else No Match
        BPP-->>IoC: Return Original Bean
    end
```

### JDK Dynamic Proxies vs. CGLIB
Internally, the framework utilizes two different strategies for creating these proxies depending on the Target Object's structure:

```mermaid
classDiagram
    class AOPProxy {
        <<interface>>
        +invoke()
    }
    class JdkDynamicAopProxy {
        -Uses java.lang.reflect.Proxy
        -Requires Target to implement interfaces
    }
    class CglibAopProxy {
        -Uses CGLIB Enhancer
        -Creates subclass of Target class
        -Used when Target has no interfaces
    }
    AOPProxy <|-- JdkDynamicAopProxy
    AOPProxy <|-- CglibAopProxy
```
*Note: In the book's `CommentService` example, since the service does not implement an interface, Spring transparently defaults to a CGLIB proxy, generating a dynamic subclass at runtime to intercept method calls.*

## 3. Implementation Flow

Implementing an aspect requires four specific steps.

```mermaid
flowchart LR
    S1[Step 1: Enable AOP] --> S2[Step 2: Declare Aspect]
    S2 --> S3[Step 3: Define Pointcut/Advice]
    S3 --> S4[Step 4: Implement Logic]

    subgraph Configuration
    S1 -.->|"@EnableAspectJAutoProxy"| C1(Configuration Class)
    end

    subgraph Aspect Class
    S2 -.->|"@Aspect + @Bean"| C2(Spring Context)
    S3 -.->|"@Around, @Before"| C3(Advice Annotation)
    S4 -.->|ProceedingJoinPoint| C4(Aspect Logic)
    end
```
**Critical Constraint:** The `@Aspect` annotation is not a stereotype annotation. Using `@Aspect` tells Spring the class is an aspect, but Spring won't create a bean for it automatically. You must explicitly declare it as a bean using `@Bean` or a stereotype like `@Component`.

## 4. Advice Types and State Alteration

Spring AOP supports five types of advice: `@Around`, `@Before`, `@After`, `@AfterReturning`, and `@AfterThrowing`.

`@Around` is the most powerful advice because it completely wraps the target method. It receives a `ProceedingJoinPoint` parameter, representing the intercepted method.

```mermaid
stateDiagram-v2
    [*] --> MethodCall

    state "@Around Advice" as AroundAdvice 
    state AroundAdvice {
        MethodCall --> AspectLogic_Before
        AspectLogic_Before --> ProceedCall
        ProceedCall --> TargetMethodExecution

        state "Target Method" as TargetMethod 
        state TargetMethod {
            TargetMethodExecution --> ReturnsSuccessfully
            TargetMethodExecution --> ThrowsException
        }

        ReturnsSuccessfully --> AspectLogic_AfterReturning
        ThrowsException --> AspectLogic_AfterThrowing

        AspectLogic_AfterReturning --> AspectLogic_After
        AspectLogic_AfterThrowing --> AspectLogic_After
    }

    AspectLogic_After --> [*]
```

### Intercepting and Altering Execution

Aspects can intercept a method call and alter its execution by changing the value of the parameters sent or changing the returned value received by the caller.

```mermaid
sequenceDiagram
    participant Caller as Caller (main)
    participant Aspect as LoggingAspect
    participant Target as CommentService

    Caller->>Aspect: publishComment(originalParam)
    Note over Aspect: joinPoint.getArgs()
    Aspect->>Aspect: Instantiate new param
    Aspect->>Target: joinPoint.proceed(newArguments)
    Target-->>Aspect: originalReturnValue
    Aspect->>Aspect: Mutate return value
    Aspect-->>Caller: newReturnValue
```
*Under the hood:* When `joinPoint.proceed(Object[] args)` is called, the `ReflectiveMethodInvocation` substitutes the original arguments array with the newly provided array before reflecting the method execution onto the target object. The `proceed()` method is designed to throw any `Throwable` coming from the intercepted method.

## 5. Aspect Execution Chain & Ordering

When multiple aspects intercept the same method, they create an execution chain. By default, Spring does not guarantee the order in which two aspects execute. The `@Order` annotation is used to explicitly define the execution sequence.

```mermaid
graph TD
    Caller(Caller) -->|1. Method Call| A1
    subgraph Aspect Execution Chain
        A1("Aspect 1 - @Order 1") -->|2. proceed| A2("Aspect 2 - @Order 2")
        A2 -->|3. proceed| Target[Target Method]
        Target -.->|4. Returns| A2
        A2 -.->|5. Returns| A1
    end
    A1 -.->|6. Final Return| Caller
```
* **Order Valuation**: The smaller the number, the earlier that aspect executes.
* *Under the hood:* Spring translates all aspect rules into a sorted list of `MethodInterceptor` instances. Calling `proceed()` on the `JoinPoint` advances an internal index, popping the next interceptor off the stack until the target method is reached, naturally creating the Russian-doll (wrapping) effect observed on the return path.

## 6. Pointcut Strategies

### AspectJ Expression Language
You can use AspectJ pointcut language to match methods by package, return type, name, and parameters.
`@Around("execution(* services.*.*(..))")`

### Annotation-Driven Pointcuts
A modern, robust alternative to complex expressions is defining a custom annotation (e.g., `@ToLog`).

```mermaid
flowchart TD
    A[Define Custom Annotation] -->|"@Retention RUNTIME"| B("@ToLog")
    B --> C[Annotate Target Method]
    C -->|"@ToLog publishComment"| D(CommentService)
    D --> E[Define Aspect Pointcut]
    E -->|"@Around @annotation ToLog"| F(LoggingAspect)
```
**Critical Details:** 
* By default, Java annotations cannot be intercepted at runtime; you must explicitly set the retention policy using `@Retention(RetentionPolicy.RUNTIME)`.
* The pointcut expression becomes `@annotation(ToLog)` to target the specific annotated methods.