package patterns.gof.creational.factorymethod;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * DESIGN PATTERN: Factory Method
 * CATEGORY:       Creational
 * ALSO KNOWN AS:  Virtual Constructor
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Define an interface for creating an object, but let subclasses decide which
 * class to instantiate. Defers instantiation to subclasses.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A framework handles complex logic (like building a maze or managing documents)
 * but doesn't know the exact components to use. By exposing a "makeRoom()"
 * method, the framework lets subclasses inject custom rooms without changing
 * the core assembly logic.
 * Analogy: A logistics company has a standard delivery process, but the actual
 * transport (Truck vs. Ship) is decided by subclasses (RoadLogistics vs. SeaLogistics).
 * <p>
 * 3. APPLICABILITY
 * - A class can't anticipate the class of objects it must create.
 * - A class wants its subclasses to specify the objects it creates.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Product (MapSite, Room, Wall, Door): Interface of objects the factory creates.
 * - ConcreteProduct (BombedWall, EnchantedRoom): Implements the Product interface.
 * - Creator (MazeGame): Declares the factory method(s) returning Product objects.
 * - ConcreteCreator (BombedMazeGame, EnchantedMazeGame): Overrides the factory
 * method to return an instance of a ConcreteProduct.
 * <p>
 * 5. COLLABORATIONS
 * Creator relies on its subclasses to define the factory method so that it
 * returns an instance of the appropriate ConcreteProduct.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Decouples the core logic from specific product classes.
 * + Easily extended by adding new subclasses.
 * + Connects parallel class hierarchies.
 * - Can lead to an explosion of subclasses if you need a subclass just to
 * override a simple creation step.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * We use `protected` methods to provide a default implementation in the base
 * Creator. Subclasses only override what they need to change.
 * Modern alternative: passing a `Supplier<Room>` instead of subclassing.
 * ============================================================================
 */
public class FactoryMethodDemonstration {

    // ========================================================================
    // MOCKED ENTITIES (Shared Baseline Components)
    // ========================================================================

    public enum Direction { NORTH, SOUTH, EAST, WEST }

    public abstract static class MapSite {
        public abstract void enter();
    }

    public static class Maze {
        private final Map<Integer, Room> rooms = new HashMap<>();
        public void addRoom(Room room) { rooms.put(room.getRoomNumber(), room); }
        public Room roomNo(int number) { return rooms.get(number); }
    }

    public static class Room extends MapSite {
        protected int roomNumber;
        protected MapSite[] sides = new MapSite[4];

        public Room(int roomNo) { this.roomNumber = roomNo; }
        public int getRoomNumber() { return roomNumber; }
        public void setSide(Direction d, MapSite site) { sides[d.ordinal()] = site; }
        @Override public void enter() { System.out.println("Entering standard room: " + roomNumber); }
    }

    public static class Wall extends MapSite {
        @Override public void enter() { System.out.println("Bumping into a standard wall."); }
    }

    public static class Door extends MapSite {
        protected Room room1;
        protected Room room2;
        public Door(Room r1, Room r2) { this.room1 = r1; this.room2 = r2; }
        @Override public void enter() { System.out.println("Passing through a standard door."); }
    }

    // --- Concrete Products (Mocked for Subclasses) ---

    public static class BombedWall extends Wall {
        @Override public void enter() { System.out.println("Bumping into a damaged, bombed wall."); }
    }

    public static class RoomWithABomb extends Room {
        public RoomWithABomb(int n) { super(n); }
        @Override public void enter() { System.out.println("Entering Room " + roomNumber + ". Watch out for the bomb!"); }
    }

    public static class Spell {
        public String description = "Alohomora!";
    }

    public static class EnchantedRoom extends Room {
        private Spell spell;
        public EnchantedRoom(int n, Spell spell) { super(n); this.spell = spell; }
        @Override public void enter() { System.out.println("Entering Enchanted Room " + roomNumber + ". Spell: " + spell.description); }
    }

    public static class DoorNeedingSpell extends Door {
        public DoorNeedingSpell(Room r1, Room r2) { super(r1, r2); }
        @Override public void enter() { System.out.println("Passing through an Enchanted Door requiring a spell."); }
    }


    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Creation logic is hardcoded. If we want a "BombedMaze", we have to
     * duplicate the entire createMaze() method just to change "new Wall()"
     * to "new BombedWall()".
     */
    static class NaiveMazeGame {
        public Maze createMaze() {
            Maze aMaze = new Maze();
            Room r1 = new Room(1); // Hardcoded standard room
            Room r2 = new Room(2);
            Wall w1 = new Wall();  // Hardcoded standard wall

            aMaze.addRoom(r1);
            aMaze.addRoom(r2);
            return aMaze;
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     */

    /**
     * The CREATOR: Defines factory methods in MazeGame for creating the maze,
     * room, wall and door objects. Provides default implementations that
     * return the simplest kinds of components.
     */
    public static class MazeGame {

        // --- Factory Methods ---

        public Maze makeMaze() {
            return new Maze();
        }

        public Room makeRoom(int n) {
            return new Room(n);
        }

        public Wall makeWall() {
            return new Wall();
        }

        public Door makeDoor(Room r1, Room r2) {
            return new Door(r1, r2);
        }

        // --- Template Method utilizing Factory Methods ---
        /**
         * CreateMaze uses the factory methods instead of hard-coding the classes.
         */
        public Maze createMaze() {
            Maze aMaze = makeMaze();
            Room r1 = makeRoom(1);
            Room r2 = makeRoom(2);
            Door theDoor = makeDoor(r1, r2);

            aMaze.addRoom(r1);
            aMaze.addRoom(r2);

            r1.setSide(Direction.NORTH, makeWall());
            r1.setSide(Direction.EAST, theDoor);
            r1.setSide(Direction.SOUTH, makeWall());
            r1.setSide(Direction.WEST, makeWall());

            r2.setSide(Direction.NORTH, makeWall());
            r2.setSide(Direction.EAST, makeWall());
            r2.setSide(Direction.SOUTH, makeWall());
            r2.setSide(Direction.WEST, theDoor);

            return aMaze;
        }
    }

    /**
     * CONCRETE CREATOR 1: EnchantedMazeGame
     * Redefines Room and Door products to return enchanted varieties and adds a
     * helper to cast spells.
     */
    public static class EnchantedMazeGame extends MazeGame {
        public EnchantedMazeGame() {}

        @Override
        public Room makeRoom(int n) {
            return new EnchantedRoom(n, castSpell());
        }

        @Override
        public Door makeDoor(Room r1, Room r2) {
            return new DoorNeedingSpell(r1, r2);
        }

        protected Spell castSpell() {
            return new Spell();
        }
    }

    /**
     * CONCRETE CREATOR 2: BombedMazeGame
     * Subclasses MazeGame to specialize parts of the maze. Redefines Room and
     * Wall products to return bombed varieties.
     */
    public static class BombedMazeGame extends MazeGame {
        public BombedMazeGame() {}

        @Override
        public Wall makeWall() {
            return new BombedWall();
        }

        @Override
        public Room makeRoom(int n) {
            return new RoomWithABomb(n);
        }
    }


    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {

        System.out.println("--- Factory Method Naive Approach ---");
        NaiveMazeGame naiveGame = new NaiveMazeGame();
        naiveGame.createMaze();

        System.out.println("\n--- Factory Method Pattern Approach ---");

        System.out.println("--- 1. Base Game (Standard Components) ---");
        MazeGame standardGame = new MazeGame();
        Maze standardMaze = standardGame.createMaze(); // Calls default factory methods
        standardMaze.roomNo(1).enter();
        standardMaze.roomNo(1).sides[Direction.NORTH.ordinal()].enter();

        System.out.println("\n--- 2. Bombed Game (Overridden Walls & Rooms) ---");
        MazeGame bombedGame = new BombedMazeGame();
        Maze bombedMaze = bombedGame.createMaze(); // Polymorphically calls overridden methods
        bombedMaze.roomNo(1).enter();
        bombedMaze.roomNo(1).sides[Direction.NORTH.ordinal()].enter();

        System.out.println("\n--- 3. Enchanted Game (Overridden Rooms & Doors) ---");
        MazeGame enchantedGame = new EnchantedMazeGame();
        Maze enchantedMaze = enchantedGame.createMaze();
        enchantedMaze.roomNo(1).enter();
        enchantedMaze.roomNo(1).sides[Direction.EAST.ordinal()].enter();
    }
}