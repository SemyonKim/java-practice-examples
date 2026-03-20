package patterns.gof.behavioral.state;

/**
 * ============================================================================
 * DESIGN PATTERN: State
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Objects for States
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Allow an object to alter its behavior when its internal state changes.
 * To the outside world, the object appears to change its class completely.
 * It resolves the issue of having massive, multipart conditional statements
 * dictating how an object behaves across different lifecycles.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A TCP Connection acts differently to commands (open, close, acknowledge)
 * depending on whether it is listening, established, or closed.
 * + Analogy: A Vending Machine. If you press "Dispense", the behavior is
 * different depending on whether the machine is in a "Has Coin" state or
 * "No Coin" state.
 * <p>
 * 3. APPLICABILITY
 * Use when an object's behavior depends strictly on its state and must adapt
 * at runtime. It fixes poor designs riddled with 'if-else' or 'switch'
 * statements evaluating state flags.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Context (TCPConnection): Defines the interface for clients and maintains
 * a reference to the current State.
 * - State (TCPState): Interface encapsulating state-specific behaviors.
 * - ConcreteStates (TCPListen, TCPEstablished): Implement specific behaviors
 * and often handle transitions to the next state.
 * <p>
 * 5. COLLABORATIONS
 * Context delegates requests to the current State object. State objects might
 * call back the Context to alter its internal state reference (transition).
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Localizes state-specific logic (Single Responsibility Principle).
 * + Makes state transitions explicit and atomic.
 * + State objects can be shared as Flyweights.
 * - Can bloat the project with many small classes if states are trivial.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * States can be singletons if they carry no instance data.
 * + MODERN UPDATE: Java Enums are incredibly powerful for representing states,
 * as they guarantee singletons inherently and can declare abstract methods
 * that each enum constant must implement. Sealed interfaces/classes also
 * strictly control the hierarchy of states.
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - Drawing tools (HotDraw, Unidraw) abstracting the "current tool".
 * - Spring State Machine (Enterprise state management).
 * - TCP Protocol implementations or Task/Thread lifecycle managers.
 * <p>
 * 9. RELATED PATTERNS
 * - Flyweight / Singleton: State objects are often implemented as Singletons.
 * - Strategy: Structurally similar, but States generally know about each other
 * and orchestrate transitions, whereas Strategies are independent algorithms.
 * ============================================================================
 */
public class StateDemonstration {

    // ========================================================================
    // MOCKED ENTITIES
    // ========================================================================

    /**
     * Represents the data stream being transmitted over TCP.
     */
    record TCPOctetStream(String payload) {}

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Demonstrates code riddled with conditional logic depending on an enum.
     * This violates the Open/Closed Principle; adding a new state requires
     * modifying every method.
     */
    static class NaiveImplementation {
        enum StateFlag { CLOSED, LISTENING, ESTABLISHED }

        static class NaiveTCPConnection {
            private StateFlag state = StateFlag.CLOSED;

            public void activeOpen() {
                if (state == StateFlag.CLOSED) {
                    System.out.println("[Naive] Sending SYN... Transition to ESTABLISHED.");
                    state = StateFlag.ESTABLISHED;
                } else {
                    System.out.println("[Naive] Error: Already open or listening.");
                }
            }

            public void close() {
                if (state == StateFlag.ESTABLISHED) {
                    System.out.println("[Naive] Sending FIN... Transition to CLOSED.");
                    state = StateFlag.CLOSED;
                } else if (state == StateFlag.LISTENING) {
                    System.out.println("[Naive] Stopping listener... Transition to CLOSED.");
                    state = StateFlag.CLOSED;
                } else {
                    System.out.println("[Naive] Error: Already closed.");
                }
            }
            // Imagine 10 more methods with massive switch statements here...
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * Extracts behavior into specific State classes.
     */

    /**
     * State Interface (TCPState).
     * Defines default behavior, which ConcreteStates will override.
     */
    interface TCPState {
        default void activeOpen(TCPConnection context) {
            System.out.println("Invalid operation: activeOpen in current state.");
        }
        default void passiveOpen(TCPConnection context) {
            System.out.println("Invalid operation: passiveOpen in current state.");
        }
        default void close(TCPConnection context) {
            System.out.println("Invalid operation: close in current state.");
        }
        default void transmit(TCPConnection context, TCPOctetStream stream) {
            System.out.println("Invalid operation: transmit in current state.");
        }
    }

    /**
     * Concrete State: CLOSED
     * Implemented as a Singleton as suggested by the GoF text.
     */
    static class TCPClosed implements TCPState {
        private static final TCPClosed INSTANCE = new TCPClosed();
        private TCPClosed() {}
        public static TCPClosed getInstance() { return INSTANCE; }

        @Override
        public void activeOpen(TCPConnection context) {
            System.out.println("[TCPClosed] Sending SYN, receiving SYN/ACK... Transitioning to ESTABLISHED.");
            context.changeState(TCPEstablished.getInstance());
        }

        @Override
        public void passiveOpen(TCPConnection context) {
            System.out.println("[TCPClosed] Opening port... Transitioning to LISTEN.");
            context.changeState(TCPListen.getInstance());
        }
    }

    /**
     * Concrete State: LISTEN
     */
    static class TCPListen implements TCPState {
        private static final TCPListen INSTANCE = new TCPListen();
        private TCPListen() {}
        public static TCPListen getInstance() { return INSTANCE; }

        @Override
        public void close(TCPConnection context) {
            System.out.println("[TCPListen] Closing port listener... Transitioning to CLOSED.");
            context.changeState(TCPClosed.getInstance());
        }
    }

    /**
     * Concrete State: ESTABLISHED
     */
    static class TCPEstablished implements TCPState {
        private static final TCPEstablished INSTANCE = new TCPEstablished();
        private TCPEstablished() {}
        public static TCPEstablished getInstance() { return INSTANCE; }

        @Override
        public void close(TCPConnection context) {
            System.out.println("[TCPEstablished] Sending FIN... Transitioning to LISTEN (Wait state).");
            context.changeState(TCPListen.getInstance());
        }

        @Override
        public void transmit(TCPConnection context, TCPOctetStream stream) {
            System.out.println("[TCPEstablished] Processing octet stream payload: " + stream.payload());
            context.processOctet(stream);
        }
    }

    /**
     * Context (TCPConnection).
     * Maintains a reference to the current state and delegates calls.
     */
    static class TCPConnection {
        private TCPState state;

        public TCPConnection() {
            // Initializing to a default state
            this.state = TCPClosed.getInstance();
        }

        // --- Package-private state modifier accessible by State instances ---
        void changeState(TCPState state) {
            this.state = state;
            System.out.println("  -> Internal State updated to: " + state.getClass().getSimpleName());
        }

        // --- Context utility actions ---
        void processOctet(TCPOctetStream stream) {
            System.out.println("  -> System routed data packet internally.");
        }

        // --- Public Interface delegated to State ---
        public void activeOpen() {
            state.activeOpen(this);
        }

        public void passiveOpen() {
            state.passiveOpen(this);
        }

        public void close() {
            state.close(this);
        }

        public void transmit(TCPOctetStream stream) {
            state.transmit(this, stream);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- State Pattern: Naive Approach ---");
        NaiveImplementation.NaiveTCPConnection naiveConn = new NaiveImplementation.NaiveTCPConnection();
        naiveConn.activeOpen();
        naiveConn.close();
        naiveConn.close(); // Shows error handling

        System.out.println("\n--- State Pattern: Pattern Approach ---");
        // 1. Initialize context
        TCPConnection connection = new TCPConnection();

        // 2. Transmit while closed (Should fail elegantly without blowing up)
        connection.transmit(new TCPOctetStream("Hello"));

        // 3. Open the connection
        connection.activeOpen();

        // 4. Transmit data (Now successful because state changed)
        connection.transmit(new TCPOctetStream("Encrypted Payload..."));

        // 5. Close connection
        connection.close();

        // 6. Attempt another close (Now in Listen wait-state)
        connection.close();
    }
}