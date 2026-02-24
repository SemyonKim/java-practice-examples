package puzzlers.bloch.classier.puzzle4.click;

public class CodeTalk {
    public void doIt() {
        printMessage();
    }

    // Package-private: invisible to the 'hack' package
    void printMessage() {
        System.out.println("Click");
    }
}
