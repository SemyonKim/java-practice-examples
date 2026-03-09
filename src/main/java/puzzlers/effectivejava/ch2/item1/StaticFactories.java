package puzzlers.effectivejava.ch2.item1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <h2>Consider static factory methods instead of constructors</h2>
 *
 * <p><strong>Core Principle:</strong> Provide public static factory methods (SFMs)
 * instead of, or in addition to, constructors to gain naming clarity and instance control.</p>
 *
 * <ul>
 * <li><b>Advantage 1:</b> Unlike constructors, they have names (clarity).</li>
 * <li><b>Advantage 2:</b> Not required to create a new object every time (caching/Flyweight).</li>
 * <li><b>Advantage 3:</b> Can return an object of any subtype of their return type (flexibility).</li>
 * <li><b>Advantage 4:</b> The class of the returned object can vary based on input parameters.</li>
 * <li><b>Advantage 5:</b> The returned class need not exist when the SFM is written (Service Provider Frameworks).</li>
 * </ul>
 *
 * <ul>
 * <li><b>Limitation 1:</b> Classes without public/protected constructors cannot be subclassed.</li>
 * <li><b>Limitation 2:</b> They are not readily distinguishable from other static methods in Javadoc.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item3 Singleton
 * @see puzzlers.effectivejava.ch4.item17 ImmutableClasses
 * @see puzzlers.effectivejava.ch4.item20 InterfacesOverAbstractClasses
 */
public class StaticFactories {

    /**
     * <b>Advantage 1: Descriptive Names</b>
     * SFMs describe the object being returned, unlike constructors which are tied to the class name.
     */
    public void advantage01_Names() {
        // Constructor is ambiguous: What does 128, 5 mean?
        BigInteger prime1 = new BigInteger(128, 5, new Random());

        // SFM is explicit: We are getting a probable prime.
        BigInteger prime2 = BigInteger.probablePrime(128, new Random());
    }

    /**
     * <b>Advantage 2: Instance Control (Caching)</b>
     * Immutable classes can use pre-constructed instances to avoid unnecessary object creation.
     */
    public void advantage02_InstanceControl() {
        // Boolean.valueOf never creates a new object; it returns TRUE or FALSE constants.
        Boolean b1 = Boolean.valueOf(true);
        Boolean b2 = Boolean.valueOf(true);

        System.out.println(b1 == b2); // true (Same memory reference)
    }

    /**
     * <b>Advantage 3: Subtype Flexibility</b>
     * An API can return objects without making their implementation classes public.
     */
    public List<String> advantage03_SubtypeFlexibility() {
        List<String> list = new ArrayList<>();
        // Returns an instance of a private implementation class (java.util.Collections$UnmodifiableList)
        // The client only knows it's a 'List'.
        return Collections.unmodifiableList(list);
    }

    /**
     * <b>Advantage 4: Variable Return Types</b>
     * Returns different types depending on the input size.
     */
    public void advantage04_InputVariableReturns() {
        // EnumSet has no public constructor.
        // If enum size <= 64, it returns RegularEnumSet.
        // If enum size > 64, it returns JumboEnumSet.
        // Set<MyEnum> set = EnumSet.noneOf(MyEnum.class);
    }

    /**
     * <b>Advantage 5: Service Provider Frameworks</b>
     * Basis of JDBC: The implementation (Driver) is decoupled from the client.
     */
    public void advantage05_ServiceProvider() {
        // Connection is the service interface.
        // DriverManager.getConnection is the static factory (Service Access API).
        // The specific Driver (implementation) is registered at runtime.
        // Connection conn = DriverManager.getConnection("url");
    }

    /**
     * <b>Limitation: Subclassing & Discoverability</b>
     * Classes without public/protected constructors cannot be subclassed.
     * SFMs are also harder to find than constructors in standard Javadoc.
     */
    public void namingConventions() {
        // from: Date d = Date.from(instant);
        // of: Set<String> s = Set.of("A", "B");
        // valueOf: BigInteger b = BigInteger.valueOf(100L);
        // instance / getInstance: StackWalker.getInstance(options);
        // create / newInstance: Array.newInstance(class, length);
        // getType / newType: Files.newBufferedReader(path)
    }
}