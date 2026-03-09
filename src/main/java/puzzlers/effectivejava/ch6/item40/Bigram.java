package puzzlers.effectivejava.ch6.item40;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <h2>Consistently use the Override annotation</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use the {@code @Override} annotation on every method declaration
 * that you believe overrides a supertype declaration. This allows the compiler to
 * protect you from accidental overloading and signature mismatches.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Error Detection:</b> The compiler generates an error if a method marked with
 * {@code @Override} does not actually override a method, catching typos and signature errors.</li>
 * <li><b>Bug Prevention:</b> Prevents "nefarious bugs" where a class accidentally overloads
 * a method (like {@code equals}) instead of overriding it, leading to broken collection behavior.</li>
 * <li><b>Documentation:</b> Clearly communicates to readers that the method is part of a
 * supertype's contract, improving code readability.</li>
 * <li><b>IDE Support:</b> Most IDEs use this annotation to provide warnings if you
 * inadvertently change a signature or forget to implement a method.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Redundancy in Concrete Classes:</b> In a non-abstract class, the compiler
 * already forces you to implement abstract methods, making {@code @Override} technically
 * optional there (though still recommended for clarity).</li>
 * <li><b>Clutter:</b> Some developers omit it for concrete implementations of
 * interface methods that lack {@code default} implementations to keep the code concise.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item10 EqualsContract
 * @see puzzlers.effectivejava.ch3.item11 HashCodeContract
 * @see puzzlers.effectivejava.ch8.item52 UseOverloadingJudiciously
 */
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    /*
     * BUGGY VERSION: Overloads equals instead of overriding it.
     * If we added @Override here, the compiler would slap our wrist.
     * public boolean equals(Bigram b) { ... }
     */

    /**
     * Correct implementation using @Override.
     * The compiler ensures the parameter is exactly 'Object'.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bigram)) {
            return false;
        }
        Bigram b = (Bigram) o;
        return b.first == first && b.second == second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }

        /*
         * If the 'equals' method above lacked @Override and used (Bigram b)
         * as a parameter, this would print 260.
         * With the correct override, it prints 26.
         */
        System.out.println("Set size (Expected 26): " + s.size());

        if (s.size() == 26) {
            System.out.println("Success: Annotations saved the day!");
        } else {
            System.out.println("Failure: Identity-based equals used.");
        }
    }
}