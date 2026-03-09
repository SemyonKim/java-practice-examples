package puzzlers.effectivejava.ch9.item58;

import java.util.*;

/**
 * <h2>Prefer for-each loops to traditional for loops</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use the enhanced for loop (for-each) instead of traditional for loops
 * whenever possible. It eliminates the clutter of iterators and index variables, reducing
 * the opportunity for error and making the code more readable without any performance penalty.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Error Prevention:</b> Eliminates the "iterator occurs three times" or "index variable occurs four times"
 * traps where the wrong variable might be used accidentally.</li>
 * <li><b>Succinctness:</b> Hides the iterator/index boilerplate, focusing purely on the elements.</li>
 * <li><b>Consistency:</b> The same syntax applies to both {@code Iterator}-based collections and arrays.</li>
 * <li><b>Safety in Nesting:</b> Prevents the common bug where the outer iterator is advanced
 * inside the inner loop.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Destructive Filtering:</b> If you must remove elements during iteration, you need the {@code Iterator.remove()} method
 * (though {@code Collection.removeIf} is often a better alternative).</li>
 * <li><b>Transforming:</b> If you need to replace the value of an element in a list or array, you need the index or {@code ListIterator}.</li>
 * <li><b>Parallel Iteration:</b> If you need to traverse multiple collections in lockstep, you must manage multiple iterators/indices manually.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item45 Streams
 * @see puzzlers.effectivejava.ch9.item57 LocalVariableScope
 */
public class EnhancedForLoop {

    enum Suit { CLUB, DIAMOND, HEART, SPADE }
    enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

    static class Card {
        public Card(Suit suit, Rank rank) {}
    }

    /**
     * Demonstrates the preferred idiom for nested iteration.
     * This avoids the "NoSuchElementException" bug common with manual iterators.
     */
    public List<Card> createDeck(Collection<Suit> suits, Collection<Rank> ranks) {
        List<Card> deck = new ArrayList<>();
        // Clean, safe, and bug-free nested iteration
        for (Suit suit : suits) {
            for (Rank rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    /**
     * Demonstrates a case where for-each cannot be used (Destructive filtering).
     * Note: In Java 8+, use collection.removeIf(filter) instead.
     */
    public void removeNulls(List<?> list) {
        for (Iterator<?> i = list.iterator(); i.hasNext(); ) {
            if (i.next() == null) {
                i.remove();
            }
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Collection<Suit> suits = Arrays.asList(Suit.values());
        Collection<Rank> ranks = Arrays.asList(Rank.values());
        EnhancedForLoop generator = new EnhancedForLoop();

        // 1. Simple iteration over an array
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = 0;
        for (int n : numbers) {
            sum += n;
        }
        System.out.println("Sum: " + sum);

        // 2. Nested iteration
        List<Card> deck = generator.createDeck(suits, ranks);
        System.out.println("Deck size: " + deck.size());

        // 3. Handling the "buggy" dice example via for-each
        enum Face { ONE, TWO, THREE, FOUR, FIVE, SIX }
        Collection<Face> faces = EnumSet.allOf(Face.class);

        System.out.println("Possible dice rolls:");
        for (Face f1 : faces) {
            for (Face f2 : faces) {
                // No risk of advancing f1's iterator here!
                System.out.print(f1 + "-" + f2 + " ");
            }
            System.out.println();
        }
    }
}