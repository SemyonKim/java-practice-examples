package patterns.gof.creational.abstractfactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * DESIGN PATTERN: Abstract Factory
 * CATEGORY:       Creational
 * ALSO KNOWN AS:  Kit
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Provide an interface for creating families of related or dependent objects
 * without specifying their concrete classes.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * UI toolkits must support multiple look-and-feel standards (Motif, PM) without
 * hard-coding widget classes. An Abstract Factory provides a centralized interface
 * to produce a complete, matching family of widgets (Windows, Scrollbars) so the
 * client code remains agnostic to the specific standard being used.
 * <p>
 * 3. APPLICABILITY
 * - A system should be independent of how products are created/composed.
 * - A system needs to be configured with one of multiple product families.
 * - Product family constraints must be enforced.
 * - Providing a class library of products, revealing only interfaces.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - AbstractFactory (MazeFactory): Declares interface for creating abstract products.
 * - ConcreteFactory (EnchantedMazeFactory, BombedMazeFactory): Implements creation operations.
 * - AbstractProduct (Room, Wall, Door): Interfaces for product types.
 * - ConcreteProduct (EnchantedRoom, BombedWall, etc.): Specific implementations.
 * - Client (MazeGame): Uses only abstract interfaces.
 * <p>
 * 5. COLLABORATIONS
 * A single instance of ConcreteFactory is created at run-time and passed to the
 * client. The client delegates object creation to this factory.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Isolates concrete classes from the client.
 * + Makes exchanging product families trivial.
 * + Promotes consistency among products.
 * - Supporting new kinds of products requires updating the abstract interface
 * and all factory subclasses.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * - Factories are often Singletons.
 * - Often uses Factory Method for creation, but can use Prototype to avoid subclassing.
 * - MODERN UPDATE: Java 21+ permits using Dependency Injection or generic
 * interfaces (Supplier<T>) to minimize boilerplate. Record classes can be used
 * for immutable products.
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - GoF: InterViews (WidgetKit), ET++ (WindowSystem).
 * - Java: DocumentBuilderFactory, Connection (JDBC).
 * <p>
 * 9. RELATED PATTERNS
 * Factory Method, Prototype, Singleton.
 */
public class AbstractFactoryDemonstration {

    // ========================================================================
    // MOCKED ENTITIES (Shared Baseline Components)
    // ========================================================================

    public enum Direction { NORTH, SOUTH, EAST, WEST }

    public abstract static class MapSite {
        public abstract void enter();
    }

    public static class Maze {
        private final Map<Integer, Room> rooms = new HashMap<>();
        public void addRoom(Room room) { rooms.put(room.roomNumber, room); }
        public Room roomNo(int number) { return rooms.get(number); }
    }

    // --- Abstract Products ---

    public static class Room extends MapSite {
        protected int roomNumber;
        protected MapSite[] sides = new MapSite[4];

        public Room(int roomNo) { this.roomNumber = roomNo; }
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

    // --- Concrete Products for Enchanted Family ---

    public static class Spell {
        public String description = "Abracadabra!";
    }

    public static class EnchantedRoom extends Room {
        private Spell spell;
        public EnchantedRoom(int n, Spell spell) {
            super(n);
            this.spell = spell;
        }
        @Override public void enter() { System.out.println("Entering Enchanted Room " + roomNumber + ". Spell: " + spell.description); }
    }

    public static class DoorNeedingSpell extends Door {
        public DoorNeedingSpell(Room r1, Room r2) { super(r1, r2); }
        @Override public void enter() { System.out.println("Passing through an Enchanted Door requiring a spell."); }
    }

    // --- Concrete Products for Bombed Family ---

    public static class RoomWithABomb extends Room {
        public RoomWithABomb(int n) { super(n); }
        @Override public void enter() { System.out.println("Entering Room " + roomNumber + ". Watch out for the bomb!"); }
    }

    public static class BombedWall extends Wall {
        @Override public void enter() { System.out.println("Bumping into a damaged, bombed wall."); }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Hard-codes the instantiation of specific components. If we want a bombed
     * maze, we have to rewrite this entire method.
     */
    static class NaiveMazeGame {
        public Maze createMaze() {
            Maze aMaze = new Maze();
            Room r1 = new Room(1);
            Room r2 = new Room(2);
            Door theDoor = new Door(r1, r2);

            aMaze.addRoom(r1);
            aMaze.addRoom(r2);
            // Hard-coded instantiations of 'Wall'
            r1.setSide(Direction.NORTH, new Wall());
            r1.setSide(Direction.EAST, theDoor);
            // ... omitting rest for brevity
            return aMaze;
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     */

    // Abstract Factory (Acts as both Abstract and Concrete Factory for the standard base)
    public static class MazeFactory {
        public MazeFactory() {}
        public Maze makeMaze() { return new Maze(); }
        public Wall makeWall() { return new Wall(); }
        public Room makeRoom(int n) { return new Room(n); }
        public Door makeDoor(Room r1, Room r2) { return new Door(r1, r2); }
    }

    // Concrete Factory 1: Enchanted Maze
    public static class EnchantedMazeFactory extends MazeFactory {
        public EnchantedMazeFactory() {}

        @Override
        public Room makeRoom(int n) { return new EnchantedRoom(n, castSpell()); }

        @Override
        public Door makeDoor(Room r1, Room r2) { return new DoorNeedingSpell(r1, r2); }

        protected Spell castSpell() { return new Spell(); }
    }

    // Concrete Factory 2: Bombed Maze
    public static class BombedMazeFactory extends MazeFactory {
        public BombedMazeFactory() {}

        @Override
        public Wall makeWall() { return new BombedWall(); }

        @Override
        public Room makeRoom(int n) { return new RoomWithABomb(n); }
    }

    // The Client
    public static class MazeGame {
        // The client relies entirely on the factory interface
        public Maze createMaze(MazeFactory factory) {
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
        MazeGame game = new MazeGame();

        System.out.println("--- 1. Creating a Standard Maze ---");
        MazeFactory standardFactory = new MazeFactory();
        Maze standardMaze = game.createMaze(standardFactory);
        standardMaze.roomNo(1).enter();
        standardMaze.roomNo(1).sides[Direction.NORTH.ordinal()].enter();

        System.out.println("\n--- 2. Creating an Enchanted Maze ---");
        MazeFactory enchantedFactory = new EnchantedMazeFactory();
        Maze enchantedMaze = game.createMaze(enchantedFactory);
        enchantedMaze.roomNo(1).enter();
        enchantedMaze.roomNo(1).sides[Direction.EAST.ordinal()].enter(); // The Door

        System.out.println("\n--- 3. Creating a Bombed Maze ---");
        MazeFactory bombedFactory = new BombedMazeFactory();
        Maze bombedMaze = game.createMaze(bombedFactory);
        bombedMaze.roomNo(1).enter();
        bombedMaze.roomNo(1).sides[Direction.NORTH.ordinal()].enter(); // The Wall
    }
}