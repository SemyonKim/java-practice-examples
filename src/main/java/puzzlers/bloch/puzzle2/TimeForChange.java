package puzzlers.bloch.puzzle2;

import java.math.BigDecimal;

/**
 * Time for a Change
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   A naive way to represent money is to use floating-point types (`double`).
 *   For example:
 *     double funds = 1.00;
 *     int itemsBought = 0;
 *     for (double price = 0.10; funds >= price; price += 0.10) {
 *         funds -= price;
 *         itemsBought++;
 *     }
 *     System.out.println(itemsBought + " items bought, remaining funds = " + funds);
 * <p>
 * Expected behavior:
 *   Buy 10 items at prices 0.10, 0.20, ..., 1.00 with no leftover funds.
 * <p>
 * Actual behavior:
 *   Due to floating-point rounding errors, the loop terminates early.
 *   Example output:
 *     4 items bought, remaining funds = 0.3999999999999999
 * <p>
 * Lesson:
 *   Floating-point types (`float`, `double`) cannot precisely represent decimal
 *   fractions like 0.10. This makes them unsuitable for monetary calculations.
 * <p>
 * Key takeaway:
 *   Use integer types (`int`, `long`) to represent cents, or `BigDecimal` for
 *   exact decimal arithmetic when working with money.
 */
public class TimeForChange {

    // Naive implementation using double
    static void doubleMoneyExample() {
        double funds = 1.00;
        int itemsBought = 0;
        for (double price = 0.10; funds >= price; price += 0.10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println("[double] " + itemsBought + " items bought, remaining funds = " + funds);
    }

    // Correct implementation using int (cents)
    static void intMoneyExample() {
        int funds = 100; // cents
        int itemsBought = 0;
        for (int price = 10; funds >= price; price += 10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println("[int] " + itemsBought + " items bought, remaining funds = " + funds + " cents");
    }

    // Correct implementation using BigDecimal
    static void bigDecimalMoneyExample() {
        BigDecimal funds = new BigDecimal("1.00");
        int itemsBought = 0;
        for (BigDecimal price = new BigDecimal("0.10");
             funds.compareTo(price) >= 0;
             price = price.add(new BigDecimal("0.10"))) {
            funds = funds.subtract(price);
            itemsBought++;
        }
        System.out.println("[BigDecimal] " + itemsBought + " items bought, remaining funds = " + funds);
    }

    public static void main(String[] args) {
        doubleMoneyExample();
        intMoneyExample();
        bigDecimalMoneyExample();
    }
}