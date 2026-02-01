package advanced.tagginginterfaces;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

// ============================================================
// Suggested Order of Reading This Code
// 1. Null interface → establishes the tagging mechanism.
// 2. Robot interface → defines the contract (name, model, operations).
// 3. Operation class → shows how robot actions are represented.
// 4. SnowRobot class → concrete implementation of a real robot.
// 5. NullRobotProxyHandler → dynamic proxy + null object pattern.
// 6. NullRobot utility class → factory method + demonstration.
// ============================================================

// 5. NullRobotProxyHandler
// There will presumably be many different types of Robot,
// and we’d like each Null to do something special for each Robot type—
// here, incorporate information about the exact type of Robot the Null represents.
// This information is captured by the dynamic proxy:
class NullRobotProxyHandler implements InvocationHandler {
    private String nullName;
    private Robot proxied = new NRobot();

    // Constructor builds a descriptive name for the null robot,
    // based on the type of Robot being proxied.
    NullRobotProxyHandler(Class<? extends Robot> type) {
        nullName = type.getSimpleName() + " NullRobot";
    }

    // Inner class NRobot implements both Null and Robot.
    // It provides safe, do-nothing implementations:
    // - name() and model() return the descriptive nullName
    // - operations() returns an empty list
    private class NRobot implements Null, Robot {
        @Override
        public String name() { return nullName; }
        @Override
        public String model() { return nullName; }
        @Override
        public List<Operation> operations() {
            return Collections.emptyList();
        }
    }

    // The invoke method forwards all method calls on the proxy
    // to the proxied NRobot instance.
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        return method.invoke(proxied, args);
    }
}

// 6. NullRobot utility class
// Whenever you need a null Robot object, you just call newNullRobot(),
// passing your desired type of Robot, and it returns a proxy.
// The proxy fulfills the requirements of the Robot and Null interfaces,
// and provides the specific name of the type it proxies.
public class NullRobot {
    public static Robot newNullRobot(Class<? extends Robot> type) {
        return (Robot) Proxy.newProxyInstance(
                NullRobot.class.getClassLoader(),
                new Class[]{ Null.class, Robot.class },
                new NullRobotProxyHandler(type));
    }

    // Demonstration: testing both a real SnowRobot and a NullRobot proxy.
    public static void main(String[] args) {
        Stream.of(
                new SnowRobot("SnowBee"),
                newNullRobot(SnowRobot.class)
        ).forEach(Robot::test);
    }
}