package puzzlers.effectivejava.ch9.item60;

import java.math.BigDecimal;

/**
 * <h2>Avoid float and double if exact answers are required</h2>
 *
 * <p>
 * <b>Core Principle:</b> The {@code float} and {@code double} types are designed for scientific
 * and engineering approximations. They use binary floating-point arithmetic, which cannot
 * accurately represent base-10 decimals like 0.1. For exact results—especially in
 * monetary calculations—use {@code BigDecimal}, {@code int}, or {@code long}.
 * </p>
 *
 * <h3>Advantages of Alternatives</h3>
 * <ul>
 * <li><b>BigDecimal:</b>
 * <ul>
 * <li><b>Precision:</b> Provides exact results for decimal fractions of any size.</li>
 * <li><b>Control:</b> Offers full control over rounding via eight different rounding modes.</li>
 * <li><b>Convenience:</b> The system manages the decimal point for you.</li>
 * </ul>
 * </li>
 * <li><b>int / long:</b>
 * <ul>
 * <li><b>Performance:</b> Extremely fast primitive arithmetic.</li>
 * <li><b>Familiarity:</b> Uses standard operators (+, -, *, /).</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>BigDecimal:</b> Much slower than primitive types and less convenient to write (requires
 * method calls instead of operators). Always use the <b>String constructor</b> to avoid
 * initial precision loss.</li>
 * <li><b>int / long:</b> Requires the developer to manually track the decimal point (e.g., storing
 * cents instead of dollars). Limited by magnitude: {@code int} handles up to 9 digits,
 * {@code long} up to 18 digits.</li>
 * </ul>
 *
 * @see java.math.BigDecimal
 * @see java.math.RoundingMode
 */
public class ExactArithmetic {

    /**
     * Broken: Uses double for monetary calculations.
     * Result: 3 items bought, Change: $0.3999999999999999
     */
    public void brokenDoubleExample() {
        double funds = 1.00;
        int itemsBought = 0;
        for (double price = 0.10; funds >= price; price += 0.10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println("Double - Items bought: " + itemsBought);
        System.out.println("Double - Change: $" + funds);
    }

    /**
     * Correct: Uses BigDecimal for exact results and rounding control.
     * Result: 4 items bought, Money left over: $0.00
     */
    public void correctBigDecimalExample() {
        final BigDecimal TEN_CENTS = new BigDecimal(".10");
        int itemsBought = 0;
        BigDecimal funds = new BigDecimal("1.00");

        for (BigDecimal price = TEN_CENTS;
             funds.compareTo(price) >= 0;
             price = price.add(TEN_CENTS)) {
            funds = funds.subtract(price);
            itemsBought++;
        }
        System.out.println("BigDecimal - Items bought: " + itemsBought);
        System.out.println("BigDecimal - Money left over: $" + funds);
    }

    /**
     * Correct & Fast: Uses primitive int by shifting the decimal point (cents).
     * Result: 4 items bought, Cash left over: 0 cents
     */
    public void correctPrimitiveExample() {
        int funds = 100; // Total cents
        int itemsBought = 0;
        for (int price = 10; funds >= price; price += 10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println("Int (cents) - Items bought: " + itemsBought);
        System.out.println("Int (cents) - Cash left over: " + funds + " cents");
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ExactArithmetic demo = new ExactArithmetic();

        // 1. The Naive (Broken) approach
        System.out.println("--- Broken Double Demo ---");
        demo.brokenDoubleExample();

        // 2. The Robust approach (BigDecimal)
        System.out.println("\n--- Robust BigDecimal Demo ---");
        demo.correctBigDecimalExample();

        // 3. The Performance-Critical approach (int/long)
        System.out.println("\n--- Performance-Critical Int Demo ---");
        demo.correctPrimitiveExample();

        // Quick illustration of why rounding doubles doesn't always work
        System.out.println("\nNaive subtraction: 1.03 - 0.42 = " + (1.03 - 0.42));
    }
}