# Effective Java: Study & Implementation Notes
This repository contains my personal study notes, code examples, and implementations based on the 90 items outlined in "**Effective Java**" (3rd Edition) by **Joshua Bloch**.

The goal of this project is to deepen my understanding of best practices in the Java programming language, focusing on clarity, performance, and maintainability.

Each item is documented directly within the source code using Javadoc and self-documenting code practices.

---

## 🛠 Project Structure
The repository is organized by chapters. You can navigate to the source files to read the implementations and their corresponding Javadoc:

- **Chapter 2**: Creating and Destroying Objects
  - [Item 1: Consider static factory methods instead of constructors](ch2/item1/StaticFactories.java) 
  - [Item 2: Consider a builder when faced with many constructor parameters](ch2/item2/NutritionFacts.java)
  - [Item 3: Enforce the singleton property with a private constructor or an enum type](ch2/item3/Elvis.java)
  - [Item 4: Enforce noninstantiability with a private constructor](ch2/item4/UtilityClass.java)
  - [Item 5: Prefer dependency injection to hardwiring resources](ch2/item5/SpellChecker.java)
  - [Item 6: Avoid creating unnecessary objects](ch2/item6/ObjectReuse.java)
  - [Item 7: Eliminate obsolete object references](ch2/item7/Stack.java)
  - [Item 8: Avoid finalizers and cleaners](ch2/item8/Room.java)
  - [Item 9: Prefer try-with-resources to try-finally](ch2/item9/TopLineRetriever.java)
- **Chapter 3**: Methods Common to All Objects
  - [Item 10: Obey the general contract when overriding equals](ch3/item10/PhoneNumber.java)
  - [Item 11: Always override hashCode when you override equals](ch3/item11/PhoneNumber.java)
  - [Item 12: Always override toString](ch3/item12/PhoneNumber.java)
  - [Item 13: Override clone judiciously](ch3/item13/Stack.java)
  - [Item 14: Consider implementing Comparable](ch3/item14/PhoneNumber.java)
- **Chapter 4**: Classes and Interfaces
  - [Item 15: Minimize the accessibility of classes and members](ch4/item15/AccessControlExample.java)
  - [Item 16: In public classes, use accessor methods, not public fields](ch4/item16/Point.java)
  - [Item 17: Minimize mutability](ch4/item17/Complex.java)
  - [Item 18: Favor composition over inheritance](ch4/item18/InstrumentedSet.java)
  - [Item 19: Design and document for inheritance or else prohibit it](ch4/item19/Super.java)
  - [Item 20: Prefer interfaces to abstract classes](ch4/item20/PreferInterfaces.java)
  - [Item 21: Design interfaces for posterity](ch4/item21/PosterityInterfaceDemo.java)
  - [Item 22: Use interfaces only to define types](ch4/item22/PhysicalConstants.java)
  - [Item 23: Prefer class hierarchies to tagged classes](ch4/item23/Figure.java)
  - [Item 24: Favor static member classes over nonstatic](ch4/item24/NestedClassLibrary.java)
  - [Item 25: Limit source files to a single top-level class](ch4/item25/TopLevelClassDemo.java)
- **Chapter 5**: Generics
  - [Item 26: Don’t use raw types](ch5/item26/RawTypeDemo.java)
  - [Item 27: Eliminate unchecked warnings](ch5/item27/WarningManager.java)
  - [Item 28: Prefer lists to arrays](ch5/item28/Chooser.java)
  - [Item 29: Favor generic types](ch5/item29/Stack.java)
  - [Item 30: Favor generic methods](ch5/item30/GenericMethodUtils.java)
  - [Item 31: Use bounded wildcards to increase API flexibility](ch5/item31/WildcardStack.java)
  - [Item 32: Combine generics and varargs judiciously](ch5/item32/SafeVarargsDemo.java)
  - [Item 33: Consider typesafe heterogeneous containers](ch5/item33/Favorites.java)
- **Chapter 6**: Enums and Annotations
  - [Item 34: Use enums instead of int constants](ch6/item34/EnumExamples.java)
  - [Item 35: Use instance fields instead of ordinals](ch6/item35/Ensemble.java)
  - [Item 36: Use EnumSet instead of bit fields](ch6/item36/Text.java)
  - [Item 37: Use EnumMap instead of ordinal indexing](ch6/item37/Garden.java)
  - [Item 38: Emulate extensible enums with interfaces](ch6/item38/Operation.java)
  - [Item 39: Prefer annotations to naming patterns](ch6/item39/AnnotationTestRunner.java)
  - [Item 40: Consistently use the Override annotation](ch6/item40/Bigram.java)
  - [Item 41: Use marker interfaces to define types](ch6/item41/MarkerInterfaceDemo.java)
- **Chapter 7**: Lambdas and Streams
  - [Item 42: Prefer lambdas to anonymous classes](ch7/item42/LambdaComparison.java)
  - [Item 43: Prefer method references to lambdas](ch7/item43/MethodReferenceDemo.java)
  - [Item 44: Favor the use of standard functional interfaces](ch7/item44/CacheApi.java)
  - [Item 45: Use streams judiciously](ch7/item45/StreamUsage.java)
  - [Item 46: Prefer side-effect-free functions in streams](ch7/item46/StreamParadigm.java)
  - [Item 47: Prefer Collection to Stream as a return type](ch7/item47/SequenceReturner.java)
  - [Item 48: Use caution when making streams parallel](ch7/item48/ParallelStreamCaution.java)
- **Chapter 8**: Methods
  - [Item 49: Check parameters for validity](ch8/item49/ParameterValidator.java)
  - [Item 50: Make defensive copies when needed](ch8/item50/Period.java)
  - [Item 51: Design method signatures carefully](ch8/item51/SignatureDesign.java)
  - [Item 52: Use overloading judiciously](ch8/item52/CollectionClassifier.java)
  - [Item 53: Use varargs judiciously](ch8/item53/VarargsUsage.java)
  - [Item 54: Return empty collections or arrays, not nulls](ch8/item54/CheeseShop.java)
  - [Item 55: Return optionals judiciously](ch8/item55/MaxFinder.java)
  - [Item 56: Write doc comments for all exposed API elements](ch8/item56/DocumentationExample.java)
- **Chapter 9**: General Programming
  - [Item 57: Minimize the scope of local variables](ch9/item57/ScopeMinimizer.java)
  - [Item 58: Prefer for-each loops to traditional for loops](ch9/item58/EnhancedForLoop.java)
  - [Item 59: Know and use the libraries](ch9/item59/LibraryUsage.java)
  - [Item 60: Avoid float and double if exact answers are required](ch9/item60/ExactArithmetic.java)
  - [Item 61: Prefer primitive types to boxed primitives](ch9/item61/PrimitivePreference.java)
  - [Item 62: Avoid strings where other types are more appropriate](ch9/item62/StringMisuse.java)
  - [Item 63: Beware the performance of string concatenation](ch9/item63/StringConcatenation.java)
  - [Item 64: Refer to objects by their interfaces](ch9/item64/InterfaceReference.java)
  - [Item 65: Prefer interfaces to reflection](ch9/item65/ReflectionToInterface.java)
  - [Item 66: Use native methods judiciously](ch9/item66/NativeMethods.java)
  - [Item 67: Optimize judiciously](ch9/item67/OptimizationStrategy.java)
  - [Item 68: Adhere to generally accepted naming conventions](ch9/item68/NamingConventions.java)
- **Chapter 10**: Exceptions
  - [Item 69: Use exceptions only for exceptional conditions](ch10/item69/ExceptionControlFlow.java)
  - [Item 70: Use checked exceptions for recoverable conditions and runtime exceptions for programming errors](ch10/item70/ExceptionHierarchy.java)
  - [Item 71: Avoid unnecessary use of checked exceptions](ch10/item71/ExceptionRefactoring.java)
  - [Item 72: Favor the use of standard exceptions](ch10/item72/StandardExceptionUsage.java)
  - [Item 73: Throw exceptions appropriate to the abstraction](ch10/item73/AbstractionAppropriateExceptions.java)
  - [Item 74: Document all exceptions thrown by each method](ch10/item74/ExceptionDocumentation.java)
  - [Item 75: Include failure-capture information in detail messages](ch10/item75/FailureCaptureUsage.java)
  - [Item 76: Strive for failure atomicity](ch10/item76/FailureAtomicCollection.java)
  - [Item 77: Don’t ignore exceptions](ch10/item77/ExceptionHandlingPractices.java)
- **Chapter 11**: Concurrency
  - [Item 78: Synchronize access to shared mutable data](ch11/item78/SharedMutableData.java)
  - [Item 79: Avoid excessive synchronization](ch11/item79/ObservableSet.java)
  - [Item 80: Prefer executors, tasks, and streams to threads](ch11/item80/TaskExecutionManager.java)
  - [Item 81: Prefer concurrency utilities to wait and notify](ch11/item81/ConcurrencyUtilities.java)
  - [Item 82: Document thread safety](ch11/item82/ThreadSafetyDoc.java)
  - [Item 83: Use lazy initialization judiciously](ch11/item83/LazyInitialization.java)
  - [Item 84: Don’t depend on the thread scheduler](ch11/item84/ThreadScheduling.java)
- **Chapter 12**: Serialization
  - [Item 85: Prefer alternatives to Java serialization](ch12/item85/SerializationAlternatives.java)
  - [Item 86: Implement Serializable with great caution](ch12/item86/SerializableCaution.java)
  - [Item 87: Consider using a custom serialized form](ch12/item87/StringList.java)
  - [Item 88: Write readObject methods defensively](ch12/item88/Period.java)
  - [Item 89: For instance control, prefer enum types to readResolve](ch12/item89/EnumVsReadResolve.java)
  - [Item 90: Consider serialization proxies instead of serialized instances](ch12/item90/Period.java)
>**Note**: To view the formatted documentation, you can run javadoc on the source files or use the "Rendered View" in your IDE (IntelliJ/Eclipse/VS Code).

---

## ⚖️ Attribution and Legal
This project is for **educational and study purposes only**.

**Original Work**  
The content, concepts, and item titles used in this repository are based on the book:

- **Book**: Effective Java (3rd Edition)
- **Author**: Joshua Bloch
- **Publisher**: Addison-Wesley Professional

**Disclaimer**  
The code and Javadoc comments in this repository represent my personal interpretation of the book's contents. This is not a replacement for the book. To fully grasp the "Why" behind these items, I highly recommend reading Joshua Bloch's original text.