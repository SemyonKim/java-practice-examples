package patterns.gof.creational.prototype;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * DESIGN PATTERN: Prototype
 * CATEGORY:       Creational
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Specify the kinds of objects to create using a prototypical instance, and
 * create new objects by copying this prototype. Addresses the problem of
 * tying client code to specific classes when instantiating new objects.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A UI framework provides a tool to place items on a canvas. Instead of
 * hardcoding the tool to create a specific 'Note' or 'Staff', the tool is
 * given a "prototype" of the item. To create a new item, it just tells the
 * prototype to duplicate itself.
 * Analogy: Cellular mitosis. A cell duplicates itself using its existing
 * internal state (DNA) rather than building a new cell from a blueprint.
 * <p>
 * 3. APPLICABILITY
 * - When classes to instantiate are specified at run-time.
 * - To avoid building parallel factory class hierarchies.
 * - When instances of a class have only a few combinations of state, making
 * it easier to clone pre-configured instances.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Prototype (PrototypeElement): Interface defining the clone operation.
 * - ConcretePrototype (Room, Wall, Door): Implements the cloning logic.
 * - Client (MazePrototypeFactory): Creates objects by cloning prototypes.
 * <p>
 * 5. COLLABORATIONS
 * Client asks the prototype object to clone itself.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - Pros: Hides concrete classes, reduces subclassing, allows adding/removing
 * products at runtime, supports specifying objects by varying state.
 * - Cons: Implementing complex deep-copy cloning logic can be extremely
 * difficult, especially with circular object references.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * In modern Java, standard `Object.clone()` and `Cloneable` are considered
 * flawed. This demonstration uses a custom interface utilizing covariant
 * return types. In real-world modern Java, Copy Constructors or serialization
 * (for deep copies) are often preferred over standard cloning.
 * ============================================================================
 */
public class PrototypeDemonstration {

    // ========================================================================
    // MOCKED ENTITIES (Shared Baseline Components)
    // ========================================================================

    enum Direction { NORTH, SOUTH, EAST, WEST }

    /**
     * Modern Java Note: Instead of relying on the problematic java.lang.Cloneable,
     * we define a custom interface that enforces a strongly-typed copy method.
     * This mimics the true GoF 'Prototype' participant cleanly.
     */
    interface PrototypeElement<T> {
        T cloneComponent();
    }

    abstract static class MapSite implements PrototypeElement<MapSite> {
        public abstract void enter();
    }

    static class Room extends MapSite {
        private final int roomNumber;
        public MapSite[] sides = new MapSite[4];

        public Room(int roomNumber) {
            this.roomNumber = roomNumber;
        }

        /**
         * Copy Constructor approach (Modern Java best practice for cloning).
         */
        public Room(Room source) {
            this.roomNumber = source.roomNumber;
            // Shallow copy of sides for simplicity, deep copy would be needed for complex graphs
            System.arraycopy(source.sides, 0, this.sides, 0, source.sides.length);
        }

        @Override
        public void enter() {
            System.out.println("Entering Room " + roomNumber);
        }

        @Override
        public Room cloneComponent() {
            return new Room(this); // Utilizing copy constructor
        }

        public void setSide(Direction direction, MapSite site) {
            sides[direction.ordinal()] = site;
        }
    }

    static class Wall extends MapSite {
        public Wall() {}

        public Wall(Wall source) {
            // Copy state if any existed
        }

        @Override
        public void enter() {
            System.out.println("You hit a solid wall.");
        }

        @Override
        public Wall cloneComponent() {
            return new Wall(this);
        }
    }

    static class Door extends MapSite {
        private Room room1;
        private Room room2;
        private boolean isOpen;

        public Door(Room r1, Room r2) {
            this.room1 = r1;
            this.room2 = r2;
            this.isOpen = false;
        }

        public Door(Door source) {
            this.room1 = source.room1;
            this.room2 = source.room2;
            this.isOpen = source.isOpen;
        }

        public void initialize(Room r1, Room r2) {
            this.room1 = r1;
            this.room2 = r2;
        }

        @Override
        public void enter() {
            System.out.println("Walking through a " + (isOpen ? "open" : "closed") + " door.");
        }

        @Override
        public Door cloneComponent() {
            return new Door(this);
        }
    }

    static class Maze implements PrototypeElement<Maze> {
        private final Map<Integer, Room> rooms = new HashMap<>();

        public Maze() {}

        public Maze(Maze source) {
            // Shallow copy of map for demonstration
            this.rooms.putAll(source.rooms);
        }

        public void addRoom(Room room) {
            rooms.put(room.roomNumber, room);
        }

        public Room roomNo(int number) {
            return rooms.get(number);
        }

        @Override
        public Maze cloneComponent() {
            return new Maze(this);
        }
    }

    // --- Subclassed products to demonstrate dynamic swapping ---

    static class BombedWall extends Wall {
        private boolean isBombed;

        public BombedWall() { this.isBombed = false; }

        public BombedWall(BombedWall source) {
            super(source);
            this.isBombed = source.isBombed;
        }

        @Override
        public void enter() {
            System.out.println("You hit a bombed wall! Watch out for damage.");
        }

        @Override
        public BombedWall cloneComponent() {
            return new BombedWall(this);
        }
    }

    static class EnchantedRoom extends Room {
        public EnchantedRoom(int roomNumber) { super(roomNumber); }

        public EnchantedRoom(EnchantedRoom source) {
            super(source);
        }

        @Override
        public void enter() {
            System.out.println("Entering a magical Enchanted Room.");
        }

        @Override
        public EnchantedRoom cloneComponent() {
            return new EnchantedRoom(this);
        }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * We have to build separate factory classes entirely, or hardcode
     * instantiations via 'new', restricting dynamic variations.
     */
    static class NaiveMazeGame {
        public Maze createMaze() {
            Maze maze = new Maze();
            Room r1 = new Room(1);
            Wall w = new Wall();
            r1.setSide(Direction.NORTH, w);
            maze.addRoom(r1);
            return maze;
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * MazePrototypeFactory acts as the Client to the Prototypes. It contains
     * prototypical instances of Maze, Wall, Room, and Door. Instead of using
     * 'new', it asks the prototypes to clone themselves.
     */
    static class MazePrototypeFactory {
        private final Maze prototypeMaze;
        private final Room prototypeRoom;
        private final Wall prototypeWall;
        private final Door prototypeDoor;

        /**
         * The factory is configured with prototypes at runtime.
         */
        public MazePrototypeFactory(Maze maze, Wall wall, Room room, Door door) {
            this.prototypeMaze = maze;
            this.prototypeWall = wall;
            this.prototypeRoom = room;
            this.prototypeDoor = door;
        }

        public Maze makeMaze() {
            return prototypeMaze.cloneComponent();
        }

        public Room makeRoom(int roomNumber) {
            // Clone the prototype, then configure its specific state
            Room room = prototypeRoom.cloneComponent();
            // Note: A true robust prototype might require an initialize() method here

            return room;
        }

        public Wall makeWall() {
            return prototypeWall.cloneComponent();
        }

        public Door makeDoor(Room r1, Room r2) {
            Door door = prototypeDoor.cloneComponent();
            door.initialize(r1, r2); // Initialize specific references after cloning
            return door;
        }
    }

    /**
     * A utility class to sequence the maze construction using any factory.
     */
    static class MazeGame {
        public Maze createMaze(MazePrototypeFactory factory) {
            Maze aMaze = factory.makeMaze();
            Room r1 = factory.makeRoom(1);
            Room r2 = factory.makeRoom(2);
            Door aDoor = factory.makeDoor(r1, r2);

            aMaze.addRoom(r1);
            aMaze.addRoom(r2);

            r1.setSide(Direction.NORTH, factory.makeWall());
            r1.setSide(Direction.EAST, aDoor);
            r1.setSide(Direction.SOUTH, factory.makeWall());
            r1.setSide(Direction.WEST, factory.makeWall());

            r2.setSide(Direction.NORTH, factory.makeWall());
            r2.setSide(Direction.EAST, factory.makeWall());
            r2.setSide(Direction.SOUTH, factory.makeWall());
            r2.setSide(Direction.WEST, aDoor);

            return aMaze;
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Prototype Naive Approach ---");
        NaiveMazeGame naiveGame = new NaiveMazeGame();
        Maze naiveMaze = naiveGame.createMaze();
        naiveMaze.roomNo(1).enter();

        System.out.println("\n--- Prototype Pattern Approach ---");
        MazeGame game = new MazeGame();

        // 1. Configure the factory with standard components
        System.out.println("--- 1. Generating Standard Maze via Prototypes ---");
        MazePrototypeFactory standardFactory = new MazePrototypeFactory(
                new Maze(), new Wall(), new Room(1), new Door(null, null)
        );
        Maze standardMaze = game.createMaze(standardFactory);
        standardMaze.roomNo(1).enter();
        standardMaze.roomNo(1).sides[Direction.NORTH.ordinal()].enter();

        // 2. Simply swap prototypes to generate a completely different family of objects
        System.out.println("\n--- 2. Generating Bombed/Enchanted Maze via Swapped Prototypes ---");
        MazePrototypeFactory customFactory = new MazePrototypeFactory(
                new Maze(), new BombedWall(), new EnchantedRoom(1), new Door(null, null)
        );
        Maze customMaze = game.createMaze(customFactory);
        customMaze.roomNo(1).enter(); // Should be an Enchanted Room
        customMaze.roomNo(1).sides[Direction.NORTH.ordinal()].enter(); // Should be a Bombed Wall
    }
}