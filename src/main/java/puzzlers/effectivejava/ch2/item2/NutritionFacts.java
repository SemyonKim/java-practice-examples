package puzzlers.effectivejava.ch2.item2;

/**
 * <h2>Consider a builder when faced with many constructor parameters</h2>
 *
 * <p><strong>Core Principle:</strong> Use the Builder pattern when a class has many
 * optional parameters or multiple parameters of the same type to ensure safety,
 * readability, and to maintain immutability.</p>
 *
 * <ul>
 * <li><b>Advantage 1:</b> More readable and easier to write than telescoping constructors.</li>
 * <li><b>Advantage 2:</b> Unlike JavaBeans, it allows for immutability and ensures the object is never in an inconsistent state.</li>
 * <li><b>Advantage 3:</b> Simulates named optional parameters found in languages like Python and Scala.</li>
 * <li><b>Advantage 4:</b> Highly flexible; a single builder can create multiple objects with minor tweaks.</li>
 * </ul>
 *
 * <ul>
 * <li><b>Limitation 1:</b> Requires the creation of an intermediate builder object, which may impact performance-critical systems.</li>
 * <li><b>Limitation 2:</b> More verbose than constructors, making it best suited for 4+ parameters.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch4.item17 ImmutableClasses
 */
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // Required parameters
        private final int servingSize;
        private final int servings;

        // Optional parameters - initialized to default values
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) { calories = val; return this; }
        public Builder fat(int val) { fat = val; return this; }
        public Builder sodium(int val) { sodium = val; return this; }
        public Builder carbohydrate(int val) { carbohydrate = val; return this; }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }

    /**
     * <b>Advantage 1 & 3: Fluent API and Readability</b>
     * Demonstrates how the builder simulates named parameters and avoids the "magic number" confusion.
     */
    public void clientExample() {
        // Client code is easy to write and read.
        NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}