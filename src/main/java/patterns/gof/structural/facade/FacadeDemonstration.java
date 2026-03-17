package patterns.gof.structural.facade;

import java.io.InputStream;
import java.util.List;

/**
 * ============================================================================
 * DESIGN PATTERN: Facade
 * CATEGORY:       Structural
 * ALSO KNOWN AS:  API Gateway (in modern distributed architectures)
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Provide a unified interface to a set of interfaces in a subsystem.
 * Facade defines a higher-level interface that makes the subsystem easier to use.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A compiler subsystem consists of complex components like a Scanner, Parser,
 * BytecodeStream, and ProgramNodeBuilder. Most users just want to "compile"
 * code and don't care about the Abstract Syntax Tree generation. A Compiler
 * class (the Facade) handles the complex orchestration of these components.
 * + Analogy: Ordering food at a restaurant. You talk to the waiter (Facade).
 * You don't go to the kitchen to talk to the chef, the sous-chef, and the
 * dishwasher to orchestrate your meal.
 * <p>
 * 3. APPLICABILITY
 * Use when you want to provide a simple, default view to a complex subsystem.
 * Use when you want to decouple clients from the complex internal dependencies
 * of a module or library.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Facade (Compiler): Exposes a simple method to clients.
 * - Subsystem Classes (Scanner, Parser, etc.): Handle the actual heavy lifting.
 * - Client: Calls the Facade to avoid direct interaction with subsystems.
 * <p>
 * 5. COLLABORATIONS
 * Client requests an action from the Facade. The Facade delegates and coordinates
 * the necessary actions across the subsystem components.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * Pros: Weakens coupling, improves modularity, hides complexity.
 * Cons: Can become a "God Object" if not strictly scoped to a specific subsystem.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * + MODERN UPDATE: Java 9 Modules are perfect for the Facade pattern. The Facade
 * class is exported in `module-info.java`, while the Subsystem classes are kept
 * internal. Alternatively, use package-private visibility to hide subsystems.
 */

public class FacadeDemonstration {

    // ========================================================================
    // SUBSYSTEM MOCKS (The complex internal classes)
    // ========================================================================
    // Note: In a real system, these would likely be package-private to force
    // clients to use the Facade.

    static class Token {
        String value;
        Token(String value) { this.value = value; }
    }

    static class Scanner {
        public List<Token> tokenize(InputStream stream) {
            System.out.println("   [Scanner] Tokenizing input stream...");
            return List.of(new Token("class"), new Token("Main"));
        }
    }

    static class ProgramNode {
        String name;
        ProgramNode(String name) { this.name = name; }
    }

    static class ProgramNodeBuilder {
        public ProgramNode buildNode(List<Token> tokens) {
            System.out.println("   [Builder] Building abstract syntax tree...");
            return new ProgramNode("ASTRoot");
        }
    }

    static class Parser {
        public void parse(Scanner scanner, ProgramNodeBuilder builder, InputStream stream) {
            System.out.println("   [Parser] Coordinating scanner and builder...");
            List<Token> tokens = scanner.tokenize(stream);
            builder.buildNode(tokens);
        }
    }

    static class BytecodeStream {
        public void generate(ProgramNode rootNode) {
            System.out.println("   [BytecodeStream] Translating AST to machine bytecode...");
        }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * The client has to know about all internal components and orchestrate
     * the exact sequence of events manually.
     */
    static class NaiveClient {
        public void compileAndRun(InputStream sourceCode) {
            Scanner scanner = new Scanner();
            ProgramNodeBuilder builder = new ProgramNodeBuilder();
            Parser parser = new Parser();
            BytecodeStream bytecodeStream = new BytecodeStream();

            // Client carries the burden of orchestration
            parser.parse(scanner, builder, sourceCode);
            ProgramNode root = builder.buildNode(List.of()); // Simplified for mock
            bytecodeStream.generate(root);

            System.out.println("   [Client] Executing compiled code...");
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * The Facade hides the orchestration. The interface is clean and simple.
     */
    static class CompilerFacade {

        // The facade hides the complex dependencies
        public void compile(InputStream sourceCode) {
            Scanner scanner = new Scanner();
            ProgramNodeBuilder builder = new ProgramNodeBuilder();
            Parser parser = new Parser();
            BytecodeStream bytecodeStream = new BytecodeStream();

            parser.parse(scanner, builder, sourceCode);
            // In reality, parser builds the tree and we fetch it:
            ProgramNode astRoot = builder.buildNode(null);
            bytecodeStream.generate(astRoot);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        InputStream mockSource = InputStream.nullInputStream();

        System.out.println("--- Facade Pattern: Naive Approach ---");
        System.out.println("Client must orchestrate Scanner, Parser, and Builders manually:");
        NaiveClient naiveClient = new NaiveClient();
        naiveClient.compileAndRun(mockSource);

        System.out.println("\n--- Facade Pattern: Pattern Approach ---");
        System.out.println("Client just calls compile() on the Facade:");
        CompilerFacade compiler = new CompilerFacade();
        compiler.compile(mockSource);
        System.out.println("   [Client] Executing compiled code without knowing how it compiled...");
    }
}