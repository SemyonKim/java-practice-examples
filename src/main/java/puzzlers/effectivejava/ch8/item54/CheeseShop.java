package puzzlers.effectivejava.ch8.item54;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h2>Return empty collections or arrays, not nulls</h2>
 *
 * <p>
 * <b>Core Principle:</b> Methods that return a collection or an array should never return {@code null}
 * to indicate that the container is empty. Instead, they should return a zero-length collection or array.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>API Simplicity:</b> Clients do not need to perform "circumlocution" (null checks) before processing results.</li>
 * <li><b>Error Prevention:</b> Eliminates the risk of {@code NullPointerException} if a client forgets to handle the null case.</li>
 * <li><b>Implementation Clarity:</b> The code inside the method is often simpler as it avoids conditional logic to return null.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Performance (Negligible):</b> Allocating an empty container has a minor cost.
 * <i>Note: This can be mitigated by returning immutable empty constants (e.g., {@code Collections.emptyList()}) if profiling proves allocation is a bottleneck.</i></li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch9.item67 Optimization
 */
public class CheeseShop {

    private final List<Cheese> cheesesInStock = new ArrayList<>();

    /**
     * The right way to return a possibly empty collection.
     * Simple, clean, and safe for the client.
     */
    public List<Cheese> getCheeses() {
        return new ArrayList<>(cheesesInStock);
    }

    /**
     * Optimization: Returns a shared, immutable empty list if stock is empty.
     * Use this only if performance measurements justify it.
     */
    public List<Cheese> getCheesesOptimized() {
        return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArrayList<>(cheesesInStock);
    }

    /**
     * The right way to return a possibly empty array.
     * Note: Passing a zero-length array to toArray is the preferred idiom.
     */
    public Cheese[] getCheesesArray() {
        return cheesesInStock.toArray(new Cheese[0]);
    }

    /**
     * Optimization: Returns a shared static zero-length array.
     */
    private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

    public Cheese[] getCheesesArrayOptimized() {
        return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        CheeseShop shop = new CheeseShop();

        // No null check required! The code is clean and safe.
        List<Cheese> cheeses = shop.getCheeses();
        if (cheeses.contains(new Cheese("Stilton"))) {
            System.out.println("Jolly good!");
        } else {
            System.out.println("No Stilton today.");
        }

        // Works perfectly with for-each loops without checking for null
        for (Cheese c : shop.getCheesesArray()) {
            System.out.println("Cheese: " + c);
        }
    }
}

/**
 * Simple domain class representing a resource.
 */
class Cheese {
    private final String name;
    public Cheese(String name) { this.name = name; }
    @Override public String toString() { return name; }
}