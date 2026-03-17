# Creational Design Patterns

***[Back to the Pattern Catalog](../README.md)***

## Overview
Creational design patterns abstract the instantiation process. They help make a system independent of how its objects are created, composed, and represented. Creational patterns become important as systems evolve to depend more on object composition than class inheritance. As that happens, emphasis shifts away from hardcoding a fixed set of behaviors toward defining a smaller set of fundamental behaviors that can be composed into any number of more complex ones. Thus, creating objects with particular behaviors requires more than simply instantiating a class.

---

### Core Themes
There are two recurring themes in these patterns:
1. They all encapsulate knowledge about which concrete classes the system uses.
2. They hide how instances of these classes are created and put together.
- All the system at large knows about the objects is their interfaces as defined by abstract classes.
- Consequently, the creational patterns give you a lot of flexibility in what gets created, who creates it, how it gets created, and when.
- They let you configure a system with "product" objects that vary widely in structure and functionality.
- Configuration can be static (that is, specified at compile-time) or dynamic (at run-time).

Sometimes creational patterns are competitors. For example, there are cases when either Prototype or Abstract Factory could be used profitably. At other times they are complementary: Builder can use one of the other patterns to implement which components get built. Prototype can use Singleton in its implementation.

---

## The Maze Game Baseline
To highlight the similarities and differences among the creational patterns, we use a common example throughout—building a maze for a computer game. We define a maze as a set of rooms. A room knows its neighbors; possible neighbors are another room, a wall, or a door to another room. The classes `Room`, `Door`, and `Wall` define the components of the maze used in all our examples.

**Class Relationships**  
Below is the structural representation of the maze components and their relationships:

| Component | Type           | Key Methods                         | Key Attributes | Inheritance       |
|:----------|:---------------|:------------------------------------|:---------------|:------------------|
| MapSite   | Abstract Class | `enter()`                           | None           | Base Class        |
| Room      | Concrete Class | `enter()`, `setSide()`, `getSide()` | `roomNumber`   | Extends `MapSite` |
| Wall      | Concrete Class | `enter()`                           | None           | Extends `MapSite` |
| Door      | Concrete Class | `enter()`                           | `isOpen`       | Extends `MapSite` |
| Maze      | Class          | `addRoom()`, `roomNo()`             | None           | Independent       |

---

## Base Implementation (Java 21+)

Each room has four sides, mapped using a `Direction` enumeration. The `MapSite` acts as the common abstract class for all components. `enter()` provides a simple basis for more sophisticated game operations. If you enter a room, your location changes; if you enter a door, you either go to the next room (if open) or hurt your nose (if closed). `Room` maintains references to other `MapSite` objects and stores a room number. `MazeGame` creates the maze.

```java
// Mocked Entity: Direction Enum
public enum Direction {
    NORTH, SOUTH, EAST, WEST
}

public abstract class MapSite {
    public abstract void enter();
}

public class Room extends MapSite {
    private final int roomNumber;
    private final MapSite[] sides = new MapSite[4];

    public Room(int roomNo) {
        this.roomNumber = roomNo;
    }

    public MapSite getSide(Direction direction) {
        return sides[direction.ordinal()];
    }

    public void setSide(Direction direction, MapSite mapSite) {
        sides[direction.ordinal()] = mapSite;
    }

    @Override
    public void enter() {
        // Room entry logic
    }
}

public class Wall extends MapSite {
    public Wall() {}

    @Override
    public void enter() {
        // Wall bump logic
    }
}

public class Door extends MapSite {
    private final Room room1;
    private final Room room2;
    private boolean isOpen;

    public Door(Room room1, Room room2) {
        this.room1 = room1;
        this.room2 = room2;
    }

    public Room otherSideFrom(Room room) {
        return room == room1 ? room2 : room1;
    }

    @Override
    public void enter() {
        // Door entry logic
    }
}

import java.util.HashMap;
import java.util.Map;

public class Maze {
    private final Map<Integer, Room> rooms = new HashMap<>();

    public Maze() {}

    public void addRoom(Room room) {
        // Simple mock implementation for adding a room
        rooms.put(room.hashCode(), room);
    }

    public Room roomNo(int number) {
        return rooms.get(number);
    }
}

public class MazeGame {

    // Hard-coded maze creation
    public Maze createMaze() {
        Maze aMaze = new Maze();
        Room r1 = new Room(1);
        Room r2 = new Room(2);
        Door theDoor = new Door(r1, r2);

        aMaze.addRoom(r1);
        aMaze.addRoom(r2);

        r1.setSide(Direction.NORTH, new Wall());
        r1.setSide(Direction.EAST, theDoor);
        r1.setSide(Direction.SOUTH, new Wall());
        r1.setSide(Direction.WEST, new Wall());

        r2.setSide(Direction.NORTH, new Wall());
        r2.setSide(Direction.EAST, new Wall());
        r2.setSide(Direction.SOUTH, new Wall());
        r2.setSide(Direction.WEST, theDoor);

        return aMaze;
    }
}
```

The real problem with the `createMaze` member function isn't its size but its inflexibility. It hard-codes the maze layout. The creational patterns show how to make this design more flexible by making it easy to change the classes that define the components of a maze.

---

### Pattern Approaches to Instantiation
The creational patterns provide different ways to remove explicit references to concrete classes from code that needs to instantiate them:
- **Factory Method:** If `createMaze` calls virtual functions instead of constructor calls to create components, you can change the instantiated classes by making a subclass of `MazeGame` and redefining those virtual functions.
- **Abstract Factory:** If `createMaze` is passed an object as a parameter to use for creating rooms, walls, and doors, you can change the classes by passing a different parameter.
- **Builder:** If `createMaze` is passed an object that builds a new maze entirely via operations for adding parts, you can use inheritance to change parts of the maze or how it is built.
- **Prototype:** If `createMaze` is parameterized by prototypical components that it copies and adds to the maze, you can change the maze's composition by replacing these prototypes.
- **Singleton:** This pattern can ensure there's only one maze per game and that all game objects have ready access to it without resorting to global variables

---

### Discussion and Trade-offs
There are two common ways to parameterize a system by the classes of objects it creates.
- **Class Creational (Subclassing):** One way is to subclass the class that creates the objects, which corresponds to the Factory Method pattern. The main drawback is that it can require creating a new subclass just to change the class of the product, and such changes can cascade.
- **Object Creational (Composition):** The other way relies more on object composition by defining an object responsible for knowing the class of the product objects and making it a parameter. This is a key aspect of Abstract Factory, Builder, and Prototype.

All three object-composition patterns involve creating a new "factory object" whose responsibility is to create product objects. Abstract Factory produces objects of several classes. Builder builds a complex product incrementally. Prototype builds a product by copying a prototype object.

People often use Factory Method as the standard way to create objects, but it isn't always necessary. Designs that use Abstract Factory, Prototype, or Builder are even more flexible than those that use Factory Method, but they're also more complex. Often, designs start out using Factory Method and evolve toward the other creational patterns as the designer discovers where more flexibility is needed. Knowing many design patterns gives you more choices when trading off one design criterion against another.