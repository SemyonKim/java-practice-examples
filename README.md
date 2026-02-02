# Java Practice Examples

![Repo Size](https://img.shields.io/github/repo-size/SemyonKim/java-practice-examples) 
![Last Commit](https://img.shields.io/github/last-commit/SemyonKim/java-practice-examples) 
![License](https://img.shields.io/github/license/SemyonKim/java-practice-examples) 
![JDK](https://img.shields.io/badge/JDK-25-orange)
![Gradle](https://img.shields.io/badge/Gradle-9.2-02303A.svg?logo=gradle)
![Tagline](https://img.shields.io/badge/Hands--on_Java_practice_from_basics_to_advanced-1E90FF?style=for-the-badge)

This repository contains my practical implementations of selected examples from *On Java 8* by Bruce Eckel.  
I am not implementing every example from the book(s) — only those I find interesting or useful.  
In the future, I plan to expand with examples from other classic Java books, but *On Java 8* is the starting point.

---

## Index

- [Project Structure](#-project-structure)
- [Topics & Examples](#-topics--examples)
- [Requirements](#-requirements)
- [Build Tool](#️-build-tool)
- [Notes](#-notes)
- [Documentation](#-documentation)
- [Roadmap](#-roadmap)
- [License](#-license)

---

## 📂 Project Structure

Examples are organized by **topics** rather than chapters, so the repository can grow naturally:

- `basics/` – fundamental syntax, variables, operators
- `oop/` – classes, inheritance, polymorphism
- `generics/` – type safety, wildcards, API design
- `concurrency/` – threads, executors, synchronization
- `streams/` – functional programming, lambdas, streams API
- `collections/` – lists, sets, maps, iterators
- `io/` – file handling, serialization
- `exceptions/` – try/catch, checked vs. unchecked, restrictions, custom exceptions
- `advanced/` – reflection, annotations, JVM internals

Each folder may contain subfolders for finer granularity (e.g., `basics/string/example1`) and includes examples with commentary and improvements.

---

## 📑 Topics & Examples

Here is a growing index of examples with direct links:

- **Basics**
  - [HelloWorld](src/main/java/basics/HelloWorld.java)
  - [OnJava8 : Unsigned Right Shift](src/main/java/basics/shift/UnsignedRightShift.java)
  - [OnJava8 : Overloading on Return Values](src/main/java/basics/overloading/ReturnTypeOverload.java)
  - [OnJava8 : Overloading Varargs](src/main/java/basics/overloading/OverloadingVarargs.java)
  - *(more examples coming soon)*

- **OOP**
  - [OnJava8 : Complete Decoupling](src/main/java/oop/decoupling/Applicator.java)
  - [OnJava8 : Inner Classes - Closures & Callbacks](src/main/java/oop/innerclasses/Callbacks.java)
  - *(more examples coming soon)*

- **Generics**
  - [OnJava8 : Template Method Design Pattern](src/main/java/generics/creator/CreatorGeneric.java)
  - *(more examples coming soon)*

- **Concurrency**
  - *(examples to be added)*

- **Streams**
  - *(examples to be added)*

- **Collections**
  - *(examples to be added)*

- **IO**
  - *(examples to be added)*

- **Exceptions**
  - [OnJava8 : Restrictions](src/main/java/exceptions/restrictions/StormyInning.java)
  - *(more examples coming soon)*

- **Advanced**
  - [OnJava8 : Design by Contract + Unit Testing](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueueException.java)
  - [OnJava8 : Proxy Design and Null Object Patterns](src/main/java/advanced/tagginginterfaces/Null.java)
  - *(more examples coming soon)*

---

## 🛠 Requirements

- JDK 25 (or whichever version you’re using)
- Gradle 9.2 (wrapper included in repo)
- Tested on Windows 11
- IDE: IntelliJ IDEA (recommended)

---

## ⚙️ Build Tool

This project uses **Gradle** for build and dependency management.

### Build & Run

To compile and run an example:

```bash
./gradlew build
./gradlew run --args="basics.HelloWorld"
```
Replace `basics.HelloWorld` with the fully qualified class name of the example you want to run.

---

## 📖 Notes
Each example may include:
- Selected code inspired by *On Java 8* (not a full reproduction of the book’s examples)
- My commentary and improvements
- Edge cases or compiler error demos
- Additional resources (if required, placed under `src/main/resources`)

### ⚠️ About Compile‑Error Examples 
This repository contains both working examples and examples that intentionally produce compiler errors. 
These error‑producing snippets are included to illustrate Java’s rules and limitations. 
- All error examples are **commented out** so the project can compile cleanly.

**Why?** 
- Commented examples let you read the code and understand the issue without breaking the build. 


---

## 📚 Documentation
- Commentary is embedded directly in the source code for clarity.
- Topic folders act as a **reference library** of reusable examples.
- Future plan: expand documentation into a `docs/` folder with summaries, diagrams, and links to examples.

---

## 🚀 Roadmap
- [x] Initial Gradle setup with HelloWorld
- [ ] Add examples from *On Java 8*
- [ ] Expand with examples from other Java books
- [ ] Add topic‑specific resources (e.g., JSON, text files under `src/main/resources`)
- [ ] Continuous integration with GitHub Actions (`./gradlew build` on push)

---

## 🌍 Author
- 👤 Semyon Kim
- 📍 Uzbekistan
- 🗣️ Languages: Russian (native), English (intermediate), Korean (elementary)
- 🔗 [GitHub](https://github.com/SemyonKim)

---

## 📜 License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
