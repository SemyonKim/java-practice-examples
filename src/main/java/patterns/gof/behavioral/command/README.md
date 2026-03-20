# Command - Object Behavioral

***[Back to the Pattern Catalog](../../README.md)***

**[Code Implementation](CommandDemonstration.java)**

## Intent & Core Problem
Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations. 

> **Also Known As:** Action, Transaction 

**☕ Java Note:**
> In modern Java, the Command pattern is the foundation for task execution (e.g., `Runnable` and `Callable` submitted to an `ExecutorService`). It allows developers to treat method invocations as objects, enabling asynchronous execution, robust undo mechanisms, and distributed job queues.

---

## Motivation & Real-World Analogy
Sometimes it's necessary to issue requests to objects without knowing anything about the operation being requested or the receiver of the request.  For example, user interface toolkits include objects like buttons and menus that carry out a request in response to user input.  But the toolkit can't implement the request explicitly in the button or menu, because only applications that use the toolkit know what should be done on which object.  As toolkit designers we have no way of knowing the receiver of the request or the operations that will carry it out. 

The Command pattern lets toolkit objects make requests of unspecified application objects by turning the request itself into an object.  This object can be stored and passed around like other objects.  The key to this pattern is an abstract `Command` class, which declares an interface for executing operations.  In the simplest form this interface includes an abstract `Execute` operation.  Concrete Command subclasses specify a receiver-action pair by storing the receiver as an instance variable and by implementing `Execute` to invoke the request.  The receiver has the knowledge required to carry out the request. 

Menus can be implemented easily with Command objects.  Each choice in a Menu is an instance of a `MenuItem` class.  An `Application` class creates these menus and their menu items along with the rest of the user interface.  The `Application` class also keeps track of `Document` objects that a user has opened.  The application configures each `MenuItem` with an instance of a concrete `Command` subclass.  When the user selects a `MenuItem`, the `MenuItem` calls `Execute` on its command, and `Execute` carries out the operation.  `MenuItems` don't know which subclass of `Command` they use.  Command subclasses store the receiver of the request and invoke one or more operations on the receiver. 

```text
┌───────────────┐         ┌───────────────┐         ┌───────────────┐ command  ┌───────────────┐
│  Application  │<>──────►│     Menu      │<>──────►│   MenuItem    │<>───────►│    Command    │
├───────────────┤         ├───────────────┤         ├───────────────┤          ├───────────────┤
│ Add(Document) │<>┐      │ Add(MenuItem) │         │ Clicked()  o──┼──┐       │ Execute()     │
└───────────────┘  │      └───────────────┘         └───────────────┘  │       └───────────────┘
                   │                                                   │               ^
                   │                          ┌────────────────────────┘               │
                   V                          │ command->Execute()                     │
           ┌───────────────┐                  └────────────────────────┘             - - - 
           │   Document    │                                                    
           ├───────────────┤                                                    
           │ Open()        │                                                    
           │ Close()       │                                                    
           │ Cut()         │                                                    
           │ Copy()        │                                                    
           │ Paste()       │                                                    
           └───────────────┘
```
*Diagram Description: An object interaction mapping illustrating how an Application builds UI elements. The `MenuItem` delegates its `Clicked()` behavior to a `Command` object's `Execute()` method.* 

For example, `PasteCommand` supports pasting text from the clipboard into a `Document`.  `PasteCommand`'s receiver is the `Document` object it is supplied upon instantiation.  The `Execute` operation invokes `Paste` on the receiving `Document`. 

```text
                                            ┌───────────────┐
                                            │    Command    │
┌───────────────┐                           ├───────────────┤
│   Document    │                           │ Execute()     │
├───────────────┤                           └───────────────┘
│ Open()        │                                   ^
│ Close()       │                                   │
│ Cut()         │                         - - - ────+──── - - -
│ Copy()        │                                   │
│ Paste()       │◄─────────┐                ┌───────┴───────┐
└───────────────┘          │       document │ PasteCommand  │
                           └────────────────┤───────────────┤
                                            │ Execute()  o──┼───┐
                                            └───────────────┘   │
                                                                │ document->Paste()
                                                                └───────────────────┘
```
*Diagram Description: `PasteCommand` inherits from `Command`. It holds a reference to a `Document` receiver and invokes `Paste()` on it during execution.* 

`OpenCommand`'s `Execute` operation is different: it prompts the user for a document name, creates a corresponding `Document` object, adds the document to the receiving application, and opens the document. 

```text
┌───────────────┐                                           ┌───────────────┐
│  Application  │                                           │    Command    │
├───────────────┤                                           ├───────────────┤
│ Add(Document) │◄─────────┐                                │ Execute()     │
└───────────────┘          │                                └───────────────┘
                           │                                        ^
                           │                        ┌───────────────┴ - - -
                           │                        │
                           └────────────────┌───────┴───────┐
                                application │  OpenCommand  │
                                            ├───────────────┤
                                            │ Execute()  o──┼───┐
                                            │ AskUser()     │   │
                                            └───────────────┘   │
                                    ┌───────────────────────────┘
                                    │ name = AskUser()
                                    │ doc = new Document(name)
                                    │ application->Add(doc)
                                    │ doc->Open()
                                    └───────────────────────────┘
```
*Diagram Description: `OpenCommand` acts as a more complex command that coordinates with the `Application` to create and open a new `Document`.* 

Sometimes a `MenuItem` needs to execute a *sequence* of commands.  Because it's common to string commands together in this way, we can define a `MacroCommand` class to allow a `MenuItem` to execute an open-ended number of commands.  `MacroCommand` has no explicit receiver, because the commands it sequences define their own receiver. 

```text
┌───────────────┐
│    Command    │◄───────────────────────────┐
├───────────────┤                            │
│ Execute()     │                            │
└───────────────┘                            │
        ^                                    │
- - - - ┴───────────────┐                    │     
                        │                    │     
                 ┌──────┴────────┐ commands  │
                 │ MacroCommand  │<>─────────┘
                 ├───────────────┤
                 │ Execute()  o──┼───┐
                 └───────────────┘   │
         ┌───────────────────────────┘
         │ for all c in commands
         │ c->Execute()
         └───────────────────────────┘
```
*Diagram Description: `MacroCommand` is a Composite that contains a collection of other `Command` objects, executing them in sequence.* 

In each of these examples, notice how the Command pattern decouples the object that invokes the operation from the one having the knowledge to perform it.  All of this is possible because the object that issues a request only needs to know how to issue it; it doesn't need to know how the request will be carried out. 

---

## Applicability
Use the Command pattern when you want to:
* **Parameterize objects by an action to perform**, as `MenuItem` objects did above.  Commands are an object-oriented replacement for callbacks. 
* **Specify, queue, and execute requests at different times.**  A Command object can have a lifetime independent of the original request. 
* **Support undo.**  The Command's `Execute` operation can store state for reversing its effects in the command itself.  Executed commands are stored in a history list for unlimited-level undo and redo. 
* **Support logging changes** so that they can be reapplied in case of a system crash.  Recovering from a crash involves reloading logged commands from disk and reexecuting them. 
* **Structure a system around high-level operations built on primitive operations.**  The Command pattern offers a way to model transactions with a common interface. 

---

## Structure & Participants

### Structure

```text
┌───────────────┐         ┌───────────────┐              ┌───────────────┐
│    Client     │         │    Invoker    │<>───────────►│    Command    │
└─┬─────┬───────┘         └───────────────┘              ├───────────────┤
  │     │                                                │ Execute()     │
  │     │                                                └───────────────┘
  │     │                 ┌───────────────┐                      ^
  │     └────────────────►│   Receiver    │                      │
  │                       ├───────────────┤     receiver ┌───────┴───────┐
  │                       │ Action()      │◄─────────────┤ConcreteCommand│
  │                       └───────────────┘              ├───────────────┤
  │                                                      │ Execute()  o──┼──┐
  └----------------------------------------------------->│ state         │  │ receiver->Action();
                                                         └───────────────┘  └─────────────────────┘
```
*Diagram Description: The fundamental structure of the Command pattern showing Client, Invoker, Receiver, Command, and ConcreteCommand relationships.* 

### Participants
* **Command:** Declares an interface for executing an operation. 
* **ConcreteCommand** (`PasteCommand`, `OpenCommand`): Defines a binding between a Receiver object and an action.  Implements `Execute` by invoking the corresponding operation(s) on Receiver. 
* **Client** (`Application`): Creates a `ConcreteCommand` object and sets its receiver. 
* **Invoker** (`MenuItem`): Asks the command to carry out the request. 
* **Receiver** (`Document`, `Application`): Knows how to perform the operations associated with carrying out a request.  Any class may serve as a Receiver. 

---

## Collaborations

```text
aReceiver       aClient                                   aCommand       anInvoker
   │               │                                         │               │
   │               │ new Command(aReceiver)                  │               │
   │               ├────────────────────────────────────────►│               │
   │               │                                         │               │
   │               │ StoreCommand(aCommand)                  │               │
   │               ├─────────────────────────────────────────┼──────────────►│
   │               │                                         │               │
   │               │                                         │ Execute()     │
   │               │                                         │◄──────────────┤
   │ Action()      │                                         │               │
   │◄──────────────┼─────────────────────────────────────────┤               │
```
*Diagram Description: Sequence of events starting with a Client creating a command and passing it to an Invoker, which later triggers the Receiver via the Command.* 

* The client creates a `ConcreteCommand` object and specifies its receiver. 
* An Invoker object stores the `ConcreteCommand` object. 
* The invoker issues a request by calling `Execute` on the command.  When commands are undoable, `ConcreteCommand` stores state for undoing the command prior to invoking `Execute`. 
* The `ConcreteCommand` object invokes operations on its receiver to carry out the request. 

---

## Consequences (Trade-offs)
The Command pattern has the following consequences:
1. Command decouples the object that invokes the operation from the one that knows how to perform it. 
2. Commands are first-class objects.  They can be manipulated and extended like any other object. 
3. You can assemble commands into a composite command (like `MacroCommand`). 
4. It's easy to add new Commands, because you don't have to change existing classes. 

---

## Implementation Hints & Modern Java Context
1. **How intelligent should a command be?**  At one extreme it merely defines a binding between a receiver and the actions.  At the other extreme it implements everything itself without delegating to a receiver at all. 
2. **Supporting undo and redo.**  Commands can support undo and redo capabilities if they provide a way to reverse their execution (e.g., an Unexecute or Undo operation).  The command may need to copy itself acting as a Prototype if its state varies across invocations. 
3. **Avoiding error accumulation.**  The Memento pattern can be applied to give the command access to historical state information without exposing the internals of other objects. 

**☕ Java Context & Best Practices:**
> The original text mentions using C++ templates for simple commands. In Java, you typically implement simple, non-undoable commands using lambda expressions mapped to functional interfaces like `Runnable` or `Consumer<T>`. However, if the command requires an `undo()` method or maintains internal execution state, a standard class implementing a multi-method interface is required, as Java lambdas cannot satisfy interfaces with more than one abstract method.

```java
// Java: Simple command via lambda (only for single-action commands)
Runnable pasteCmd = document::paste; 
invoker.store(pasteCmd);

// Java: Undoable command requires a class/anonymous block
UndoableCommand complexCmd = new UndoableCommand() {
    @Override public void execute() { document.paste(); }
    @Override public void undo() { document.undoPaste(); }
};
```

---

## Known Uses & Java API Usage
* **Java Core Libraries:** `java.lang.Runnable` and `java.util.concurrent.Callable` are classic Command interfaces used extensively in thread pooling and task scheduling.
* **Java UI (Swing/AWT):** `javax.swing.Action` provides a centralized interface for application commands that can be attached to menus, buttons, and keystrokes.
* **Spring Framework:** The `JdbcTemplate` relies heavily on callback objects (e.g., `StatementCallback`, `ResultSetExtractor`) which encapsulate specific query logic as commands passed to the template.

---

## Related Patterns
* **Composite:** Can be used to implement MacroCommands. 
* **Memento:** Can keep state the command requires to undo its effect. 
* **Prototype:** A command that must be copied before being placed on the history list acts as a Prototype. 