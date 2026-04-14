# Adapter & Facade Patterns

> 💡 **DESIGN Principles:**
> * Encapsulate what varies. 
> * Favor composition over inheritance. 
> * Program to interfaces, not implementations.
> * Strive for loosely coupled designs between objects that interact.
> * **Open Closed Principle:** Classes should be open for extension but closed for modification. 
> * **Dependency Inversion Principle:** Depend on abstractions. Do not depend on concrete classes.
> * **Principle of Least Knowledge:** Talk only to your immediate friends. 

> 🧩 **DESIGN PATTERN:**
> * The Adapter Pattern converts the interface of a class into another interface the clients expect.  Adapter lets classes work together that couldn’t otherwise because of incompatible interfaces.
> * The Facade Pattern provides a unified interface to a set of interfaces in a subsystem.  Facade defines a higher level interface that makes the subsystem easier to use. 

---

## Architecture Evolution 1: The Adapter Pattern

### Naive/Initial State

```mermaid
classDiagram
    direction LR
    class Client {
    }
    class Duck {
        <<interface>>
        +quack()
        +fly()
    }
    class Turkey {
        <<interface>>
        +gobble()
        +fly()
    }
    Client --> Duck : Expects Target 
    note for Turkey "Incompatible Adaptee Interface "
```

```java
// Target Interface 
public interface Duck {
    void quack(); 
    void fly(); 
}

// Adaptee Interface 
public interface Turkey {
    void gobble(); // Turkeys don’t quack, they gobble. 
    void fly(); // Turkeys can fly, although they can only fly short distances. 
}
```

### Intermediate Evolution States (Object vs. Class Adapter)

```mermaid
classDiagram
    direction BT
    class Target {
        <<interface>>
    }
    class Adaptee
    class ObjectAdapter {
        -Adaptee adaptee
    }
    class ClassAdapter
    
    ObjectAdapter ..|> Target : Implements
    ObjectAdapter --> Adaptee : Composes 
    
    ClassAdapter --|> Target : Subclasses
    ClassAdapter --|> Adaptee : Subclasses 
    
    note for ClassAdapter "Requires Multiple Inheritance (Not possible in Java) "
```

### Final Pattern-Refined State (Object Adapter)

```mermaid
classDiagram
    direction LR
    class Client
    class Duck {
        <<interface_Target>>
        +quack()
        +fly()
    }
    class TurkeyAdapter {
        -Turkey turkey
        +quack()
        +fly()
    }
    class Turkey {
        <<interface_Adaptee>>
        +gobble()
        +fly()
    }
    
    Client --> Duck : request() 
    TurkeyAdapter ..|> Duck : Implements 
    TurkeyAdapter --> Turkey : translatedRequest() 
```

```java
// Final Pattern-Refined: Object Adapter 
public class TurkeyAdapter implements Duck {
    Turkey turkey; 

    public TurkeyAdapter(Turkey turkey) {
        this.turkey = turkey; 
    } 

    public void quack() {
        turkey.gobble(); 
    } 

    public void fly() {
        for(int i=0; i < 5; i++) {
            turkey.fly(); 
        } 
    }
}
```

### Final Pattern-Refined State (Enumerator to Iterator Adapter)

```mermaid
classDiagram
    direction LR
    class Iterator {
        <<interface_Target>>
        +hasNext()
        +next()
        +remove()
    }
    class EnumerationIterator {
        -Enumeration enumeration
        +hasNext()
        +next()
        +remove()
    }
    class Enumeration {
        <<interface_Adaptee>>
        +hasMoreElements()
        +nextElement()
    }
    
    EnumerationIterator ..|> Iterator : Implements
    EnumerationIterator --> Enumeration : Composes 
    note for EnumerationIterator "remove() -> throws UnsupportedOperationException "
```

```java
// Final Pattern-Refined: Legacy Collection Adapter 
public class EnumerationIterator implements Iterator<Object> {
    Enumeration<?> enumeration; 

    public EnumerationIterator(Enumeration<?> enumeration) {
        this.enumeration = enumeration; 
    } 

    public boolean hasNext() {
        return enumeration.hasMoreElements(); 
    } 

    public Object next() {
        return enumeration.nextElement(); 
    } 

    public void remove() {
        throw new UnsupportedOperationException(); 
    } 
}
```

---

## Architecture Evolution 2: The Facade Pattern

### Naive/Initial State

```mermaid
graph TD
    Client --> PopcornPopper
    Client --> TheaterLights
    Client --> Screen
    Client --> Projector
    Client --> Amplifier
    Client --> StreamingPlayer
    
    classDef note fill:#f9f9f9,stroke:#333,stroke-width:2px;
    class Client note;
    note1(Tangled Interactions ) --> Client
```

### Final Pattern-Refined State

```mermaid
graph TD
    Client --> Facade
    Facade[HomeTheaterFacade] --> PopcornPopper
    Facade --> TheaterLights
    Facade --> Screen
    Facade --> Projector
    Facade --> Amplifier
    Facade --> StreamingPlayer
    Facade --> Tuner
    
    classDef unified fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    class Facade unified;
    note2(Unified Interface ) -.-> Facade
```

```java
// Final Pattern-Refined: Subsystem Facade 
public class HomeTheaterFacade {
    Amplifier amp;
    Tuner tuner;
    StreamingPlayer player;
    Projector projector;
    TheaterLights lights;
    Screen screen;
    PopcornPopper popper; 

    public HomeTheaterFacade(Amplifier amp, Tuner tuner, StreamingPlayer player, Projector projector, Screen screen, TheaterLights lights, PopcornPopper popper) { 
        this.amp = amp;
        this.tuner = tuner;
        this.player = player;
        this.projector = projector;
        this.screen = screen;
        this.lights = lights;
        this.popper = popper; 
    } 

    public void watchMovie(String movie) { 
        popper.on();
        popper.pop();
        lights.dim(10);
        screen.down();
        projector.on();
        projector.wideScreenMode(); 
        amp.on();
        amp.setStreamingPlayer(player);
        amp.setSurroundSound();
        amp.setVolume(5);
        player.on();
        player.play(movie); 
    }

    public void endMovie() { 
        popper.off();
        lights.on();
        screen.up();
        projector.off();
        amp.off();
        player.stop();
        player.off(); 
    } 
}
```

---

## Architecture Evolution 3: Principle of Least Knowledge (Law of Demeter)

### Naive/Initial State

```mermaid
graph LR
    Client -- getThermometer --> Station
    Client -- getTemperature --> Thermometer
    
    style Client fill:#ffcccc,stroke:#cc0000;
    note3(Coupled to multiple classes ) --> Client
```

```java
// Violation of Principle 
public float getTemp() {
    Thermometer thermometer = station.getThermometer();
    return thermometer.getTemperature(); 
} 
```

### Final Pattern-Refined State

```mermaid
graph LR
    Client -- getTemperature --> Station
    Station -- getTemperature --> Thermometer
    
    style Client fill:#ccffcc,stroke:#009900;
    note4(Talks only to immediate friends ) --> Client
```

```java
// Adherence to Principle (Delegation) 
public float getTemp() {
    return station.getTemperature(); 
} 

// Method Call Boundaries Verification 
public class Car {
    Engine engine; // Component 

    public void start(Key key) {
        Doors doors = new Doors(); // Instantiated object
        boolean authorized = key.turns(); // Parameter object -> OK

        if (authorized) { 
            engine.start(); // Component object -> OK
            updateDashboardDisplay(); // Local method -> OK 
            doors.lock(); // Instantiated object -> OK
        } 
    }
    
    public void updateDashboardDisplay() {
        // update display
    }
}
```