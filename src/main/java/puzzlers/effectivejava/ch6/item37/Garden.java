package puzzlers.effectivejava.ch6.item37;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * <h2>Use EnumMap instead of ordinal indexing</h2>
 *
 * <p>
 * <b>Core Principle:</b> Avoid using {@code ordinal()} to index into arrays. This
 * practice is error-prone, lacks type safety, and is difficult to maintain.
 * Use {@link java.util.EnumMap}, which is specifically designed for enum keys
 * and provides the speed of an array with the richness of the {@code Map} interface.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Type Safety:</b> No unchecked casts are required, and the compiler
 * ensures you only use the correct enum type for keys.</li>
 * <li><b>Performance:</b> Comparable to array indexing because it uses an array
 * internally, but hides the "plumbing" from the developer.</li>
 * <li><b>Maintainability:</b> Adding or reordering enum constants does not
 * break the mapping logic, unlike manual array indexing.</li>
 * <li><b>Ease of Use:</b> Handles its own printable string representations
 * through the keys and integrates seamlessly with Java Streams.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Single Type:</b> The {@code EnumMap} can only hold keys from a single
 * enum type (though nesting allows for multidimensional relationships).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item33 BoundedTypeTokens
 * @see puzzlers.effectivejava.ch6.item35 OrdinalAbuse
 * @see puzzlers.effectivejava.ch7.item45 Streams
 */
public class Garden {

    public static class Plant {
        public enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

        final String name;
        final LifeCycle lifeCycle;

        Plant(String name, LifeCycle lifeCycle) {
            this.name = name;
            this.lifeCycle = lifeCycle;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // --- Complex Multi-dimensional Example: Phase Transitions ---

    public enum Phase {
        SOLID, LIQUID, GAS, PLASMA;

        public enum Transition {
            MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
            BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
            SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
            IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);

            private final Phase from;
            private final Phase to;

            Transition(Phase from, Phase to) {
                this.from = from;
                this.to = to;
            }

            // Nested EnumMap to map (from phase) -> (to phase) -> (transition)
            private static final Map<Phase, Map<Phase, Transition>> m =
                    Stream.of(values())
                            .collect(groupingBy(
                                    t -> t.from,
                                    () -> new EnumMap<>(Phase.class),
                                    toMap(
                                            t -> t.to,
                                            t -> t,
                                            (x, y) -> y,
                                            () -> new EnumMap<>(Phase.class)
                                    )
                            ));

            public static Transition from(Phase from, Phase to) {
                return m.get(from).get(to);
            }
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        List<Plant> garden = List.of(
                new Plant("Basil", Plant.LifeCycle.ANNUAL),
                new Plant("Lavender", Plant.LifeCycle.PERENNIAL),
                new Plant("Parsley", Plant.LifeCycle.BIENNIAL),
                new Plant("Rosemary", Plant.LifeCycle.PERENNIAL)
        );

        // 1. Basic EnumMap grouping
        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
        for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
            plantsByLifeCycle.put(lc, new HashSet<>());
        }
        for (Plant p : garden) {
            plantsByLifeCycle.get(p.lifeCycle).add(p);
        }
        System.out.println("Plants by LifeCycle: " + plantsByLifeCycle);

        // 2. Stream-based grouping into EnumMap
        Map<Plant.LifeCycle, Set<Plant>> streamGroup = garden.stream()
                .collect(groupingBy(
                        p -> p.lifeCycle,
                        () -> new EnumMap<>(Plant.LifeCycle.class),
                        toSet()
                ));
        System.out.println("Stream result: " + streamGroup);

        // 3. Nested Transition Lookup
        Phase.Transition trans = Phase.Transition.from(Phase.GAS, Phase.PLASMA);
        System.out.println("Gas to Plasma is called: " + trans);
    }
}