package patterns.gof.creational.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * DESIGN PATTERN: Builder
 * CATEGORY:       Creational
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Separate the construction of a complex object from its representation so that
 * the same construction process can create different representations.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Building a complex Maze requires instantiating rooms, doors, and walls in
 * a specific, error-prone sequence. A Builder hides these details, letting
 * a Director sequence the logic while the Builder manages the state.
 * Analogy: Ordering a custom PC. The Director is the assembly manual, and
 * the Builder is the technician who puts the CPU, RAM, and GPU together.
 * <p>
 * 3. APPLICABILITY
 * - Creating complex objects with multiple components.
 * - Decoupling the assembly algorithm from the component representations.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Builder (MazeBuilder): Specifies an interface for adding maze components.
 * - ConcreteBuilder (StandardMazeBuilder, CountingMazeBuilder): Implements the
 * Builder interface to construct and assemble parts, tracks representation.
 * - Director (MazeGame): Executes the steps to build a maze.
 * - Product (Maze, MazeCounts): The complex object under construction.
 * <p>
 * 5. COLLABORATIONS
 * Client creates Director and ConcreteBuilder. Client passes builder to
 * Director. Director triggers build steps. Client fetches Product from Builder.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Isolates complex construction logic.
 * + Allows different underlying representations using the same process.
 * + Gives you finer control over the construction process (step-by-step).
 * - Requires creating specific Concrete Builders for each new type of Product
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * - We use `default` methods in the Builder interface so concrete builders
 * only implement what they need.
 * - We utilize fluent API returns ({@code return this; }).
 */
public class BuilderDemonstration {

    // ========================================================================
    // MOCKED ENTITIES (Shared Baseline Components)
    // ========================================================================

    public enum Direction { NORTH, SOUTH, EAST, WEST }

    public abstract static class MapSite {
        public abstract void enter();
    }

    public static class Room extends MapSite {
        protected int roomNumber;
        protected MapSite[] sides = new MapSite[4];

        public Room(int roomNo) { this.roomNumber = roomNo; }
        public void setSide(Direction d, MapSite site) { sides[d.ordinal()] = site; }
        public int getRoomNo() { return roomNumber; }
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

    // Product 1
    public static class Maze {
        private final Map<Integer, Room> rooms = new HashMap<>();
        public void addRoom(Room room) { rooms.put(room.roomNumber, room); }
        public Room roomNo(int number) { return rooms.get(number); }
    }

    // Product 2
    /**
     * Modern Java approach: Using a Java 14+ Record to return multiple states.
     */
    public record MazeCounts(int rooms, int doors) {}


    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * The construction logic is tightly bound to the internal representation.
     * Creating variations (like just counting elements instead of building them)
     * requires duplicating the entire construction logic.
     */
    static class NaiveMazeGame {
        public Maze createMaze() {
            Maze maze = new Maze();
            Room r1 = new Room(1);
            Room r2 = new Room(2);
            Door theDoor = new Door(r1, r2);

            maze.addRoom(r1);
            maze.addRoom(r2);
            // Imagine hundreds of lines of complex initialization here...
            return maze;
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     */

    /**
     * Builder Interface.
     * Modern Java Note: Using `default` empty methods prevents concrete subclasses
     * from being forced to implement methods they don't care about.
     */
    public interface MazeBuilder {
        default MazeBuilder buildMaze() {return this;}
        default MazeBuilder buildRoom(int room) {return this;}
        default MazeBuilder buildDoor(int roomFrom, int roomTo) {return this;}
        default Maze getMaze() { return null; }
    }

    /**
     * Concrete Builder 1: StandardMazeBuilder
     * An implementation that builds simple mazes.
     * Keeps track of the maze it's building in the variable currentMaze.
     */
    public static class StandardMazeBuilder implements MazeBuilder {
        private Maze currentMaze;

        public StandardMazeBuilder() {
            this.currentMaze = null;
        }

        @Override
        public MazeBuilder buildMaze() {
            this.currentMaze = new Maze();
            return this;
        }

        @Override
        public Maze getMaze() {
            return this.currentMaze;
        }

        @Override
        public MazeBuilder buildRoom(int n) {
            if (currentMaze.roomNo(n) == null) {
                Room room = new Room(n);
                currentMaze.addRoom(room);
                room.setSide(Direction.NORTH, new Wall());
                room.setSide(Direction.SOUTH, new Wall());
                room.setSide(Direction.EAST, new Wall());
                room.setSide(Direction.WEST, new Wall());
            }
            return this;
        }

        @Override
        public MazeBuilder buildDoor(int n1, int n2) {
            Room r1 = currentMaze.roomNo(n1);
            Room r2 = currentMaze.roomNo(n2);
            Door d = new Door(r1, r2);

            r1.setSide(commonWall(r1, r2), d);
            r2.setSide(commonWall(r2, r1), d);

            return this;
        }

        /**
         * Utility operation that determines the direction of the common wall between two rooms.
         */
        private Direction commonWall(Room r1, Room r2) {
            // Mocked logic for finding common walls based on standard layout logic
            if (r1.getRoomNo() < r2.getRoomNo()) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        }
    }

    /**
     * Concrete Builder 2: CountingMazeBuilder
     * Doesn't create a maze at all; it just counts the different kinds of
     * components that would have been created.
     * Notice how the Product type here (MazeCounts) is entirely different.
     */
    public static class CountingMazeBuilder implements MazeBuilder {
        private int doors;
        private int rooms;

        public CountingMazeBuilder() {
            this.rooms = 0;
            this.doors = 0;
        }

        @Override
        public MazeBuilder buildRoom(int n) {
            this.rooms++;
            return this;
        }

        @Override
        public MazeBuilder buildDoor(int r1, int r2) {
            this.doors++;
            return this;
        }

        public void addWall(int n, Direction dir) {
            // Ignored in standard creation, but available if extended
        }

        public MazeCounts getCounts() {
            return new MazeCounts(rooms, doors);
        }
    }

    /**
     * Director: Manages the complex sequential logic of building.
     * It relies entirely on the abstract interface.
     */
    public static class MazeGame {

        /**
         * The director constructs the product step by step.
         * The builder hides the internal representation of the Maze.
         */
        public void createMaze(MazeBuilder builder) {
            builder.buildMaze()
                    .buildRoom(1)
                    .buildRoom(2)
                    .buildDoor(1, 2);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Builder Naive Approach ---");
        NaiveMazeGame naiveGame = new NaiveMazeGame();
        Maze naiveMaze = naiveGame.createMaze();
        System.out.println("Naive Maze Created with rooms: " + naiveMaze.roomNo(1));

        System.out.println("\n--- Builder Pattern Approach ---");
        MazeGame director = new MazeGame();

        // 1. Building a standard maze
        System.out.println("--- 1. Using StandardMazeBuilder ---");
        StandardMazeBuilder standardBuilder = new StandardMazeBuilder();
        director.createMaze(standardBuilder);
        Maze standardMaze = standardBuilder.getMaze(); // Client retrieves product

        System.out.println("Standard maze created.");
        standardMaze.roomNo(1).enter();
        System.out.println("Checking East side of Room 1:");
        standardMaze.roomNo(1).sides[Direction.EAST.ordinal()].enter(); // Should be the door

        // 2. Reusing the EXACT same construction logic for a different representation
        System.out.println("\n--- 2. Using CountingMazeBuilder ---");
        CountingMazeBuilder countingBuilder = new CountingMazeBuilder();
        director.createMaze(countingBuilder);

        // Fetch the result using the modern Java record approach
        MazeCounts counts = countingBuilder.getCounts();
        System.out.println("The maze has " + counts.rooms() + " rooms and " + counts.doors() + " doors");
    }
}