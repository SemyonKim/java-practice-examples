package advanced.tagginginterfaces;

import java.util.function.Supplier;

// ============================================================
// Suggested Order of Reading This Code
// 1. Null interface → establishes the tagging mechanism.
// 2. Robot interface → defines the contract (name, model, operations).
// 3. Operation class → shows how robot actions are represented.
// 4. SnowRobot class → concrete implementation of a real robot.
// 5. NullRobotProxyHandler → dynamic proxy + null object pattern.
// 6. NullRobot utility class → factory method + demonstration.
// ============================================================

// 3. Operation class
// Operation contains a description and a command (it’s a type of Command pattern).
// These are defined as references to functional interfaces so you can pass lambda
// expressions or method references to the Operation constructor:
public class Operation {
    public final Supplier<String> description;
    public final Runnable command;

    public Operation(Supplier<String> descr, Runnable cmd) {
        description = descr;
        command = cmd;
    }
}