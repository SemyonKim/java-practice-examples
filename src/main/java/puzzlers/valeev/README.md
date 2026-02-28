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
  - [Assuming that char value is a character](strings/mistake1/CharValueMistake.java)
  - [java.util.Locale: Unexpected Case Conversions](strings/mistake2/UnexpectedCaseConversionMistake.java)
  - [Using String.format with the Default Locale](strings/mistake3/StringFormatLocaleMistake.java)
  - [Mismatched Format Arguments: %s and %b](strings/mistake4/FormatArgumentMistake.java)
- Comparing objects
  - [Pre-Java 18: Use of Reference Equality instead of the equals Method](comparing/mistake1/ReferenceEqualityMistake.java)
  - [Assuming equals() Compares Content](comparing/mistake2/ContentEqualityMistake.java)
  - [Using URL.equals() and hashCode()](comparing/mistake3/URLEqualityMistake.java)
  - [Comparing BigDecimals with Different Scales](comparing/mistake4/BigDecimalScaleMistake.java)
  - [Wrong hashCode() with Array Fields](comparing/mistake5/ArrayHashCodeMistake.java)
  - [Mismatch between equals() and hashCode()](comparing/mistake6/HashCodeMismatchMistake.java)
  - [Using Subtraction when Comparing Numbers](comparing/mistake7/ComparisonSubtractionMistake.java)
  - [Ignoring Possible NaN Values in Comparison Methods](comparing/mistake8/NaNComparisonMistake.java)
  - [Failing to Represent an Object as a Sequence of Keys](comparing/mistake9/ComparisonSequenceMistake.java)
  - [Returning Random Numbers from a Comparator](comparing/mistake10/RandomComparatorMistake.java)
- Collections and maps
  - [Using Null Values in Maps](collections/mistake1/NullInMapsMistake.java)
  - [Trying to Modify an Unmodifiable Collection](collections/mistake2/UnmodifiableCollectionMistake.java)
  - [Using Mutable Objects as Keys](collections/mistake3/MutableKeyMistake.java)
  - [Concurrent Modification During Iteration](collections/mistake4/ConcurrentModificationMistake.java)
  - [Mixing Collection.remove(Object) and List.remove(int) for List of Integer](collections/mistake5/ListRemoveMistake.java)
  - [Reading the Collection inside Collection.removeIf()](collections/mistake6/RemoveIfQueryMistake.java)
  - [Concurrent Modification in Map.computeIfAbsent()](collections/mistake7/ComputeIfAbsentMistake.java)
  - [Violating Iterator Contracts](collections/mistake8/IteratorContractMistake.java)
- Library methods
  - [Using Null Values in a Stream](library/mistake1/StreamNullMistake.java)
  - [Violating the Stream API Contract: Stream.reduce(identity, accumulator)](library/mistake2/StreamContractMistake.java)
  - [Using getClass() instead of instanceOf](library/mistake3/GetClassVsInstanceof.java)
  - [Using getClass() on enums, annotations or Class<?>](library/mistake4/Mistake88.java)
  - [Accidental invalidation of weak or soft references](library/mistake5/WeakReferenceInvalidation.java)
  - [Time-of-Check to Time-of-Use (TOCTOU) vulnerabilities](library/mistake6/WorldStabilityMistake.java)
- Unit testing
  - [Side effect in assert statement](testing/mistake1/AssertSideEffectMistake.java)
  - [Using assertNotEquals() to check the equality contract](testing/mistake2/AssertNotEqualsMistake.java)