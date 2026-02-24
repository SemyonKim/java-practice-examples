# Java Puzzlers

This folder contains selected examples from *Java Puzzlers: Traps, Pitfalls, and Corner Cases*  
by Joshua Bloch and Neal Gafter (2005).

## Structure
- Each puzzler is placed in its own subfolder.
- Code files are self‑documenting: explanations and lessons are embedded directly in the source.

## Index
- Expressive Puzzlers
  - [Odd number check pitfalls](expressive/puzzle1/Oddity.java)
  - [Money with double precision](expressive/puzzle2/TimeForChange.java)
  - [Microseconds to milliseconds overflow](expressive/puzzle3/LongDivision.java)
  - [Hex literal type confusion](expressive/puzzle4/JoyOfHex.java)
  - [Sign vs Zero Extension](expressive/puzzle5/Multicast.java)
  - [Conditional Promotion Trap](expressive/puzzle6/DosEquis.java)
  - [Compound Assignment Trap](expressive/puzzle7/Tweedledum.java)
  - [Compound Assignment with References Trap](expressive/puzzle8/Tweedledee.java)
- Puzzlers with Character
  - [Unicode Preprocessing Trap](character/puzzle1/EscapeRout.java)
  - [Line Feed Injection Trap](character/puzzle2/LineFeedInjection.java)
  - [Unicode Literal Trap](character/puzzle3/Ugly.java)
  - [Charset Mapping Trap](character/puzzle4/StringCheese.java)
  - [Regex Replacement Trap](character/puzzle5/RegexTrap.java)
- Loopy Puzzlers
  - [Bitwise Masking Requirement](loopy/puzzle1/BitwiseMasking.java)
  - [Infinite Shift Loop Trap](loopy/puzzle2/ShiftLoop.java)
  - [Floating-Point Precision Gap](loopy/puzzle3/Looper.java)
  - [Non-Self-Equal Trap](loopy/puzzle4/NaNLooper.java)
  - [Shifting Short Infinite Loop](loopy/puzzle5/CompoundTrap.java)
  - [Floating-Point Step-or-Stall Trap](loopy/puzzle6/FloatPrecisionTrap.java)
- Exceptional Puzzlers
  - [Arcane Exception Trio](exceptional/puzzle1/Arcane3.java)
  - [Definite Unassignment Paradox](exceptional/puzzle2/UnwelcomeGuest.java)
  - [Scoping Linkage Paradox](exceptional/puzzle3/Strange2.java)
  - [Exhausting Recursive Trap](exceptional/puzzle4/Workout.java)
- Classy Puzzlers
  - [Case of the Confusing Constructor](classy/puzzle1/Confusing.java)
  - [Static Initialization Paradox](classy/puzzle2/Elvis.java)
  - [Instanceof-Cast Contrast](classy/puzzle3/TypeTests.java)
  - [Overridable Method in Constructor Trap](classy/puzzle4/ColorPoint.java)
  - [Re-Initialization Reset](classy/puzzle5/Client.java)
  - [Private Constructor Capture](classy/puzzle6/MyThing.java)
  - [Static Methods on Null References](classy/puzzle7/Null.java)
  - [Local Variable Declaration Statement Trap](classy/puzzle8/Creator.java)
- Library Puzzlers
  - [Absolute Minimum Trap](library/puzzle1/Modesty.java)
  - [Integer Overflow Comparator Trap](library/puzzle2/SuspiciousSort.java)
- Classier Puzzlers
  - [Field Hiding Illusion](classier/puzzle1/PublicMatter.java)
  - [Closest Class Definition Trap](classier/puzzle2/StrungOut.java)
  - [Obscuring Redux: Variable Precedence Over Type](classier/puzzle3/ShadesOfGray.java)
  - [Package-Private Pitfall](classier/puzzle4/hack/TypeIt.java)
  - [Inherited Members Shadow Static Import](classier/puzzle5/ImportDuty.java)
  - [Inconsistency in The Final Modifier for Methods and Fields](classier/puzzle6/DoubleJeopardy.java)
- More Library Puzzlers
  - [Reflection Infection: Inaccessible Qualifying Type Trap](libraryadvanced/puzzle1/Reflector.java)
  - [Innermost Scope Name Resolution Trap](libraryadvanced/puzzle2/Pet.java)
  - [Reflection and Inner Class Implicit Constructor](libraryadvanced/puzzle3/Outer.java)
  - [Beer Blast: The Hung Process Trap](libraryadvanced/puzzle4/BeerBlast.java)
- Advanced Puzzlers
  - [Literal Exception: Special Case for MIN int and long](advanced/puzzle1/LiteralEdge.java)
  - [Computational equality (==) violates Reflexivity and Transitivity, but honors Symmetry](advanced/puzzle2/EqualityParadox.java)
  - [Raw Type Infection: The Erasure Trap](advanced/puzzle3/RawInfection.java)
  - [Generic Shadowing: The Identity Crisis](advanced/puzzle4/LinkedList.java)
  - [Serial Killer: The Deserialization Hazard](advanced/puzzle5/SerialKiller.java)
  - [Twisted Pair: The Scope vs. Inheritance Trap](advanced/puzzle6/Twisted.java)
  - [Brittle Constant Syndrome: Binary Incompatibility](advanced/puzzle7/client/PrintWords.java)