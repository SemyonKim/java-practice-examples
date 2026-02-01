package advanced.tagginginterfaces;

import java.util.Arrays;
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

// 4. SnowRobot class
// We can now create a Robot that removes snow:
public class SnowRobot implements Robot {
    private String name;

    public SnowRobot(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String model() {
        return "SnowBot Series 11";
    }

    // This Robot has a list of operations, each defined with a description and a command.
    // The descriptions are lambda expressions producing text,
    // and the commands are lambdas that print actions to the console.
    private List<Operation> ops = Arrays.asList(
            new Operation(
                    () -> name + " can shovel snow",
                    () -> System.out.println(name + " shoveling snow")),
            new Operation(
                    () -> name + " can chip ice",
                    () -> System.out.println(name + " chipping ice")),
            new Operation(
                    () -> name + " can clear the roof",
                    () -> System.out.println(name + " clearing roof"))
    );

    @Override
    public List<Operation> operations() {
        return ops;
    }

    // Demonstration: testing a real SnowRobot instance.
    public static void main(String[] args) {
        Robot.test(new SnowRobot("Slusher"));
    }
}