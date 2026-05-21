# NIWA — AI Chatbot 🤖
### CodeAlpha Java Internship — Task 3

---

## 📋 Project Overview

**NIWA** (Artificial Responsive Intelligence Assistant) is a Java-based AI chatbot with a modern Swing GUI. It uses **rule-based Natural Language Processing (NLP)** through regex pattern matching to understand user input and generate contextual responses.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 💬 Conversation | Greetings, farewells, small talk |
| 🕐 Real-time Clock | Tells the current time |
| 📅 Date Awareness | Tells today's date |
| 🧮 Math Solver | Evaluates arithmetic: `+`, `-`, `*`, `/` |
| 😄 Jokes | 8+ built-in programmer jokes |
| ☕ Java Q&A | Answers questions about Java/OOP |
| ❓ Help Menu | Lists all capabilities |
| 🗑️ Clear Chat | Clears the conversation with confirmation |
| ✍️ Typing Indicator | Simulates NIWA "thinking" |

---

## 🏗️ Project Structure

```
CodeAlpha_AIChatbot/
├── src/
│   └── com/codealpha/chatbot/
│       ├── Main.java              ← Entry point
│       ├── model/
│       │   └── Message.java       ← Message data model
│       ├── engine/
│       │   └── ChatEngine.java    ← NLP brain (intent matching)
│       └── gui/
│           └── ChatbotGUI.java    ← Swing GUI (chat window)
├── out/                           ← Compiled .class files (auto-generated)
├── run.bat                        ← Windows run script
├── run.sh                         ← Linux/Mac run script
└── README.md
```

---

## 🛠️ Technologies Used

- **Java 11+** — Core language
- **Java Swing** — GUI framework (JFrame, JPanel, JLabel, JScrollPane)
- **Java Regex** — `java.util.regex` for NLP pattern matching
- **Java Time API** — `java.time` for live clock & date
- **OOP Principles** — Encapsulation, inner classes, separation of concerns

---

## 🚀 How to Run

### Prerequisites
- JDK 11 or higher installed
- `javac` and `java` available in your PATH

### Windows
```bash
run.bat
```

### Linux / Mac
```bash
chmod +x run.sh
./run.sh
```

### Manual (any OS)
```bash
# 1. Compile
javac -d out -sourcepath src src/com/codealpha/chatbot/Main.java src/com/codealpha/chatbot/model/Message.java src/com/codealpha/chatbot/engine/ChatEngine.java src/com/codealpha/chatbot/gui/ChatbotGUI.java

# 2. Run
java -cp out com.codealpha.chatbot.Main
```

---

## 🧠 How the NLP Works

1. User types a message and presses **Enter** or clicks **Send**.
2. `ChatEngine.getResponse(input)` is called.
3. The engine first checks if input is a **math expression** (regex: `\d+ op \d+`).
4. If not math, the input is matched against **14+ intent categories**, each with multiple regex patterns.
5. A **random response** is selected from the matched intent's response pool.
6. Special markers (`__TIME__`, `__DATE__`) are resolved dynamically.
7. If nothing matches, a **fallback response** is returned.

---

## 💬 Example Conversations

```
User: hello
NIWA: Hey! Great to see you. How can I help?

User: what time is it
NIWA: The current time is: 10:35 AM 🕐

User: 25 * 4
NIWA: 🧮 25 * 4 = 100

User: tell me a joke
NIWA: Why do programmers prefer dark mode?
      Because light attracts bugs! 🐛

User: who made you
NIWA: I was created as part of the CodeAlpha Java Internship program! 🎓
```

---

## 👤 Author

- **Name:** [Your Full Name]
- **Institution:** [Your University]
- **Internship:** CodeAlpha — Java Programming
- **GitHub:** [Your GitHub Profile]

---

## 📜 License

This project was built for educational purposes as part of the **CodeAlpha Java Internship Program**.
