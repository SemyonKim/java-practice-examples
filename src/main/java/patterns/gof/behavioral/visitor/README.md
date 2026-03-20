# Visitor - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](VisitorDemonstration.java)**

## Intent & Core Problem
Represent an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements on which it operates.

**☕ Java Note: Modern Relevance**
> In modern Java, the Visitor pattern is a powerful tool for operating on complex hierarchies (like Abstract Syntax Trees or DOMs) without polluting the domain objects with unrelated operational logic. With the introduction of **Sealed Classes** (Java 17) and **Pattern Matching for Switch** (Java 21), Java now offers functional alternatives to the classic object-oriented Visitor, but the classic Double-Dispatch pattern remains a staple in enterprise frameworks.

---

## Motivation & Real-World Analogy
Consider a compiler that represents programs as abstract syntax trees (ASTs). It needs to perform various operations on these trees, such as static semantic analysis (type-checking), code optimization, and code generation. Additionally, we might use the AST for pretty-printing, restructuring, or computing metrics.

Most of these operations treat assignment statement nodes differently from variable or arithmetic expression nodes. Consequently, there is a class for assignment statements, another for variables, and so on.

**The Naive Approach:**
Distributing these operations directly across the node classes leads to a system that is difficult to understand, maintain, and change. Adding a new operation (like pretty-printing) requires modifying every single node class.

```text
                    ┌─────────────────────────────────┐
                    │              Node               │
                    ├─────────────────────────────────┤
                    │ + TypeCheck()                   │
                    │ + GenerateCode()                │
                    │ + PrettyPrint()                 │
                    └─────────────────────────────────┘
                                    ^
                                    │
          ─ ─ ─ ────────┬───────────┴───────────┬────────── ─ ─ ─ 
                        │                       │
                ┌───────┴────────┐      ┌───────┴─────────┐
                │ AssignmentNode │      │ VariableRefNode │
                ├────────────────┤      ├─────────────────┤
                │+TypeCheck()    │      │+TypeCheck()     │
                │+GenerateCode() │      │+GenerateCode()  │
                │+PrettyPrint()  │      │+PrettyPrint()   │
                └────────────────┘      └─────────────────┘
```
*Diagram 1: Part of the Node class hierarchy with operations distributed across subclasses. The Problem: It will be confusing to have type checking code mixed with pretty-printing code or flow analysis code. Moreover, adding a new operation usually requires recompiling all of these classes. It would be better if each new operation could be added separately, and the node classes were independent of the operations that apply to them.*

**The Visitor Solution:**
The Visitor pattern packages related operations from each class into a separate object called a **Visitor**. Instead of storing the type-checking logic inside the nodes, we pass a `TypeCheckingVisitor` object to the AST.

When a node "accepts" the visitor, it sends a request to the visitor that includes the node's specific type. The visitor then executes the operation appropriate for that specific node.

```text
                    ┌────────────────────────────────────────┐
                    │              NodeVisitor               │
                    ├────────────────────────────────────────┤
                    │ + VisitAssignment(AssignmentNode)      │
                    │ + VisitVariableRef(VariableRefNode)    │
                    └────────────────────────────────────────┘
                                         ^
                                         │
                 ┌───────────────────────┴──────────────────────┐
                 │                                              │
┌────────────────┴──────────────────┐         ┌─────────────────┴─────────────────┐
│       TypeCheckingVisitor         │         │      CodeGeneratingVisitor        │
├───────────────────────────────────┤         ├───────────────────────────────────┤
│+VisitAssignment(AssignmentNode)   │         │+VisitAssignment(AssignmentNode)   │
│+VisitVariableRef(VariableRefNode) │         │+VisitVariableRef(VariableRefNode) │
└───────────────────────────────────┘         └───────────────────────────────────┘
```
*Diagram 2: NodeVisitor hierarchy gathering related operations into standalone classes (The Solution).*

This relies on **Double-Dispatch**: the operation executed depends on both the type of the Visitor and the type of the Element (Node).

```text
 ┌─────────┐            ┌─────────────────────┐
 │ Program │<>─────────►│        Node         │
 └─────────┘            ├─────────────────────┤
                        │+Accept(NodeVisitor) │   
                        └─────────────────────┘
                                    ^
                                    │
            ┌───────────────────────┴──────────────────────┐
            │                                              │
┌───────────┴─────────────┐                    ┌───────────┴─────────────┐
│     AssignmentNode      │                    │     VariableRefNode     │
├─────────────────────────┤                    ├─────────────────────────┤
│+Accept(NodeVisitor v) o─│───┐                │+Accept(NodeVisitor v) o─│───┐
└─────────────────────────┘   │                └─────────────────────────┘   │
                              │                                              │
    ┌─────────────────────────┘                   ┌──────────────────────────┘
    │ v->VisitAssignment(this)                    │ v->VisitVariableRef(this)
    └─────────────────────────┘                   └──────────────────────────┘ 
```
*Diagram 3: Node hierarchy.*

```text
 aProgram       anAssignmentNode            aNodeVisitor
     │                 │                         │
     │                 │                         │
     │    Accept(v)    │                         │
     │────────────────►│                         │
     │                 │  VisitAssignment(this)  │
     │                 │────────────────────────►│
     │                 │                         │
```
*Diagram 4: Interaction diagram showing Double-Dispatch in action.*

**☕ Java Note: Extending with Lambdas**
> While the classic Visitor requires creating a new class for every operation, modern Java allows for lightweight generic visitors where operations can be passed as `Consumer<T>` or `Function<T, R>` lambdas, drastically reducing boilerplate for simple traversals.

---

## Applicability
Use the Visitor pattern when:
* An object structure contains many classes of objects with differing interfaces, and you want to perform operations on these objects that depend on their concrete classes.
* Many distinct and unrelated operations need to be performed on objects in an object structure, and you want to avoid "polluting" their classes with these operations.
* The classes defining the object structure rarely change, but you often want to define new operations over the structure. If the object hierarchy changes frequently, the cost of updating the Visitor interfaces becomes prohibitive.

---

## Structure & Participants

```text
 ┌────────────┐
 │ <Implicit> │
 │   Client   │
 └───┬─┬──────┘                     ┌────────────────────────────────────────┐
     │ └───────────────────────────►│                Visitor                 │                    
     │                              ├────────────────────────────────────────┤                    
     │                              │+VisitConcreteElementA(ConcreteElementA)│                    
     │                              │+VisitConcreteElementB(ConcreteElementB)│                    
     │                              └────────────────────────────────────────┘                    
     │                                                  ^
     │                                                  │                                          
     │                              ┌───────────────────┴─────────────────────┐                    
     │                              │                                         │
     │         ┌────────────────────┴───────────────────┐ ┌───────────────────┴────────────────────┐
     │         │            ConcreteVisitor1            │ │           ConcreteVisitor2             │
     │         ├────────────────────────────────────────┤ ├────────────────────────────────────────┤
     │         │+VisitConcreteElementA(ConcreteElementA)│ │+VisitConcreteElementA(ConcreteElementA)│
     │         │+VisitConcreteElementB(ConcreteElementB)│ │+VisitConcreteElementB(ConcreteElementB)│
     │         └────────────────────────────────────────┘ └────────────────────────────────────────┘
     │                                                
     │             
     ▼                                                
   ┌─────────────────┐                        ┌─────────────────────┐   
   │ ObjectStructure │───────────────────────►│       Element       │  
   └─────────────────┘                        ├─────────────────────┤
                                              │+Accept(v: Visitor)  │
                                              └─────────────────────┘
                                                         ^
                                                         │
                                           ┌─────────────┴──────────────┐
                                           │                            │
                                ┌──────────┴──────────┐     ┌───────────┴──────────┐
                                │  ConcreteElementA   │     │   ConcreteElementB   │
                                ├─────────────────────┤     ├──────────────────────┤
                                │+Accept(v:Visitor) o │     │+Accept(v:Visitor) o  │
                                │+OperationA()      │ │     │+OperationB()      │  │
                                └───────────────────│─┘     └───────────────────│──┘
               ┌────────────────────────────────────┘                           │
               │return v.VisitConcreteElementA(this);                           │
               └─────────────────────────────────────┘                          │
                                           ┌────────────────────────────────────┘
                                           │return v.VisitConcreteElementB(this);
                                           └─────────────────────────────────────┘
```
*Diagram 5: Common Structure of the Visitor Design Pattern.*

* **Visitor:** Declares a Visit operation for each class of ConcreteElement in the object structure. The operation's signature identifies the class that sent the Visit request.
* **ConcreteVisitor:** Implements each operation declared by Visitor. Each operation implements a fragment of the algorithm designed for the corresponding class of object in the structure. Provides context and stores local state.
* **Element:** Declares an `Accept` operation that takes a visitor as an argument.
* **ConcreteElement:** Implements an `Accept` operation that takes a visitor as an argument.
* **ObjectStructure (Program):** Can enumerate its elements, may provide a high-level interface to allow the visitor to visit its elements, and may either be a Composite or a collection (like a List or Set).

---

## Collaborations

```text
anObjectStructure    aConcreteElementA    aConcreteElementB                aConcreteVisitor
        │                    │                    │                                │
        │Accept(aVisitor)    │                    │                                │
        │───────────────────►│                    │                                │
        │                    │VisitConcreteElementA(this)                          │
        │                    │────────────────────────────────────────────────────►│
        │                    │                    │                    OperationA()│
        │                    │◄────────────────────────────────────────────────────│
        │                    │                    │                                │
        │                    │                    │                                │
        │Accept(aVisitor)    │                    │                                │
        │────────────────────┼───────────────────►│                                │
        │                    │                    │VisitConcreteElementB(this)     │
        │                    │                    │───────────────────────────────►│
        │                    │                    │                    OperationB()│
        │                    │                    │◄───────────────────────────────│
        │                    │                    │                                │
```
*Diagram 6: Sequence of collaborations showing the ObjectStructure traversing elements and passing the visitor.*

* A client that uses the Visitor pattern must create a `ConcreteVisitor` object and then traverse the object structure, visiting each element with the visitor.
* When an element is visited, it calls the Visitor operation that corresponds to its class. The element supplies itself as an argument to this operation.

---

## Consequences
1.  **Makes adding new operations easy:** Adding a new operation over an object structure is as simple as adding a new `ConcreteVisitor` class.
2.  **Gathers related operations and separates unrelated ones:** Related behavior is localized in a single visitor class, while the data structures themselves remain purely data-focused.
3.  **Adding new ConcreteElement classes is hard:** Every new Element requires a new abstract method in the `Visitor` interface and implementations in every `ConcreteVisitor`.
4.  **Visiting across class hierarchies:** Unlike an Iterator, a Visitor can visit objects that do not share a common parent class (other than `Element`).
5.  **Accumulating state:** Visitors can accumulate state as they visit elements, avoiding the need to pass state as global variables or extra arguments to traversal methods.
6.  **Breaking encapsulation:** The Visitor approach assumes that the `ConcreteElement` interface is powerful enough to let visitors do their job. You may have to expose internal state via public getters, compromising encapsulation.

**☕ Java Note: Modern Java Pattern Matching vs. Classic Visitor**
> If your Element hierarchy relies on Java 17+ `sealed` interfaces/classes, you can avoid the boilerplate of the Visitor pattern entirely using Pattern Matching for Switch (Java 21+):
> ```java
> public String processNode(Node node) {
>     return switch (node) {
>         case AssignmentNode a -> "Assigning " + a.getVariable() + " = " + a.getValue();
>         case VariableRefNode v -> "Reference to " + v.getName();
>         // Exhaustive due to sealed class, no default needed!
>     };
> }
> ```
> This modern approach keeps logic separate from the data structures (just like Visitor) but eliminates the `Accept` boilerplate and the double-dispatch mechanism.

---

## Implementation Hints
* **Double Dispatch:** The pattern executes an operation depending on *two* types: the visitor's type and the element's type. Standard Java methods use single dispatch (polymorphism based solely on the receiver object). Visitor simulates double dispatch via the `element.accept(visitor)` -> `visitor.visit(this)` handshake.
* **Who is responsible for traversal?** Often, an Iterator or the `ObjectStructure` is responsible for traversing the collection and calling `accept` on each element. However, the `Visitor` itself can control the traversal if the traversal order depends on the results of the operations.

---

## Known Uses & Java API Usage
* **`java.nio.file.FileVisitor`:** Used with `Files.walkFileTree()`. The `SimpleFileVisitor` allows developers to define logic (the Visitor) that operates on files and directories (the Elements) without modifying the Java File API.
* **`javax.lang.model.element.ElementVisitor`:** Used heavily in Java Annotation Processing to visit variables, classes, methods, and packages in the abstract syntax tree of Java code during compilation.
* **ASM Bytecode Library:** Uses `ClassVisitor`, `MethodVisitor`, and `FieldVisitor` to traverse, generate, and transform compiled Java `.class` bytecodes.
* **Spring Framework `BeanDefinitionVisitor`:** Used to traverse and modify bean definition metadata in the application context (e.g., resolving property placeholders).

---

## Related Patterns
* **Composite:** Visitors can be used to apply an operation over an object structure defined by the Composite pattern.
* **Iterator:** Iterator and Visitor can be used together to traverse data structures. Iterators abstract the traversal, while Visitors abstract the operation performed on the elements.
* **Interpreter:** Visitor may be applied to do the interpretation.