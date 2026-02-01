package advanced.tagginginterfaces;

import java.util.List;

// ============================================================
// Suggested Order of Reading This Code
// 1. Null interface → establishes the tagging mechanism.
// 2. Robot interface → defines the contract (name, model, operations).
// 3. Operation class → shows how robot actions are represented.
// 4. SnowRobot class → concrete implementation of a real robot.
// 5. NullRobotProxyHandler → dynamic proxy + null object pattern.
// 6. NullRobot utility class → factory method + demonstration.
// ============================================================

// 2. Robot interface
// If you work with interfaces instead of concrete classes, you can use a DynamicProxy
// to automatically create the Nulls. Suppose we have a Robot interface that defines a
// name, model, and a List<Operation> that describes what the Robot does:
public interface Robot {
    String name();
    String model();
    List<Operation> operations();

    // Robot also incorporates a static method to perform tests.
    // You access a Robot’s services by calling operations().
    // If r is a Null, it prints "[Null Robot]".
    // Otherwise, it prints the Robot’s name, model, and executes its operations.
    static void test(Robot r) {
        if(r instanceof Null)
            System.out.println("[Null Robot]");
        System.out.println("Robot name: " + r.name());
        System.out.println("Robot model: " + r.model());
        for(Operation operation : r.operations()) {
            System.out.println(operation.description.get());
            operation.command.run();
        }
    }
}