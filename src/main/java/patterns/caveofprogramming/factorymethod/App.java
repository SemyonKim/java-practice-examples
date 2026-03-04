package patterns.caveofprogramming.factorymethod;

/**
 * <h1>Factory Method Pattern</h1>
 * <p>
 * <b>Definition:</b>
 * Define an interface for creating an object, but let subclasses decide which class to instantiate.
 * The Factory Method lets a class defer instantiation to subclasses.
 * <p>
 * <b>Official Recommendations & Best Practices:</b>
 * <ul>
 * <li><b>Dependency Inversion Principle:</b> Depend upon abstractions, not concrete classes.
 * The factory helps achieve this by hiding the 'new' keyword.</li>
 * <li><b>Single Responsibility Principle:</b> Move the object creation code into one place
 * (the factory), making the code easier to maintain.</li>
 * <li><b>Open/Closed Principle:</b> You can introduce new types of products into the program
 * without breaking existing client code.</li>
 * <li><b>Usage:</b> Use this pattern when you don't know beforehand the exact types and
 * dependencies of the objects your code should work with.</li>
 * </ul>
 */
public abstract class App {

    /**
     * This is the "Factory Method".
     * Instead of calling 'new EmailAlert()', the application calls this method.
     * * @return A concrete instance of a Notification.
     */
    public abstract Notification createNotification();

    /**
     * Core logic that uses the product created by the factory method.
     */
    public void sendAlert() {
        Notification notification = createNotification();
        notification.notifyUser();
    }
}

/**
 * The Product Interface.
 */
interface Notification {
    void notifyUser();
}

/**
 * Concrete Product 1: Email
 */
class EmailNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending an Email notification...");
    }
}

/**
 * Concrete Product 2: SMS
 */
class SMSNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending an SMS notification...");
    }
}

/**
 * Concrete Creator for Email.
 */
class EmailApp extends App {
    @Override
    public Notification createNotification() {
        return new EmailNotification();
    }
}

/**
 * Concrete Creator for SMS.
 */
class SMSApp extends App {
    @Override
    public Notification createNotification() {
        return new SMSNotification();
    }
}