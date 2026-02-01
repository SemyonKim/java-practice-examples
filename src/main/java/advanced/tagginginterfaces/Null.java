package advanced.tagginginterfaces;

// ============================================================
// Suggested Order of Reading This Code
// 1. Null interface → establishes the tagging mechanism.
// 2. Robot interface → defines the contract (name, model, operations).
// 3. Operation class → shows how robot actions are represented.
// 4. SnowRobot class → concrete implementation of a real robot.
// 5. NullRobotProxyHandler → dynamic proxy + null object pattern.
// 6. NullRobot utility class → factory method + demonstration.
// ============================================================

// 1. Null interface
// Sometimes it’s more convenient to use a tagging interface to indicate null-ness.
// A tagging interface has no elements; you just use its name as a tag:
public interface Null {
}