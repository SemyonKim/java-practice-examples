# Interpreter - Class Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](InterpreterDemonstration.java)**

## Intent & Core Problem
Given a specific language, the Interpreter pattern defines a representation for its grammar and provides an interpreter that uses this representation to evaluate sentences within that language.

**☕ Java Insight: Sealed Interfaces & Pattern Matching**
> In modern Java, interpreting languages or evaluating Abstract Syntax Trees (ASTs) has been revolutionized by `sealed` interfaces and pattern matching for `switch`. You can now strongly type your grammar hierarchy and safely evaluate it without always relying on the classic polymorphic `interpret()` method or the Visitor pattern.
```java
// Modern Java approach for Interpreter node evaluation
public sealed interface Expression permits Literal, Alternation {}
public record Literal(String text) implements Expression {}
public record Alternation(Expression left, Expression right) implements Expression {}

// Interpreter logic extracted using pattern matching
String interpret(Expression expr) {
    return switch(expr) {
        case Literal(var text) -> text;
        case Alternation(var l, var r) -> interpret(l) + "|" + interpret(r);
    };
}
```

---

## Motivation & Real-World Analogy
When a particular domain problem occurs frequently, it often makes sense to express instances of that problem as sentences in a specialized, simple language. You can then build an interpreter to solve the problem by evaluating these sentences.

A classic example is searching for strings that match a specific pattern. Regular expressions form a standard language for defining such search patterns. Instead of writing custom algorithms for every conceivable search criteria, you write a general algorithm that interprets a regular expression defining the target strings.

The Interpreter pattern dictates how to define the grammar, represent sentences, and interpret them. For regular expressions, consider a simplified grammar:
* `expression ::= literal | alternation | sequence | repetition | '(' expression ')'`
* `alternation ::= expression '|' expression`
* `sequence ::= expression '&' expression`
* `repetition ::= expression '*'`
* `literal ::= 'a' | 'b' | 'c' | ... { 'a' | 'b' | 'c' | ... }*`

Here, `expression` is the start symbol. The pattern translates this grammar directly into an object-oriented class hierarchy.

### Diagram 1: Regular Expression Class Hierarchy

```text
                                  ┌──────────────────────────┐
                                  │    RegularExpression     │
                                  ├──────────────────────────┤
                                  │ Interpret(Context)       │
                                  └────────────┬─────────────┘
                                               △
                                               │
           ┌───────────────────────────────────┴───────────────────────────────────┐
           │                                                                       │
 ┌─────────┴─────────┐   ┌───────────────────────┐   ┌────────────────────┐   ┌────┴─────────────────┐
 │ LiteralExpression │   │ AlternationExpression │   │ SequenceExpression │   │ RepetitionExpression │
 ├───────────────────┤   ├───────────────────────┤   ├────────────────────┤   ├──────────────────────┤
 │ Interpret(Context)│   │ Interpret(Context)    │   │ Interpret(Context) │   │ Interpret(Context)   │
 └───────────────────┘   └──────────┬────────────┘   └─────────┬──────────┘   └─────────┬────────────┘
                                    │                          │                        │
                                    ◇ alternation1, 2          ◇ expression1, 2         ◇ repetition
                                    │                          │                        │
                                    └──────────────────────────┴────────────────────────┘
                                               (Aggregates RegularExpression)
```
**Diagram Description:** The root `RegularExpression` abstract class dictates the `Interpret(Context)` interface. Leaf nodes like `LiteralExpression` implement specific terminal evaluations. Composite nodes (`AlternationExpression`, `SequenceExpression`, `RepetitionExpression`) maintain references back to the `RegularExpression` interface (indicated by the composition diamonds), representing their child expressions.

**☕ Java Insight: Immutable AST Nodes**
> Tree nodes representing grammar components are inherently static once parsed. In Java, representing these structural nodes using `record`s ensures immutability, provides free `equals`/`hashCode` implementations (crucial for expression caching), and dramatically reduces boilerplate.

### Diagram 2: Abstract Syntax Tree Example
Every sentence in the defined language is represented as an Abstract Syntax Tree (AST) composed of these classes. For example, the regex `raining & (dogs | cats) *` translates to:

```text
                            ┌────────────────────┐
                            │ SequenceExpression │
                            └─────────┬──────────┘
                                      │
                 ┌────────────────────┴────────────────────┐
                 ▼                                         ▼
      ┌───────────────────┐                   ┌──────────────────────┐
      │ LiteralExpression │                   │ RepetitionExpression │
      │   'raining'       │                   └────────────┬─────────┘
      └───────────────────┘                                │
                                                           ▼
                                              ┌───────────────────────┐
                                              │ AlternationExpression │
                                              └────────────┬──────────┘
                                                           │
                                         ┌─────────────────┴─────────────────┐
                                         ▼                                   ▼
                              ┌───────────────────┐                 ┌───────────────────┐
                              │ LiteralExpression │                 │ LiteralExpression │
                              │      'dogs'       │                 │      'cats'       │
                              └───────────────────┘                 └───────────────────┘
```
**Diagram Description:** An instantiation of the grammar classes. The `SequenceExpression` evaluates its left child (`raining`) and its right child (the repeated alternation). The evaluation dynamically traverses down to the `LiteralExpression` leaves.

---

## Applicability
Use the Interpreter pattern when you have a language to interpret, you can represent statements in that language as abstract syntax trees, and:
* **The grammar is simple.** Complex grammars result in massive class hierarchies that become unmanageable. For complex languages, tools like parser generators (e.g., ANTLR) are better suited.
* **Efficiency is not the primary concern.** Interpreters built from ASTs are typically slower than direct machine-code compilation or optimized state machines. Highly efficient interpreters often translate the AST into an intermediate form first.

---

## Structure & Participants

### Diagram 3: Common Structure

```text
                                        ┌─────────┐
                   ┌───────────────────>│ Context │
                   │                    └─────────┘
                   │                         
                   │                         
     ┌────────┐    │               ┌────────────────────┐
     │ Client │────┴──────────────>│ AbstractExpression │<───────────────────────────┐
     └────────┘                    ├────────────────────┤                            │
                                   │ Interpret(Context) │                            │
                                   └─────────┬──────────┘                            │
                                             ^                                       │
                                             │                                       │
                        ┌────────────────────┴────────────────────┐                  │
                        │                                         │                  │
              ┌─────────┴──────────┐                    ┌─────────┴─────────────┐    │
              │ TerminalExpression │                    │ NonterminalExpression │<>──┘
              ├────────────────────┤                    ├───────────────────────┤
              │ Interpret(Context) │                    │ Interpret(Context)    │
              └────────────────────┘                    └───────────────────────┘
```

**Participants:**
1.  **AbstractExpression (`RegularExpression`):** Declares an abstract `Interpret` operation common to all nodes in the AST.
2.  **TerminalExpression (`LiteralExpression`):** Implements an `Interpret` operation associated with terminal symbols in the grammar. An instance is required for every terminal symbol in a sentence.
3.  **NonterminalExpression (`AlternationExpression`, `RepetitionExpression`, `SequenceExpressions`):** Represents rules in the grammar. Requires instance variables of type `AbstractExpression` for each symbol on the right side of the rule. It implements `Interpret` by recursively calling `Interpret` on its child expressions.
4.  **Context:** Contains global information or state accessible to the interpreter.
5.  **Client:** Builds (or is given) the abstract syntax tree representing a particular sentence and invokes the `Interpret` operation.

**☕ Java Insight: Functional Contexts**
> Instead of a heavyweight `Context` class, modern Java often utilizes `java.util.function` interfaces or an immutable `Map<String, Object>` passed through the traversal, maintaining thread-safety when interpreting the same AST across multiple threads.

---

## Collaborations
* The `Client` constructs or receives the AST assembled from `NonterminalExpression` and `TerminalExpression` instances.
* The `Client` initializes the `Context` and invokes the `Interpret` operation.
* `NonterminalExpression` nodes route the `Interpret` calls down to their child subexpressions.
* `TerminalExpression` nodes compute the base evaluations directly, frequently querying or updating the `Context`.

---

## Consequences (Trade-offs)
* **Easily modifiable and extensible grammar:** Because classes represent grammar rules, inheritance can easily derive new expressions or modify existing ones.
* **Straightforward implementation:** Classes defining the nodes have similar structures. They are generally easy to write, and often can be generated automatically by a compiler/parser tool.
* **Complex grammars are hard to maintain:** The pattern mandates at least one class for every rule in the grammar. A grammar with dozens or hundreds of rules quickly becomes a maintenance burden.
* **Adding new operations:** The pattern allows you to evaluate expressions in new ways (e.g., pretty-printing, type-checking). If operations change frequently, using the **Visitor** pattern prevents polluting the grammar classes.

---

## Implementation Hints & Modern Java Context
1.  **Creating the AST:** The Interpreter pattern does *not* specify how the AST is parsed or generated. The tree can be generated by a recursive descent parser, a table-driven parser, or manually by the client. In Java, tools like ANTLR or JavaCC are typically used to map raw text to these object structures.
2.  **Defining the `Interpret` operation:** You do not have to strictly define `interpret()` in the expression classes. If you need to perform multiple distinct operations (evaluate, type-check, translate), pushing these into a separate `Visitor` or using Java 21's Pattern Matching over a `sealed` interface is significantly cleaner.
3.  **Sharing terminal symbols:** If terminal symbols appear repeatedly and maintain no intrinsic state (only extrinsic state passed via `Context`), they can be shared using the **Flyweight** pattern.

**☕ Java Insight: Combining with other patterns**
> The AST itself is almost always a **Composite** pattern. Modern Java collections (`List.of`, `Set.of`) make initializing these unmodifiable composite structures safe and concise.

---

## Known Uses & Java API Usage
* **`java.util.regex.Pattern`:** Although heavily optimized under the hood, standard Regular Expressions conceptually stem from this pattern. The `Pattern` object acts as the compiled AST, and the `Matcher` acts as the Context holding the execution state.
* **Spring Expression Language (SpEL):** Frameworks like Spring use `ExpressionParser` to parse strings like `"#user.name"` into an `Expression` (the AST), which is then evaluated against an `EvaluationContext`.
* **Java Unified Expression Language (EL):** Used heavily in Jakarta EE (JSP/JSF). Textual expressions are parsed into `ValueExpression` objects which are evaluated dynamically at runtime using an `ELContext`.

---

## Related Patterns
* **Composite:** The abstract syntax tree is fundamentally an instance of the Composite pattern.
* **Flyweight:** Shows how to share terminal symbols within the abstract syntax tree to reduce memory overhead.
* **Iterator:** The interpreter can use an Iterator to traverse the structure if sequence evaluations are required.
* **Visitor:** Can be used to centralize operations (like pretty-printing or type-checking) on the AST into a single class rather than scattering them across all node classes.