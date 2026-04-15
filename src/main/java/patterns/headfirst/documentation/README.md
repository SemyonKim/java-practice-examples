# Design Patterns: Documentation

> **RULE: KEEP IT SIMPLE (KISS)**
> 
> Solve things in the simplest way possible; do not use a pattern if a simpler solution works.

> **RULE: BEWARE HYPOTHETICALS**
> 
> Employ patterns to handle practical, expected changes, not hypothetical ones.

> **RULE: REFACTORING TIME IS PATTERNS TIME**
> 
> Reexamine your design and introduce patterns when making changes to improve code structure without altering behavior.

> **RULE: REMOVAL IS ACCEPTABLE**
> 
> Do not be afraid to remove a pattern when a system becomes complex and the planned flexibility is no longer needed.

> **WARNING: AVOID OVERENGINEERING**
> 
> Design Patterns are not magic bullets; overuse leads to code that is downright overengineered.

---

## 1. Core Definition Architecture

```mermaid
graph LR
  A[Context] -->|Recurring Situation| B(Problem)
  B -->|Goal + Constraints| C{Design Pattern}
  C -->|Balances Forces| D[Solution]
```

* **Design Pattern:** A solution to a problem in a context.
* **Context:** The recurring situation in which the pattern applies.
* **Problem:** The goal you are trying to achieve, alongside any constraints occurring in the context.
* **Solution:** A general design that anyone can apply, which resolves the goal and set of constraints.
* ***Name:*** An essential component required to form a shared vocabulary with other developers.

---

## 2. Resolving Forces

```mermaid
graph TD
  F[Forces] --> G[The Light Side: Goal]
  F --> C[The Dark Side: Constraints]
  G --> S((Pattern Solution))
  C --> S
  S -->|Must Balance| F
```

* **Forces:** The pattern gurus' terminology for the goal and the set of constraints.
* **Balance:** A useful pattern is created only when a solution balances both sides of the force.

---

## 3. Classification Matrix

```mermaid
graph TD
  DP[Design Patterns] --> C[Creational]
  DP --> S[Structural]
  DP --> B[Behavioral]
  
  DP -.-> CL[Class Scope]
  DP -.-> OB[Object Scope]

  C --- CC(Decouples instantiation)
  S --- SC(Composes structures)
  B --- BC(Distributes responsibility)

  CL --- CLC(Inheritance / Compile Time)
  OB --- OBC(Composition / Runtime)
```

* **Creational Patterns:** Involve object instantiation to decouple a client from objects it needs.
* **Structural Patterns:** Compose classes or objects into larger structures.
* **Behavioral Patterns:** Dictate how classes and objects interact and distribute responsibility.
* **Class Patterns:** Define relationships via inheritance, established at compile time.
* **Object Patterns:** Describe relationships created by composition, making them dynamic and flexible at runtime.

---

## 4. The Mindset Evolution

```mermaid
graph LR
  B[Beginner] -->|Applies everywhere| I[Intermediate]
  I -->|Adapts to constraints| Z[Zen]
  Z -->|Prioritizes simplicity| E((Mastery))
```

* **Beginner Mind:** Uses patterns everywhere for experience, falsely believing more patterns equal a better design.
* **Intermediate Mind:** Identifies where patterns belong but may force square patterns into round holes before learning adaptation.
* **Zen Mind:** Looks for natural fits, prioritizes the simplest solutions, and applies pattern adaptations based on object principles.

---

## 5. Anti-Patterns Framework

```mermaid
graph TD
  P[Recurring Problem] -->|Highly Attractive| BS(Bad Solution)
  BS -->|Negative Long-term Impact| T[Trouble]
  BS -.->|Refactoring Suggestion| GS[Good Solution]
```

* **Definition:** Documents how to progress from a problem to a BAD solution to prevent repeated developer mistakes.
* **Anatomy:** Alerts you to why a bad solution is attractive upfront, explains why it causes trouble long-term, and suggests applicable good patterns.
* **Example (Golden Hammer):** Obsessively applying a familiar technology to architecture where it is clearly inappropriate due to fear of the unfamiliar.

---

## 6. The Patterns Zoo

```mermaid
graph LR
  PZ[Other Domains] --> A[Architectural]
  PZ --> AP[Application]
  PZ --> DS[Domain-Specific]
  PZ --> BP[Business Process]
  PZ --> O[Organizational]
  PZ --> UI[User Interface]
```

* **Architectural:** The origin of patterns, applied to buildings and towns.
* **Application:** System-level architecture.
* **Domain-Specific:** Solutions for specific fields like concurrent or real-time systems.
* **Business Process:** Interaction logic between businesses, customers, and data.
* **Organizational:** Structures of human organizations, heavily used in software support.
* **User Interface:** Solutions for designing interactive software.