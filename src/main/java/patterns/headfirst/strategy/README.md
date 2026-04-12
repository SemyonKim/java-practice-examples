# Strategy Pattern

> 💡 **DESIGN Principle:** Favor composition over inheritance.

> 💡 **DESIGN Principle:** The one constant in software development is CHANGE.

> 💡 **DESIGN Principle:** Identify the aspects of your application that vary and separate them from what stays the same.

> 💡 **DESIGN Principle:** Program to an interface, not an implementation. “Program to an interface” really means “Program to a supertype.”

> 🧩 **DESIGN PATTERN:** The Strategy Pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable.  Strategy lets the algorithm vary independently of clients that use it.

### Phase 1: The Naive State (Inheritance Problem) 

```mermaid
classDiagram
    class Duck {
        +quack()
        +swim()
        +display()*
        +fly() 
    }
    class MallardDuck {
        +display()
    }
    class RubberDuck {
        +display()
    }
    
    Duck <|-- MallardDuck : IS-A
    Duck <|-- RubberDuck : IS-A
    note for RubberDuck "BUG: RubberDuck inherits fly()!"
```

```java
public abstract class Duck {
    public void quack() { /* generic quack */ }
    public void swim() { /* generic swim */ }
    public void fly() { /* generic fly */ }
    public abstract void display();
}

public class RubberDuck extends Duck {
    public void display() { /* looks like a rubber duck */ }
    // Inherits fly() - inappropriate behavior
}
```

### Phase 2: Intermediate Evolution (Interface Duplication) 

```mermaid
classDiagram
    class Duck {
        +swim()
        +display()*
    }
    class Flyable {
        <<interface>>
        +fly()
    }
    class Quackable {
        <<interface>>
        +quack()
    }
    class MallardDuck {
        +display()
        +fly()
        +quack()
    }
    class RubberDuck {
        +display()
        +quack()
    }
    
    Duck <|-- MallardDuck
    Flyable <|.. MallardDuck
    Quackable <|.. MallardDuck
    
    Duck <|-- RubberDuck
    Quackable <|.. RubberDuck
    note for MallardDuck "Problem: No code reuse for fly() or quack() across subclasses."
```

### Phase 3: Final Pattern-Refined (Behavior Families & Delegation) 

```mermaid
classDiagram
    class Duck {
        ~FlyBehavior flyBehavior
        ~QuackBehavior quackBehavior
        +performQuack()
        +performFly()
        +setFlyBehavior(fb)
        +setQuackBehavior(qb)
        +swim()
        +display()*
    }
    
    class FlyBehavior {
        <<interface>>
        +fly()
    }
    class QuackBehavior {
        <<interface>>
        +quack()
    }
    
    class FlyWithWings {
        +fly()
    }
    class FlyNoWay {
        +fly()
    }
    
    class Quack {
        +quack()
    }
    class MuteQuack {
        +quack()
    }
    class Squeak {
        +quack()
    }
    
    class MallardDuck {
        +MallardDuck()
        +display()
    }

    Duck *-- FlyBehavior : HAS-A
    Duck *-- QuackBehavior : HAS-A
    
    FlyBehavior <|.. FlyWithWings : IMPLEMENTS
    FlyBehavior <|.. FlyNoWay : IMPLEMENTS
    
    QuackBehavior <|.. Quack : IMPLEMENTS
    QuackBehavior <|.. MuteQuack : IMPLEMENTS
    QuackBehavior <|.. Squeak : IMPLEMENTS
    
    Duck <|-- MallardDuck : IS-A
```

```java
// Behavior Interfaces 
public interface FlyBehavior {
    void fly();
}

public interface QuackBehavior {
    void quack();
}

// Concrete Behavior Families 
public class FlyWithWings implements FlyBehavior {
    public void fly() { System.out.println("I'm flying!"); }
}

public class FlyNoWay implements FlyBehavior {
    public void fly() { System.out.println("I can't fly"); }
}

public class Quack implements QuackBehavior {
    public void quack() { System.out.println("Quack"); }
}

public class MuteQuack implements QuackBehavior {
    public void quack() { System.out.println("<< Silence >>"); }
}

public class Squeak implements QuackBehavior {
    public void quack() { System.out.println("Squeak"); }
}

// Superclass with Delegation and Dynamic Setters 
public abstract class Duck {
    FlyBehavior flyBehavior;
    QuackBehavior quackBehavior;

    public Duck() {}

    public abstract void display();

    public void performFly() {
        flyBehavior.fly(); // Delegation 
    }

    public void performQuack() {
        quackBehavior.quack(); // Delegation 
    }

    public void swim() {
        System.out.println("All ducks float, even decoys!");
    }
    
    // Dynamic Setters 
    public void setFlyBehavior(FlyBehavior fb) {
        flyBehavior = fb;
    }

    public void setQuackBehavior(QuackBehavior qb) {
        quackBehavior = qb;
    }
}

// Concrete Duck 
public class MallardDuck extends Duck {
    public MallardDuck() {
        quackBehavior = new Quack(); 
        flyBehavior = new FlyWithWings();
    }

    public void display() {
        System.out.println("I'm a real Mallard duck");
    }
}
```

### Phase 4: Implementation Testing 

```java
public class MiniDuckSimulator {
    public static void main(String[] args) {
        Duck mallard = new MallardDuck();
        
        // Delegates to Quack and FlyWithWings 
        mallard.performQuack(); 
        mallard.performFly();   
        
        // Dynamic change at runtime 
        mallard.setFlyBehavior(new FlyNoWay());
        mallard.performFly();
    }
}
```