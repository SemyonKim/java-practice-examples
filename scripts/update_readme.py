import os
import re

BASE_DIR = "src/main/java"
README = "README.md"

def collect_examples():
    topics = {}
    for root, _, files in os.walk(BASE_DIR):
        for f in files:
            if f.endswith(".java"):
                topic = os.path.relpath(root, BASE_DIR)
                topics.setdefault(topic, []).append(f)
    return topics

def generate_section(topics):
    lines = ["## 📑 Topics & Examples\n"]
    for topic, files in sorted(topics.items()):
        lines.append(f"- **{topic.capitalize()}**")
        for f in sorted(files):
            path = os.path.join(BASE_DIR, topic, f).replace("\\", "/")
            name = f[:-5]  # remove .java
            lines.append(f"  - [{name}]({path})")
        lines.append("")
    return "\n".join(lines)

def replace_section(content, new_section):
    pattern = r"## 📑 Topics & Examples.*?## 🛠 Requirements"
    return re.sub(pattern, new_section + "\n## 🛠 Requirements", content, flags=re.S)

def main():
    topics = collect_examples()
    new_section = generate_section(topics)
    with open(README, encoding="utf-8") as f:
        content = f.read()
    updated = replace_section(content, new_section)
    with open(README, "w", encoding="utf-8") as f:
        f.write(updated)

if __name__ == "__main__":
    main()
