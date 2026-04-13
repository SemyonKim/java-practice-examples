# Factory Pattern

> 💡 **DESIGN Principles:**
> * Encapsulate what varies.
> * Favor composition over inheritance.
> * Program to interfaces, not implementations.
> * Strive for loosely coupled designs between objects that interact.
> * **Open Closed Principle:** Classes should be open for extension but closed for modification.
> * **Dependency Inversion Principle:** Depend on abstractions. Do not depend on concrete classes.

> 🧩 **DESIGN PATTERNS:**
> 
> **Factory Method Pattern:** Defines an interface for creating an object, but lets subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.
>
> **Abstract Factory Pattern:** Provides an interface for creating families of related or dependent objects without specifying their concrete classes.

---

### Phase 1: Naive/Initial State

```mermaid
flowchart TD
    Client[PizzaStore] --> |new| CC[CheesePizza]
    Client --> |new| GP[GreekPizza]
    Client --> |new| PP[PepperoniPizza]
    
    style Client fill:#f9f,stroke:#333,stroke-width:2px
    style CC fill:#ff9,stroke:#333,stroke-width:2px
    style GP fill:#ff9,stroke:#333,stroke-width:2px
    style PP fill:#ff9,stroke:#333,stroke-width:2px
```

```java
Pizza orderPizza(String type) {
    Pizza pizza;
    // THIS IS WHAT VARIES - NOT CLOSED FOR MODIFICATION
    switch (type) {
        case "cheese" -> pizza = new CheesePizza();
        case "greek" -> pizza = new GreekPizza();
        case "pepperoni" -> pizza = new PepperoniPizza();
    } 

    // THIS STAYS THE SAME
    pizza.prepare();
    pizza.bake(); 
    pizza.cut();
    pizza.box();
    return pizza;
}
```

---

### Phase 2: Intermediate Evolution - Simple Factory Idiom

```mermaid
classDiagram
    class PizzaStore {
        -SimplePizzaFactory factory
        +orderPizza(type: String): Pizza
    }
    
    class SimplePizzaFactory {
        %% Factory encapsulates object creation
        +createPizza(type: String): Pizza
    }
    
    class Pizza {
        <<abstract>>
        +prepare()
        +bake()
        +cut()
        +box()
    }
    
    class CheesePizza
    class PepperoniPizza
    class VeggiePizza

    PizzaStore --> SimplePizzaFactory : uses
    SimplePizzaFactory ..> Pizza : creates
    Pizza <|-- CheesePizza
    Pizza <|-- PepperoniPizza
    Pizza <|-- VeggiePizza
```

```java
public class SimplePizzaFactory {
    public Pizza createPizza(String type) {
        return switch (type) {
            case "cheese" -> new CheesePizza();
            case "pepperoni" -> new PepperoniPizza();
            case "clam" -> new ClamPizza();
            case "veggie" -> new VeggiePizza();
            default -> null;
        };
    }
}

public class PizzaStore {
    SimplePizzaFactory factory;

    public PizzaStore(SimplePizzaFactory factory) { 
        this.factory = factory;
    }

    public Pizza orderPizza(String type) {
        Pizza pizza = factory.createPizza(type);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }
}
```

> 📝 **Note:** In design patterns, the phrase “implement an interface” does NOT always mean “write a class that implements a Java interface, by using the ‘implements' keyword in the class declaration.” In the general use of the phrase, a concrete class implementing a method from a supertype (which could be an abstract class OR interface) is still considered to be “implementing the interface” of that supertype.

---

### Phase 3: Final Pattern-Refined - Factory Method Pattern

```mermaid
classDiagram
    %% The Creator Classes
    class PizzaStore {
        <<abstractCreator>>
        +orderPizza(type: String): Pizza
        #createPizza(type: String): Pizza*
    }
    
    class NYPizzaStore {
        <<ConcreteCreator>>
        #createPizza(type: String): Pizza
    }
    
    class ChicagoPizzaStore {
        <<ConcreteCreator>>
        #createPizza(type: String): Pizza
    }

    %% The Product Classes
    class Pizza {
        <<abstractProduct>>
    }
    
    class NYStyleCheesePizza {
        <<ConcreteProduct>>
    }
    
    class ChicagoStyleCheesePizza {
        <<ConcreteProduct>>
    }

    PizzaStore <|-- NYPizzaStore : implements factory method
    PizzaStore <|-- ChicagoPizzaStore : implements factory method
    
    Pizza <|-- NYStyleCheesePizza
    Pizza <|-- ChicagoStyleCheesePizza
    
    NYPizzaStore ..> NYStyleCheesePizza : decides to instantiate
    ChicagoPizzaStore ..> ChicagoStyleCheesePizza : decides to instantiate
```

```java
public abstract class PizzaStore {
    public Pizza orderPizza(String type) {
        Pizza pizza;
        // Factory Method acts as the object creator
        pizza = createPizza(type);
        pizza.prepare();
        pizza.bake(); 
        pizza.cut();
        pizza.box();
        return pizza;
    }
    
    // Subclasses are counted on to handle object creation
    protected abstract Pizza createPizza(String type);
}

public class NYPizzaStore extends PizzaStore {
    protected Pizza createPizza(String item) {
        return switch (item) {
            case "cheese" -> new NYStyleCheesePizza();
            case "veggie" -> new NYStyleVeggiePizza();
            case "clam" -> new NYStyleClamPizza();
            case "pepperoni" -> new NYStylePepperoniPizza();
            default -> null;
        };
    }
}
```

---

### Architectural Analysis: Dependency Inversion Principle

```mermaid
flowchart TD
    subgraph Initial ["Before DIP (Highly Dependent)"]
        direction TB
        HighLevel1[PizzaStore] --> LowLevel1[NYCheesePizza]
        HighLevel1 --> LowLevel2[ChicagoCheesePizza]
        HighLevel1 --> LowLevel3[NYVeggiePizza]
    end

    subgraph Inverted ["After DIP (Inverted Dependencies)"]
        direction TB
        HighLevel2[PizzaStore] --> Abstraction[Abstract Pizza]
        LowLevel4[NYCheesePizza] -. implements .-> Abstraction
        LowLevel5[ChicagoCheesePizza] -. implements .-> Abstraction
        LowLevel6[NYVeggiePizza] -. implements .-> Abstraction
    end
```

---

### Phase 4: Final Pattern-Refined - Abstract Factory Pattern

```mermaid
classDiagram
    %% Abstract Factory Interfaces
    class PizzaIngredientFactory {
        <<interface>>
        +createDough(): Dough
        +createSauce(): Sauce
        +createCheese(): Cheese
        +createVeggies(): Veggies[]
        +createPepperoni(): Pepperoni
        +createClam(): Clams
    }
    
    class NYPizzaIngredientFactory {
        <<ConcreteFactory>>
        +createDough()
        +createSauce()
        +createClam()
    }

    class ChicagoPizzaIngredientFactory {
        <<ConcreteFactory>>
        +createDough()
        +createSauce()
        +createClam()
    }

    %% Client and Product Framework
    class Pizza {
        <<abstract>>
        ~Dough dough
        ~Sauce sauce
        ~Cheese cheese
        ~Clams clam
        +prepare()*
    }
    
    class CheesePizza {
        -PizzaIngredientFactory ingredientFactory
        +prepare()
    }
    
    class NYPizzaStore {
        +createPizza(item: String)
    }

    %% Relationships
    PizzaIngredientFactory <|.. NYPizzaIngredientFactory
    PizzaIngredientFactory <|.. ChicagoPizzaIngredientFactory
    
    Pizza <|-- CheesePizza
    
    NYPizzaStore ..> CheesePizza : instantiates
    NYPizzaStore ..> NYPizzaIngredientFactory : configures pizza with
    
    CheesePizza --> PizzaIngredientFactory : delegates ingredient creation
```

```java
public interface PizzaIngredientFactory {
    Dough createDough();
    Sauce createSauce();
    Cheese createCheese();
    Veggies[] createVeggies();
    Pepperoni createPepperoni();
    Clams createClam();
}

public class NYPizzaIngredientFactory implements PizzaIngredientFactory {
    public Dough createDough() { return new ThinCrustDough(); }
    public Sauce createSauce() { return new MarinaraSauce(); }
    public Cheese createCheese() { return new ReggianoCheese(); }
    public Veggies[] createVeggies() {
        return new Veggies[]{new Garlic(), new Onion(), new Mushroom(), new RedPepper()}; 
    }
    public Pepperoni createPepperoni() { return new SlicedPepperoni(); }
    public Clams createClam() { return new FreshClams(); }
}

public class CheesePizza extends Pizza {
    PizzaIngredientFactory ingredientFactory;
 
    public CheesePizza(PizzaIngredientFactory ingredientFactory) {
        this.ingredientFactory = ingredientFactory;
    }
 
    void prepare() {
        // Collects ingredients from the local factory via composition
        dough = ingredientFactory.createDough();
        sauce = ingredientFactory.createSauce();
        cheese = ingredientFactory.createCheese();
    }
}

public class NYPizzaStore extends PizzaStore {
    protected Pizza createPizza(String item) {
        Pizza pizza = null;
        PizzaIngredientFactory ingredientFactory = new NYPizzaIngredientFactory();
 
        if (item.equals("cheese")) {
            pizza = new CheesePizza(ingredientFactory);
            pizza.setName("New York Style Cheese Pizza");
        }
        return pizza;
    }
}
```

---

## Architecture Analysis: Factory Method vs. Abstract Factory

### Comparative Architecture: Structural Breakdown

```mermaid
classDiagram
    direction TB

    namespace Factory_Method_Implementation {
        class Creator {
            <<abstract>>
            +orderProduct()
            +factoryMethod()* Product
        }
        class ConcreteCreator {
            +factoryMethod() Product
        }
        class Product {
            <<interface>>
        }
    }

    Creator <|-- ConcreteCreator
    ConcreteCreator ..> Product : instantiates

    namespace Abstract_Factory_Implementation {
        class AbstractFactory {
            <<interface>>
            +createProductA() ProductA
            +createProductB() ProductB
        }
        class ConcreteFactory {
            +createProductA()
            +createProductB()
        }
        class Client {
            -AbstractFactory factory
            +operation()
        }
    }

    AbstractFactory <|.. ConcreteFactory
    Client --> AbstractFactory : uses composition
```

---

### Key Architectural Differentiators

```mermaid
flowchart LR
    subgraph FM [Factory Method]
        direction TB
        FMA[One Product] --> FMB[Class Inheritance]
        FMB --> FMC[Subclassing Required]
    end

    subgraph AF [Abstract Factory]
        direction TB
        AFA[Product Family] --> AFB[Object Composition]
        AFB --> AFC[Interface delegation]
    end

    FM -.-> VS{Decision Point}
    AF -.-> VS
```