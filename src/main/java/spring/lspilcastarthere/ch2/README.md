# Chapter 2 - The Spring Context (Defining Beans)

```mermaid
graph TD
    subgraph Conceptual Architecture
        A["Application Memory"] --> B["Spring Context "]
        B -->|Manages| C["Object Instances / Beans "]
        D["Default Application Objects"] -.->|Invisible by default| B
        D -->|Requires explicit addition| B["Spring Context "]
    end

    subgraph Dependency Management
        M["Maven (Build Tool) "] -->|Downloads| P["pom.xml "]
        P -->|Requires dependency| S["spring-context "]
        S -->|Provides| AC["AnnotationConfigApplicationContext "]
    end
    
    AC --> B
```

```mermaid
sequenceDiagram
    box Spring Internals (Under-the-Hood Bootstrapping)
    participant App as Main (Application)
    participant Ctx as AnnotationConfigApplicationContext
    participant Registry as BeanDefinitionRegistry (Internal)
    participant Factory as DefaultListableBeanFactory (Internal)
    participant Bean as Target Bean Instance
    end

    App->>Ctx: new AnnotationConfigApplicationContext(Config.class) 
    Ctx->>Registry: Load configuration class 
    Registry->>Registry: Parse @Configuration, @Bean, and @ComponentScan 
    Registry-->>Factory: Generate BeanDefinition metadata for all discovered beans
    
    Note over Factory: Spring uses CGLIB to proxy @Configuration classes<br/>ensuring @Bean methods always return Singletons.
    
    Factory->>Factory: Instantiate Singletons (Eager Initialization)
    Factory->>Factory: Execute Dependency Injection (Populate Properties)
    Factory->>Factory: Apply BeanPostProcessors (Before Initialization)
    Factory->>Bean: Call @PostConstruct annotated methods 
    Factory->>Factory: Apply BeanPostProcessors (After Initialization)
    Factory-->>Ctx: Context completely initialized
    App->>Ctx: context.getBean(Class) 
    Ctx-->>App: Return managed Bean Instance
```

```mermaid
graph TD
    subgraph "Method 1: @Bean Annotation "
        B_Conf["@Configuration Class "] --> B_Meth["Method returning object instance "]
        B_Meth -->|Annotated with| B_Ann["@Bean "]
        
        B_Ann -->|By default| B_Name["Bean name = Method name "]
        B_Ann -->|Override| B_CustName["name or value attribute e.g., @Bean(name='miki') "]
        
        B_Name --> B_Multi{"Multiple Beans of Same Type? "}
        B_Multi -->|Ambiguity Exception| B_Fail["NoUniqueBeanDefinitionException "]
        B_Multi -->|Resolution 1| B_Prim["Annotate one with @Primary "]
        B_Multi -->|Resolution 2| B_Qual["Request by exact bean name in getBean() "]
    end
```

```java
// Configuration class acting as a source of bean definitions 
@Configuration
public class ProjectConfig {

    // Spring intercepts this call via CGLIB proxy to manage singleton scope
    @Bean(name = "miki") // Customizing the bean identifier 
    @Primary // Resolves ambiguity if multiple Parrots exist 
    Parrot parrot() {
        var p = new Parrot(); // Full control over instantiation 
        p.setName("Miki");
        return p; // Spring takes this returned instance and adds it to context 
    }
    
    // Capable of adding external classes (e.g., java.lang.String) 
    @Bean
    String hello() {
        return "Hello";
    }
}
```

```mermaid
graph TD
    subgraph "Method 2: Stereotype Annotations "
        S_Comp["Target Class "] -->|Annotated with| S_Ann["@Component "]
        S_Conf["@Configuration Class "] -->|Annotated with| S_Scan["@ComponentScan(basePackages='...') "]
        
        S_Scan -->|Instructs Spring| S_Search["Search specified packages "]
        S_Search -->|Discovers| S_Ann
        S_Ann -->|Spring takes control of creation| S_Inst["Bean Instantiated via Constructor"]
        S_Inst -->|Post-Creation| S_Post["@PostConstruct Execution "]
    end
```

```java
// Marking the class for Spring classpath scanning 
@Component
public class Parrot {
    private String name;

    // Executed by Spring BeanPostProcessor immediately after constructor 
    @PostConstruct
    public void init() {
        this.name = "Kiki"; // Customization after Spring creates the instance 
    }
}

// Instructing Spring's ClassPathBeanDefinitionScanner where to look 
@Configuration
@ComponentScan(basePackages = "main")
public class ProjectConfig {
}
```

```mermaid
graph TD
    subgraph "Method 3: Programmatic Registration (Spring 5+) "
        P_App["Application Logic"] --> P_Cond{"Dynamic Conditions "}
        P_Cond -->|Condition A| P_Reg1["Register Bean 1 "]
        P_Cond -->|Condition B| P_Reg2["Register Bean 2 "]
        
        P_Reg1 --> P_Method["context.registerBean(...) "]
        
        P_Method --> P_Param1["1. beanName (String) "]
        P_Method --> P_Param2["2. beanClass (Class<T>) "]
        P_Method --> P_Param3["3. Supplier<T> (Returns instance) "]
        P_Method --> P_Param4["4. BeanDefinitionCustomizer (varargs) "]
        
        P_Param4 -->|Example Customization| P_Primary["bc -> bc.setPrimary(true) "]
    end
```

```java
public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(ProjectConfig.class);

        Parrot x = new Parrot(); // Application creates the instance 
        x.setName("Kiki");

        // Supplier functional interface returns the created instance 
        Supplier<Parrot> parrotSupplier = () -> x;

        // Programmatic addition to BeanDefinitionRegistry directly 
        context.registerBean(
            "parrot1", 
            Parrot.class, 
            parrotSupplier, 
            bc -> bc.setPrimary(true) // Dynamic BeanDefinition customization 
        );
    }
}
```

```mermaid
graph LR
    subgraph Comparison Matrix 
        subgraph "@Bean Approach"
            B1["✅ Full control over instance creation "]
            B2["✅ Can add ANY external object (Libraries/JDK) "]
            B3["✅ Can define multiple instances of the same type "]
            B4["❌ Boilerplate-heavy (1 method per bean) "]
        end
        
        subgraph "Stereotype (@Component) Approach"
            C1["❌ Control only AFTER framework creates it "]
            C2["❌ Restricted to classes YOUR application owns "]
            C3["❌ Limited to one instance of the class by default "]
            C4["✅ Zero boilerplate; clean and fast configuration "]
        end
    end
```