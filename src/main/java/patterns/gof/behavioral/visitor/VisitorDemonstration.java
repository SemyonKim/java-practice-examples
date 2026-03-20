package patterns.gof.behavioral.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * DESIGN PATTERN: Visitor
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Double-Dispatch
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Represent an operation to be performed on the elements of an object structure.
 * Visitor lets you define a new operation without changing the classes of the
 * elements on which it operates.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Imagine a compiler parsing code into an Abstract Syntax Tree (AST). You need
 * to generate code, type-check, and pretty-print the tree. Instead of stuffing
 * `generate()`, `typeCheck()`, and `print()` methods into every single AST node
 * class, you create distinct "Visitor" objects for each task that "visit" the
 * tree nodes.
 * <p>
 * 3. APPLICABILITY
 * - When an object structure contains many classes of objects with differing interfaces.
 * - When you need to perform distinct, unrelated operations without polluting classes.
 * - When the object structure rarely changes, but you frequently add new operations.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Visitor: Interface declaring visit methods for every ConcreteElement.
 * - ConcreteVisitor: Implements the operations (e.g., TypeCheckingVisitor).
 * - Element (Node): Interface declaring `accept(Visitor)`.
 * - ConcreteElement: Implements `accept` by calling `visitor.visit(this)`.
 * - ObjectStructure: Contains the elements and facilitates traversal.
 * ============================================================================
 */
public class VisitorDemonstration {

    /**
     * Helper Mock object to fulfill the AST context.
     */
    static class Variable {
        public String name;

        public Variable(String n) { this.name = n; }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Operations are tightly coupled directly into the AST nodes. Every time
     * a new tool (like a PrettyPrinter) is needed, every single class in the
     * hierarchy must be modified.
     */
    static class NaiveApproach {

        abstract static class NaiveNode {
            abstract void typeCheck();
            abstract void generateCode();
            // What if we want to add prettyPrint()? We'd have to edit everything!
        }

        static class NaiveAssignmentNode extends NaiveNode {
            @Override void typeCheck() { System.out.println("Naive: Type checking assignment."); }
            @Override void generateCode() { System.out.println("Naive: Generating assignment code."); }
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * We define a Visitor interface. The nodes only know how to "accept" a
     * visitor. The operations are decoupled and moved into Concrete Visitors.
     */

    // --- 1. Visitor Interface ---
    public interface NodeVisitor {
        void visitAssignment(AssignmentNode node);
        void visitVariableRef(VariableRefNode node);
    }

    // --- 2. Element Interface ---
    public interface Node {
        void accept(NodeVisitor visitor);
    }

    // --- 3. Concrete Elements ---
    public static class AssignmentNode implements Node {
        private final Variable variable;
        private final String expression;

        public AssignmentNode(Variable variable, String expression) {
            this.variable = variable;
            this.expression = expression;
        }

        public Variable getVariable() { return variable; }
        public String getExpression() { return expression; }

        @Override
        public void accept(NodeVisitor visitor) {
            // DOUBLE DISPATCH: We pass 'this' (the specific type) to the visitor.
            visitor.visitAssignment(this);
        }
    }

    public static class VariableRefNode implements Node {
        private final Variable variable;

        public VariableRefNode(Variable variable) {
            this.variable = variable;
        }

        public Variable getVariable() { return variable; }

        @Override
        public void accept(NodeVisitor visitor) {
            visitor.visitVariableRef(this);
        }
    }

    // --- 4. Concrete Visitors ---
    public static class TypeCheckingVisitor implements NodeVisitor {
        @Override
        public void visitAssignment(AssignmentNode node) {
            System.out.println("[TypeCheck] Ensuring expression '" + node.getExpression() +
                    "' matches type of variable '" + node.getVariable().name + "'");
        }

        @Override
        public void visitVariableRef(VariableRefNode node) {
            System.out.println("[TypeCheck] Checking if variable '" + node.getVariable().name + "' is declared.");
        }
    }

    public static class CodeGeneratingVisitor implements NodeVisitor {
        @Override
        public void visitAssignment(AssignmentNode node) {
            System.out.println("[CodeGen] Generating bytecode to store '" + node.getExpression() +
                    "' into '" + node.getVariable().name + "'");
        }

        @Override
        public void visitVariableRef(VariableRefNode node) {
            System.out.println("[CodeGen] Generating bytecode to load variable '" + node.getVariable().name + "'");
        }
    }

    // --- 5. Object Structure ---
    public static class Program {
        private final List<Node> nodes = new ArrayList<>();

        public void addNode(Node node) {
            nodes.add(node);
        }

        // Facilitates the traversal over the AST
        public void traverse(NodeVisitor visitor) {
            for (Node node : nodes) {
                node.accept(visitor);
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Visitor Pattern Naive Approach ---");
        NaiveApproach.NaiveNode naiveNode = new NaiveApproach.NaiveAssignmentNode();
        naiveNode.typeCheck();
        naiveNode.generateCode();

        System.out.println("\n--- Visitor Pattern Approach ---");
        // 1. Build the Object Structure (AST)
        Program ast = new Program();
        Variable xVar = new Variable("x");
        Variable yVar = new Variable("y");

        ast.addNode(new VariableRefNode(xVar));
        ast.addNode(new AssignmentNode(yVar, "10 + 5"));

        // 2. Instantiate different Visitors (Operations)
        NodeVisitor typeChecker = new TypeCheckingVisitor();
        NodeVisitor codeGenerator = new CodeGeneratingVisitor();

        // 3. Apply operations by passing visitors to the Object Structure
        System.out.println(">> Running Static Semantic Analysis (Type Checking):");
        ast.traverse(typeChecker);

        System.out.println("\n>> Running Compilation (Code Generation):");
        ast.traverse(codeGenerator);
    }
}