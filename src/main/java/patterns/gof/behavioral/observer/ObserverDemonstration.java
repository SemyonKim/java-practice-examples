package patterns.gof.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * DESIGN PATTERN: Observer
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Dependents, Publish-Subscribe
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Define a one-to-many dependency between objects so that when one object
 * changes state, all its dependents are notified and updated automatically.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * We need to maintain consistency across related objects without tightly
 * coupling them. Analogy: A newspaper publisher (Subject) maintains a list
 * of subscribers (Observers). When a new edition is printed, all subscribers
 * receive it automatically, without the publisher needing to know their
 * personal details.
 * <p>
 * 3. APPLICABILITY
 * - When a change to one object requires changing others dynamically.
 * - When an object needs to notify unknown or independent objects.
 * - To preserve architectural boundaries (e.g., splitting UI from Data).
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Subject: Interface for attaching/detaching Observers.
 * - Observer: Interface with an update() method.
 * - ConcreteSubject: Maintains state, sends notifications.
 * - ConcreteObserver: Synchronizes state with ConcreteSubject upon notification.
 * <p>
 * 5. COLLABORATIONS
 * The Subject state mutates -> Subject calls notifyObservers() -> Loops
 * through Observers calling update() -> Observers query Subject for new state.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Promotes abstract coupling (Subject doesn't know ConcreteObservers).
 * + Enables dynamic broadcast communication.
 * - Blind updates can cause inefficient performance or cascading updates.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * Modern Java eschews the deprecated `java.util.Observer` in favor of
 * `PropertyChangeListener`, Spring Events, or Reactive Streams
 * (`java.util.concurrent.Flow`). It is common to pass the changed state
 * explicitly in the `update()` method (Push Model) to prevent the Observer
 * from needing a strong reference to the Subject.
 * ============================================================================
 */
public class ObserverDemonstration {

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * A tight coupling scenario where the Subject explicitly knows and calls
     * concrete presentation classes. If we add a "PieClock", we have to modify
     * the timer logic.
     */
    static class NaiveDigitalClock {
        public void refreshDisplay(int time) {
            System.out.println("Naive Digital Display: " + time);
        }
    }

    static class NaiveAnalogClock {
        public void redrawHands(int time) {
            System.out.println("Naive Analog Display: Hand moved to " + time);
        }
    }

    static class NaiveClockTimer {
        private int internalTime = 0;
        private final NaiveDigitalClock digitalClock;
        private final NaiveAnalogClock analogClock;

        public NaiveClockTimer(NaiveDigitalClock dc, NaiveAnalogClock ac) {
            this.digitalClock = dc;
            this.analogClock = ac;
        }

        public void tick() {
            internalTime++;
            // Hardcoded updates: violates Open/Closed Principle
            digitalClock.refreshDisplay(internalTime);
            analogClock.redrawHands(internalTime);
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * We define generic interfaces for both Subject and Observer.
     * We use a "Push Model" here by passing the updated state directly,
     * which is common in modern Java event-driven programming.
     */

    // Participant 1: Observer
    public interface Observer {
        void update(int newTime);
    }

    // Participant 2: Subject
    public interface Subject {
        void attach(Observer o);
        void detach(Observer o);
        void notifyObservers();
    }

    // Participant 3: ConcreteSubject
    public static class ClockTimer implements Subject {
        private int internalTime = 0;
        private final List<Observer> observers = new ArrayList<>();

        public int getTime() {
            return internalTime;
        }

        public void tick() {
            internalTime++;
            System.out.println("\n[ClockTimer] Tick... Time is now " + internalTime);
            notifyObservers();
        }

        @Override
        public void attach(Observer o) {
            observers.add(o);
        }

        @Override
        public void detach(Observer o) {
            observers.remove(o);
        }

        @Override
        public void notifyObservers() {
            // Push Model: Sending the time directly to reduce back-querying overhead
            for (Observer observer : observers) {
                observer.update(internalTime);
            }
        }
    }

    // Participant 4a: ConcreteObserver (Digital)
    public static class DigitalClock implements Observer {
        @Override
        public void update(int newTime) {
            System.out.println("-> DigitalClock: LCD displaying " + newTime + ":00");
        }
    }

    // Participant 4b: ConcreteObserver (Analog)
    public static class AnalogClock implements Observer {
        @Override
        public void update(int newTime) {
            System.out.println("-> AnalogClock: Mechanism moving hands to position " + newTime);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     * Runnable code demonstrating the pattern in action vs. the naive way.
     */
    public static void main(String[] args) {
        System.out.println("--- Observer: Naive Approach ---");
        NaiveDigitalClock ndc = new NaiveDigitalClock();
        NaiveAnalogClock nac = new NaiveAnalogClock();
        NaiveClockTimer naiveTimer = new NaiveClockTimer(ndc, nac);
        naiveTimer.tick();
        naiveTimer.tick();

        System.out.println("\n=============================================\n");

        System.out.println("--- Observer: Pattern Approach ---");
        // 1. Create the Subject
        ClockTimer timer = new ClockTimer();

        // 2. Create the Observers
        DigitalClock digitalClock = new DigitalClock();
        AnalogClock analogClock = new AnalogClock();

        // 3. Attach Observers
        timer.attach(digitalClock);
        timer.attach(analogClock);

        // 4. Mutate Subject state (Observers automatically updated)
        timer.tick();
        timer.tick();

        // 5. Dynamic detaching
        System.out.println("\n[System] Detaching AnalogClock...");
        timer.detach(analogClock);

        // 6. Mutate again
        timer.tick();

        // Modern Java addition: Attaching an observer via Lambda expression
        System.out.println("\n[System] Attaching a modern Lambda Observer...");
        timer.attach(time -> System.out.println("-> LambdaObserver: Logged time update - " + time));

        timer.tick();
    }
}