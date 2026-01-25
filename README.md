# Java Practice Examples

This repository contains my practical implementations of examples from various Java books.  
I started with *On Java 8* by Bruce Eckel, and plan to expand with examples from other classics such as *Core Java* by Cay Horstmann and *Effective Java* by Joshua Bloch.

## 📂 Structure
Examples are organized by **topics** rather than chapters, so the repository can grow across multiple books:
- `basics/` – fundamental syntax, variables, operators
- `oop/` – classes, inheritance, polymorphism
- `generics/` – type safety, wildcards, API design
- `concurrency/` – threads, executors, synchronization
- `streams/` – functional programming, lambdas, streams API
- `collections/` – lists, sets, maps, iterators
- `io/` – file handling, serialization
- `advanced/` – reflection, annotations, JVM internals

Each folder contains examples with commentary and improvements.

## ⚙️ Build Tool
This project uses **Gradle** for build and dependency management.

### Build & Run
To compile and run an example:
```bash
./gradlew build
./gradlew run --args="chapter01.HelloWorld"

(Replace chapter01.HelloWorld with the fully qualified class name of the example you want to run.)

📖 Notes
Each example may include:

Original code inspired by the book

My commentary and improvements

Edge cases or compiler error demos
