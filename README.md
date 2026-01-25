# Java Practice Examples

![Repo Size](https://img.shields.io/github/repo-size/SemyonKim/java-practice-examples) 
![Last Commit](https://img.shields.io/github/last-commit/SemyonKim/java-practice-examples) 
![License](https://img.shields.io/github/license/SemyonKim/java-practice-examples) 
![JDK](https://img.shields.io/badge/JDK-25-orange)
![Gradle](https://img.shields.io/badge/Gradle-9.2-02303A.svg?logo=gradle)
![Tagline](https://img.shields.io/badge/Hands--on_Java_practice_from_basics_to_advanced-1E90FF?style=for-the-badge)

This repository contains my practical implementations of examples from *On Java 8* by Bruce Eckel.  
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
- `advanced/` – reflection, annotations, JVM internals

Each folder may contain subfolders for finer granularity (e.g., `basics/string/example1`) and includes examples with commentary and improvements.

---

## 📑 Topics & Examples

Here is a growing index of examples with direct links:

- **Basics**
  - [HelloWorld](src/main/java/basics/HelloWorld.java)
  - *(more examples coming soon)*

- **OOP**
  - *(examples to be added)*

- **Generics**
  - *(examples to be added)*

- **Concurrency**
  - *(examples to be added)*

- **Streams**
  - *(examples to be added)*

- **Collections**
  - *(examples to be added)*

- **IO**
  - *(examples to be added)*

- **Advanced**
  - *(examples to be added)*

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
- Original code inspired by *On Java 8*
- My commentary and improvements
- Edge cases or compiler error demos
- Additional resources (if required, placed under `src/main/resources`)

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

## 📜 License
This project is licensed under the MIT License – see the LICENSE file for details.
