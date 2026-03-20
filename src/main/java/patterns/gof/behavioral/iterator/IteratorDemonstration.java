package patterns.gof.behavioral.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * ============================================================================
 * DESIGN PATTERN: Iterator
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Cursor
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Provides a way to access the elements of an aggregate object sequentially
 * without exposing its underlying representation.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * When watching television, a remote control acts as an Iterator. You press
 * "Next Channel" or "Previous Channel" (the Iterator interface) without
 * needing to know how the TV internally stores its channel list (an array,
 * a linked list, or a complex signal frequency map).
 * <p>
 * 3. APPLICABILITY
 * - To support multiple active traversals of an aggregate.
 * - To access an aggregate's contents without exposing internal details.
 * - To provide a uniform interface for traversing different structures.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Iterator: `java.util.Iterator` (Interface defining hasNext, next)
 * - ConcreteIterator: `ProfileIterator` (Tracks current index)
 * - Aggregate: `java.lang.Iterable` (Interface defining factory method)
 * - ConcreteAggregate: `SocialMediaFeed` (Holds the actual data structure)
 * <p>
 * 5. COLLABORATIONS
 * Client requests an Iterator from the Aggregate. The Iterator tracks the
 * traversal state independently of the Aggregate.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Single Responsibility: Separates traversal logic from data storage.
 * + Open/Closed Principle: You can add new collections and iterators easily.
 * - Overhead: Can be overkill for extremely simple, single-use collections.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * - ALWAYS use Java's built-in `java.lang.Iterable` and `java.util.Iterator`
 * interfaces rather than writing custom `First()`, `Next()` methods.
 * - Leveraging `Iterable` automatically unlocks the use of the Java "for-each"
 * loop, making client code incredibly clean.
 * ============================================================================
 */
public class IteratorDemonstration {

    // A modern Java 21 Record to represent our domain element.
    record UserProfile(String username, boolean isActive) {}

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * The aggregate directly exposes its internal representation (an array).
     * If the internal representation changes (e.g., to a List or Set), every
     * client traversing it will break.
     */
    static class NaiveSocialFeed {
        private final UserProfile[] profiles;
        private int size = 0;

        public NaiveSocialFeed(int capacity) {
            profiles = new UserProfile[capacity];
        }

        public void addProfile(UserProfile profile) {
            if (size < profiles.length) {
                profiles[size++] = profile;
            }
        }

        // BAD: Exposing the internal structure. Clients become tightly coupled.
        public UserProfile[] getProfiles() {
            return profiles;
        }

        public int getSize() {
            return size;
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * The collection implements standard Java Iterable. It hides how it
     * stores data (it could be an array, tree, or fetched from DB).
     */

    static class RobustSocialFeed implements Iterable<UserProfile> {
        // The internal representation is encapsulated
        private final List<UserProfile> profiles = new ArrayList<>();

        public void addProfile(UserProfile profile) {
            profiles.add(profile);
        }

        // Factory Method: Creates the Iterator.
        @Override
        public Iterator<UserProfile> iterator() {
            return new ActiveProfileIterator();
        }

        /**
         * Concrete Iterator as a private inner class.
         * Note how it can implement custom traversal logic (e.g., filtering
         * only active users) without cluttering the main Feed class.
         */
        private class ActiveProfileIterator implements Iterator<UserProfile> {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                // Custom traversal logic: find the next active user
                while (cursor < profiles.size()) {
                    if (profiles.get(cursor).isActive()) {
                        return true;
                    }
                    cursor++; // Skip inactive
                }
                return false;
            }

            @Override
            public UserProfile next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return profiles.get(cursor++);
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Iterator: Naive Approach ---");
        NaiveSocialFeed naiveFeed = new NaiveSocialFeed(5);
        naiveFeed.addProfile(new UserProfile("alice99", true));
        naiveFeed.addProfile(new UserProfile("bob_inactive", false));
        naiveFeed.addProfile(new UserProfile("charlie_dev", true));

        // Client is forced to know about arrays and handle filtering logic manually
        UserProfile[] internalArray = naiveFeed.getProfiles();
        for (int i = 0; i < naiveFeed.getSize(); i++) {
            if (internalArray[i].isActive()) {
                System.out.println("Displaying post for: " + internalArray[i].username());
            }
        }

        System.out.println("\n--- Iterator: Pattern Approach ---");
        RobustSocialFeed robustFeed = new RobustSocialFeed();
        robustFeed.addProfile(new UserProfile("alice99", true));
        robustFeed.addProfile(new UserProfile("bob_inactive", false));
        robustFeed.addProfile(new UserProfile("charlie_dev", true));

        // Client code is decoupled and drastically simplified.
        // The for-each loop implicitly calls .iterator(), .hasNext(), and .next()
        for (UserProfile profile : robustFeed) {
            System.out.println("Displaying post for: " + profile.username());
        }

        // Furthermore, multiple iterators can be used concurrently without overlap:
        System.out.println("\n--- Concurrent Traversal Demonstration ---");
        Iterator<UserProfile> it1 = robustFeed.iterator();
        Iterator<UserProfile> it2 = robustFeed.iterator();

        System.out.println("It1 First Element: " + it1.next().username());
        System.out.println("It2 First Element (Independent cursor): " + it2.next().username());
    }
}