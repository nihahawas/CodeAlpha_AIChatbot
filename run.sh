#!/bin/bash
echo "============================================"
echo " NIWA Chatbot - CodeAlpha Java Internship"
echo "============================================"
echo

echo "[1/2] Compiling Java source files..."
mkdir -p out

javac -d out -sourcepath src \
  src/com/codealpha/chatbot/Main.java \
  src/com/codealpha/chatbot/model/Message.java \
  src/com/codealpha/chatbot/engine/ChatEngine.java \
  src/com/codealpha/chatbot/gui/ChatbotGUI.java

if [ $? -ne 0 ]; then
  echo "[ERROR] Compilation failed. Make sure JDK is installed."
  exit 1
fi

echo "[2/2] Launching NIWA Chatbot..."
echo
java -cp out com.codealpha.chatbot.Main
