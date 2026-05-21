@echo off
echo ============================================
echo  NIWA Chatbot - CodeAlpha Java Internship
echo ============================================
echo.

echo [1/2] Compiling Java source files...
if not exist "out" mkdir out

javac -d out -sourcepath src ^
  src\com\codealpha\chatbot\Main.java ^
  src\com\codealpha\chatbot\model\Message.java ^
  src\com\codealpha\chatbot\engine\ChatEngine.java ^
  src\com\codealpha\chatbot\gui\ChatbotGUI.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Make sure JDK is installed.
    pause
    exit /b 1
)

echo [2/2] Launching NIWA Chatbot...
echo.
java -cp out com.codealpha.chatbot.Main
pause
