package exceptions.restrictions;

/*
 * Exception Restrictions – Key Rules
 *
 * 1. Methods in derived classes cannot throw broader (new) checked exceptions
 *    than those declared in the base class.
 * 2. Methods may throw fewer or narrower exceptions (subclasses of declared ones),
 *    or none at all.
 * 3. Constructors may declare any exceptions, but must also declare base-class
 *    constructor exceptions.
 * 4. Interfaces cannot widen exception specifications of methods already defined
 *    in a base class.
 * 5. New interface methods (not in base class) may declare exceptions freely.
 * 6. Substitutability is preserved: overriding methods must conform to base
 *    exception specifications to avoid breaking client code.
 * 7. Exception specifications are enforced by the compiler, but are not part
 *    of a method’s type (name + arguments only).
 * 8. You cannot overload methods based solely on exception specifications.
 */

// Base exception hierarchy for baseball-related events
class BaseballException extends Exception {
}

class Foul extends BaseballException {
}

class Strike extends BaseballException {
}

// Abstract class representing a baseball inning
abstract class Inning {
    // Constructor declares it throws BaseballException,
    // even though it doesn’t actually throw anything.
    // This forces derived constructors to acknowledge possible exceptions.
    Inning() throws BaseballException {
    }

    // Declares it throws BaseballException, but doesn’t actually throw.
    // Legal: allows overridden versions to throw exceptions.
    public void event() throws BaseballException {
        // Doesn't actually have to throw anything
    }

    // Abstract method declares checked exceptions.
    // Derived classes must conform to this specification.
    public abstract void atBat() throws Strike, Foul;

    // Method with no checked exceptions.
    public void walk() {
    }
}

// Storm-related exception hierarchy
class StormException extends Exception {
}

class RainedOut extends StormException {
}

// PopFoul is a more specific type of Foul
class PopFoul extends Foul {
}

// Interface describing storm behavior
interface Storm {
    // Declares throwing RainedOut
    void event() throws RainedOut;

    void rainHard() throws RainedOut;
}

// Derived class combining baseball inning and storm behavior
public class StormyInning extends Inning implements Storm {
    // Constructors can add new exceptions,
    // but must also declare base-class constructor exceptions.
    public StormyInning() throws RainedOut, BaseballException {
    }

    public StormyInning(String s) throws BaseballException {
    }

    // Attempting to throw new exceptions in walk() would break substitutability:
    // - void walk() throws PopFoul {} // Compile error

    // Interface cannot widen exception specification of existing base methods:
    // - public void event() throws RainedOut {} // Compile error

    // New interface methods not in base class can throw exceptions freely:
    @Override
    public void rainHard() throws RainedOut {
    }

    // Derived method can choose not to throw exceptions,
    // even if base version declares them.
    @Override
    public void event() {
    }

    // Overridden methods can throw *narrower* exceptions:
    // PopFoul extends Foul, so this is allowed.
    @Override
    public void atBat() throws PopFoul {
    }

    public static void main(String[] args) {
        try {
            StormyInning si = new StormyInning();
            si.atBat();
        } catch (PopFoul e) {
            System.out.println("Pop foul");
        } catch (RainedOut e) {
            System.out.println("Rained out");
        } catch (BaseballException e) {
            System.out.println("Generic baseball exception");
        }
        // Strike not thrown in derived version.

        try {
            // Upcasting to base type changes exception requirements:
            Inning i = new StormyInning();
            i.atBat();
            // Must catch exceptions declared in base-class method signature.
        } catch (Strike e) {
            System.out.println("Strike");
        } catch (Foul e) {
            System.out.println("Foul");
        } catch (RainedOut e) {
            System.out.println("Rained out");
        } catch (BaseballException e) {
            System.out.println("Generic baseball exception");
        }
    }
}