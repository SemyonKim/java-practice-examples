package patterns.gof.behavioral.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * DESIGN PATTERN: Interpreter
 * CATEGORY:       Behavioral
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Given a language, define a representation for its grammar along with an
 * interpreter that uses the representation to interpret sentences in the language.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * When you need to parse and evaluate simple expressions (like boolean logic,
 * math formulas, or search queries), hardcoding every combination is impossible.
 * By defining a class for each grammar rule, you can combine them into an
 * Abstract Syntax Tree (AST) that recursively evaluates itself.
 * Analogy: A music sheet. The notes are terminal expressions; chords/measures are
 * non-terminals. The musician (interpreter) reads the sheet (AST) based on their
 * training (context) to produce music.
 * <p>
 * 3. APPLICABILITY
 * - The grammar is simple (complex grammars bloat the class hierarchy).
 * - Efficiency is not a strict requirement (AST traversal can be slow).
 * - You need to evaluate sentences of a domain-specific language (DSL).
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - AbstractExpression (BooleanExp): Declares the evaluate interface.
 * - TerminalExpression (VariableExp, Constant): Leaf nodes handling direct values.
 * - NonterminalExpression (AndExp, OrExp, NotExp): Composite nodes delegating evaluation.
 * - Context (Context): Stores global mappings/variables used during evaluation.
 * - Client: Assembles the AST and triggers interpretation.
 * ============================================================================
 */
public class InterpreterDemonstration {

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Hardcoded logic that isn't flexible. If the rule changes at runtime,
     * the code must be recompiled. It is impossible to evaluate arbitrary
     * dynamic expressions cleanly.
     */
    static class NaiveBooleanEvaluator {
        public boolean evaluateHardcoded(boolean x, boolean y) {
            // (true and x) or (y and (not x))
            return (true && x) || (y && (!x));
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * The grammar is translated into an AST.
     * Modern Java Note: We use `sealed` interfaces to enforce strict
     * constraints over what constitutes a Boolean Expression, providing
     * better domain modeling and safety.
     */

    // Context: Holds the variables used in our sentences.
    static class Context {
        private final Map<String, Boolean> variables = new HashMap<>();

        public void assign(VariableExp exp, boolean value) {
            variables.put(exp.name(), value);
        }

        public boolean lookup(String name) {
            Boolean value = variables.get(name);
            if (value == null) {
                throw new IllegalArgumentException("Unbound variable: " + name);
            }
            return value;
        }
    }

    // AbstractExpression
    sealed interface BooleanExp permits Constant, VariableExp, AndExp, OrExp, NotExp {
        boolean evaluate(Context context);
        BooleanExp replace(String name, BooleanExp exp);
        BooleanExp copy();
    }

    // TerminalExpression
    // Modern Java Note: Records are a perfect fit for immutable Terminal nodes.
    record Constant(boolean value) implements BooleanExp {
        @Override
        public boolean evaluate(Context context) {
            return value;
        }

        @Override
        public BooleanExp replace(String name, BooleanExp exp) {
            return new Constant(value);
        }

        @Override
        public BooleanExp copy() {
            return new Constant(value);
        }
    }

    // TerminalExpression
    record VariableExp(String name) implements BooleanExp {
        @Override
        public boolean evaluate(Context context) {
            return context.lookup(name);
        }

        @Override
        public BooleanExp replace(String replaceName, BooleanExp exp) {
            if (this.name.equals(replaceName)) {
                return exp.copy();
            }
            return new VariableExp(name);
        }

        @Override
        public BooleanExp copy() {
            return new VariableExp(name);
        }
    }

    // NonterminalExpression
    record AndExp(BooleanExp operand1, BooleanExp operand2) implements BooleanExp {
        @Override
        public boolean evaluate(Context context) {
            return operand1.evaluate(context) && operand2.evaluate(context);
        }

        @Override
        public BooleanExp replace(String name, BooleanExp exp) {
            return new AndExp(operand1.replace(name, exp), operand2.replace(name, exp));
        }

        @Override
        public BooleanExp copy() {
            return new AndExp(operand1.copy(), operand2.copy());
        }
    }

    // NonterminalExpression
    record OrExp(BooleanExp operand1, BooleanExp operand2) implements BooleanExp {
        @Override
        public boolean evaluate(Context context) {
            return operand1.evaluate(context) || operand2.evaluate(context);
        }

        @Override
        public BooleanExp replace(String name, BooleanExp exp) {
            return new OrExp(operand1.replace(name, exp), operand2.replace(name, exp));
        }

        @Override
        public BooleanExp copy() {
            return new OrExp(operand1.copy(), operand2.copy());
        }
    }

    // NonterminalExpression
    record NotExp(BooleanExp operand) implements BooleanExp {
        @Override
        public boolean evaluate(Context context) {
            return !operand.evaluate(context);
        }

        @Override
        public BooleanExp replace(String name, BooleanExp exp) {
            return new NotExp(operand.replace(name, exp));
        }

        @Override
        public BooleanExp copy() {
            return new NotExp(operand.copy());
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Interpreter Naive Approach ---");
        NaiveBooleanEvaluator naive = new NaiveBooleanEvaluator();
        System.out.println("Naive result: " + naive.evaluateHardcoded(true, false));

        System.out.println("\n--- Interpreter Pattern Approach ---");
        // We want to evaluate: (true and x) or (y and (not x))
        VariableExp x = new VariableExp("X");
        VariableExp y = new VariableExp("Y");

        BooleanExp expression = new OrExp(
                new AndExp(new Constant(true), x),
                new AndExp(y, new NotExp(x))
        );

        Context context = new Context();
        context.assign(x, false);
        context.assign(y, true);

        // Interpretation
        boolean result = expression.evaluate(context);
        System.out.println("Evaluated AST Result: " + result);

        System.out.println("\n--- Replacing Variables within the AST ---");
        // Replace "Y" with a new sub-expression (Not Z)
        VariableExp z = new VariableExp("Z");
        context.assign(z, false);

        BooleanExp modifiedExpression = expression.replace("Y", new NotExp(z));

        // Evaluate the new tree: (true and false) or ((not false) and (not false))
        // -> false or (true and true) -> true
        boolean modifiedResult = modifiedExpression.evaluate(context);
        System.out.println("Evaluated Modified AST Result: " + modifiedResult);
    }
}