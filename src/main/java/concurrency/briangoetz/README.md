# Java Concurrency in Practice: Notes

Welcome to the central hub of this documentation project. This repository serves as a modernized and visual deep dive into the classic "Java Concurrency in Practice" (JCIP) by Brian Goetz et al.

## 🎯 Intent & Methodology
The primary goal of these documentation notes is to transform the foundational theory of Java concurrency into comprehensive resource.

Unlike standard summaries, these notes prioritize:
* **Visual Mental Models:** Utilizing Mermaid.js diagrams to illustrate complex thread interactions and memory visibility.
* **The "Why" Over the "What":** Focusing on underlying mechanics and theoretical drivers rather than just definitions.
* **Failure Analysis:** Providing "Breaking Code" scenarios to demonstrate what happens when these concepts are ignored in production.

## 🗺️ Documentation Roadmap
Below is the master structure of the notes. Each link leads to a detailed `README.md` containing the mental model, modern context, and code proofs for that specific chapter.

| Chapter | Title                                          | Status | Link                                    |
|:--------|:-----------------------------------------------|:-------|:----------------------------------------|
| **01**  | **Introduction**                               | ✅      | [View Notes](part1/chapter01/README.md) |
| **02**  | **Thread Safety**                              | ✅      | [View Notes](part1/chapter02/README.md) |
| **03**  | **Sharing Objects**                            | ✅      | [View Notes](part1/chapter03/README.md) |
| **04**  | **Composing Objects**                          | ✅      | [View Notes](part1/chapter04/README.md) |
| **05**  | **Building Blocks**                            | ✅      | [View Notes](part1/chapter05/README.md) |
|         | ***Summary I: Fundamentals of Thread Safety*** | ✅      | [View summary](part1/README.md)         |
| **06**  | **Task Execution**                             | ✅      | [View Notes](chapter06/README.md)       |
| **07**  | **Cancellation & Shutdown**                    | ⏳      | [View Notes](chapter07/README.md)       |
| **08**  | **Applying Thread Pools**                      | ⏳      | [View Notes](chapter08/README.md)       |
| **10**  | **Avoiding Liveness Hazards**                  | ⏳      | [View Notes](chapter10/README.md)       |
| **11**  | **Performance & Scalability**                  | ⏳      | [View Notes](chapter11/README.md)       |
| **13**  | **Explicit Locks**                             | ⏳      | [View Notes](chapter13/README.md)       |
| **14**  | **Building Custom Synchronizers**              | ⏳      | [View Notes](chapter14/README.md)       |
| **15**  | **Atomic Variables & Nonblocking**             | ⏳      | [View Notes](chapter15/README.md)       |
| **16**  | **The Java Memory Model**                      | ⏳      | [View Notes](chapter16/README.md)       |

---

## ⚖️ Authors' Rights & Attribution
This repository is a derivative study guide based on:
> **Java Concurrency in Practice** 
> *Authors: Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes, and Doug Lea.*

**Disclaimer:** These notes are for educational purpose. They reflect a synthesis of the original text's principles, updated with modern Java concurrency features. All credit for the foundational concepts and the original structure belongs to the authors of the book. Users are highly encouraged to purchase the original work for the full academic context.