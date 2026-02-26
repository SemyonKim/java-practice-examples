# Java Mistakes

This folder contains selected examples from *100 Java Mistakes and How To Avoid Them* by Tagir Valeev.

## Structure
- Each mistake is placed in its own subfolder.
- Code files are self‑documenting: explanations and lessons are embedded directly in the source.

## Index
- Expressions  
  - [Ternary Type Conversion Pitfalls](expressions/mistake1/ConditionalExpressionMistake.java)
  - [Incorrectly Using Variable Arity Calls (Varargs)](expressions/mistake2/VarargsMistakes.java)
  - [Conditional Operators and Variable Arity Calls](expressions/mistake3/VarargsTernaryMistake.java)
  - [Binding a Method Reference to the Wrong Method](expressions/mistake4/MethodReferenceMistake.java)
- Program structure
  - [Loop Overflow (Infinite Loops at Boundary Values)](structure/mistake1/LoopOverflowMistake.java)
  - [Incorrect Initialization Order](structure/mistake2/InitializationOrderMistake.java)
- Numbers
  - [Signed Zero: +0.0 and -0.0](numbers/mistake1/SignedZeroMistake.java)
  - [Not a Number (NaN) Values](numbers/mistake2/NaNMistake.java)
  - [Double.MIN_VALUE is not the Minimal Value](numbers/mistake3/MinValueMistake.java)
- Common exceptions
  - [NullPointerException: Avoiding Nulls and Defensive Checks](exceptions/mistake1/NullDefensiveMistake.java)
  - [NullPointerException: Using Optional instead of Null](exceptions/mistake2/OptionalUsageMistake.java)
  - [ClassCastException: Generic types and implicit casts](exceptions/mistake3/GenericCastMistake.java)
  - [ClassCastException: Different class loaders](exceptions/mistake4/ClassLoaderMistake.java)
  - [StackOverflowError: Deep but finite recursion](exceptions/mistake5/StackOverflowMistake.java)
  - [StackOverflowError: Infinite recursion](exceptions/mistake6/InfiniteRecursionMistake.java)
- Strings
  - []()
  - []()
- *(to be filled as examples are added)*
