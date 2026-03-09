package puzzlers.effectivejava.ch8.item51;

import java.util.Map;
import java.util.Objects;

/**
 * <h2>Design method signatures carefully</h2>
 *
 * <p>
 * <b>Core Principle:</b> Adhere to a set of API design heuristics to ensure methods
 * are easy to learn, consistent, and less prone to user error. This includes careful
 * naming, limiting parameter counts, favoring interfaces, and using enums over booleans.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Readability:</b> Short parameter lists and descriptive names make the
 * intent of the code immediately obvious without documentation.</li>
 * <li><b>Safety:</b> Avoiding long sequences of identically typed parameters
 * prevents accidental transposition errors.</li>
 * <li><b>Flexibility:</b> Favoring interfaces (e.g., {@code Map} over {@code HashMap})
 * allows clients to pass any implementation, avoiding unnecessary data conversion.</li>
 * <li><b>Extensibility:</b> Using enums instead of booleans allows for adding
 * more options in the future without breaking the API.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Boilerplate:</b> Creating helper classes or builders for parameters adds
 * more classes to the project, which can increase complexity if overused.</li>
 * <li><b>Granularity:</b> Breaking methods up too much can lead to an "explosion"
 * of methods that might overwhelm the user if the "power-to-weight ratio" is low.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item2 Builders
 * @see puzzlers.effectivejava.ch4.item24 StaticMemberClasses
 * @see puzzlers.effectivejava.ch6.item34 Enums
 * @see puzzlers.effectivejava.ch9.item64 InterfacesForTypes
 * @see puzzlers.effectivejava.ch9.item68 NamingConventions
 */
public class SignatureDesign {

    // 1. Prefer Enums to Booleans (Better readability and extensibility)
    public enum TemperatureScale { FAHRENHEIT, CELSIUS, KELVIN }

    public static double convertTemperature(double value, TemperatureScale scale) {
        return switch (scale) {
            case CELSIUS -> value;
            case FAHRENHEIT -> (value - 32) * 5 / 9;
            case KELVIN -> value - 273.15;
        };
    }

    // 2. Use Helper Classes (Parameter Objects) to shorten parameter lists
    // Instead of: findCard(int rank, int suit, String deckName, boolean isJokerAllowed)
    public static class Card {
        private final int rank;
        private final String suit;

        public Card(int rank, String suit) {
            this.rank = rank;
            this.suit = Objects.requireNonNull(suit);
        }
    }

    public boolean hasCard(Card card) {
        // Implementation logic
        return true;
    }

    // 3. Favor Interfaces over Classes for parameter types
    // BAD: public void process(HashMap<String, String> map)
    // GOOD: Allows HashMap, TreeMap, LinkedHashMap, etc.
    public void processData(Map<String, String> data) {
        Objects.requireNonNull(data);
        // Process data...
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Case 1: Enum vs Boolean
        // Clearer than: convertTemperature(100.0, true);
        double temp = convertTemperature(100.0, TemperatureScale.FAHRENHEIT);
        System.out.println("Converted temp: " + temp);

        // Case 2: Parameter Object
        Card aceOfSpades = new Card(1, "Spades");
        SignatureDesign game = new SignatureDesign();
        System.out.println("Has card: " + game.hasCard(aceOfSpades));

        // Case 3: Interface flexibility
        game.processData(Map.of("Key", "Value")); // Works with any Map implementation
    }
}