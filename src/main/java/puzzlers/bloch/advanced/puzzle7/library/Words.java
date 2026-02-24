package puzzlers.bloch.advanced.puzzle7.library;

public class Words {
    private Words(){} // Uninstantiable

    // Step 1: Compile both library and client classes
    public static final String FIRST = "the";
    public static final String SECOND = null;
    public static final String THIRD = "set";

    // Step 2: UPDATE
    // Update and recompile the library.Words class
    // Do not recompile the client.PrintWords
    //The Output in client.PrintWords: "the chemistry set"
//    public static final String FIRST = "physics";
//    public static final String SECOND = "chemistry";
//    public static final String THIRD = "biology";

    // Step 3: FIX
    /* // THE FIX: Use a method to prevent inlining
    public static final String FIRST = ident("physics");
    public static final String SECOND = ident("chemistry");
    public static final String THIRD = ident("biology");

    private static String ident(String s) { return s; }
    */
}