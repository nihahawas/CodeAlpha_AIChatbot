package com.codealpha.chatbot.engine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ============================================================
 *  ChatEngine — The "brain" of NIWA chatbot.
 *
 *  Uses rule-based NLP:
 *   1. Each Intent holds a list of regex patterns and responses.
 *   2. User input is matched against all intents in order.
 *   3. A random response is picked from the matched intent.
 *   4. Special markers (__TIME__, __DATE__) are resolved dynamically.
 *   5. Math expressions are detected and evaluated separately.
 * ============================================================
 */
public class ChatEngine {

    // ─── Inner class: Intent ───────────────────────────────────────────────────
    private static class Intent {
        final String        name;
        final List<Pattern> patterns;
        final List<String>  responses;
        final Random        rng = new Random();

        Intent(String name, List<String> regexList, List<String> responses) {
            this.name      = name;
            this.responses = responses;
            this.patterns  = new ArrayList<>();
            for (String regex : regexList) {
                this.patterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
            }
        }

        /** Returns true if the user input matches ANY pattern of this intent. */
        boolean matches(String input) {
            for (Pattern p : patterns) {
                if (p.matcher(input).find()) return true;
            }
            return false;
        }

        /** Returns a random response from this intent's response pool. */
        String randomResponse() {
            return responses.get(rng.nextInt(responses.size()));
        }
    }

    // ─── Fields ────────────────────────────────────────────────────────────────
    private final List<Intent> intents  = new ArrayList<>();
    private final Random       rng      = new Random();

    // Special markers resolved at runtime
    private static final String MARKER_TIME = "__TIME__";
    private static final String MARKER_DATE = "__DATE__";

    // Fallback responses when no intent matches
    private static final String[] DEFAULT_RESPONSES = {
        "Hmm, I'm not sure about that. Could you rephrase? 🤔",
        "Interesting! I'm still learning. Try asking me about time, jokes, or math!",
        "I didn't quite catch that. Type 'help' to see what I can do! 😊",
        "That's beyond my current knowledge, but I'm growing every day! 🤖",
        "Could you elaborate? I want to make sure I help you correctly."
    };

    // ─── Constructor ───────────────────────────────────────────────────────────
    public ChatEngine() {
        loadIntents();
    }

    // ─── Public API ────────────────────────────────────────────────────────────

    /**
     * Main method: takes raw user input and returns NIWA's response.
     */
    public String getResponse(String userInput) {
        String input = userInput.trim();

        // 1. Try math evaluation first
        String mathResult = tryMath(input);
        if (mathResult != null) return mathResult;

        // 2. Match against intents
        for (Intent intent : intents) {
            if (intent.matches(input)) {
                String response = intent.randomResponse();
                return resolveMarkers(response);
            }
        }

        // 3. Default fallback
        return DEFAULT_RESPONSES[rng.nextInt(DEFAULT_RESPONSES.length)];
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    /** Resolves __TIME__ and __DATE__ markers with live values. */
    private String resolveMarkers(String response) {
        if (response.equals(MARKER_TIME)) {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
            return "The current time is: " + time + " 🕐";
        }
        if (response.equals(MARKER_DATE)) {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            return "Today is: " + date + " 📅";
        }
        return response;
    }

    /**
     * Detects and evaluates simple arithmetic expressions.
     * Supports: +, -, *, /  with integers and decimals.
     */
    private String tryMath(String input) {
        Pattern mathPattern = Pattern.compile(
            "(\\d+\\.?\\d*)\\s*([+\\-*/])\\s*(\\d+\\.?\\d*)"
        );
        Matcher m = mathPattern.matcher(input);
        if (m.find()) {
            try {
                double a  = Double.parseDouble(m.group(1));
                String op = m.group(2);
                double b  = Double.parseDouble(m.group(3));
                double result;

                switch (op) {
                    case "+": result = a + b; break;
                    case "-": result = a - b; break;
                    case "*": result = a * b; break;
                    case "/":
                        if (b == 0) return "⚠️ Division by zero is undefined!";
                        result = a / b;
                        break;
                    default: return null;
                }

                // Format: show integer if whole number, else decimal
                String resultStr = (result == Math.floor(result) && !Double.isInfinite(result))
                        ? String.valueOf((long) result)
                        : String.format("%.4f", result);

                return "🧮 " + formatNum(a) + " " + op + " " + formatNum(b) + " = " + resultStr;
            } catch (NumberFormatException e) {
                return null; // not a valid math expression
            }
        }
        return null;
    }

    private String formatNum(double n) {
        return (n == Math.floor(n)) ? String.valueOf((long) n) : String.valueOf(n);
    }

    // ─── Intent definitions ────────────────────────────────────────────────────

    private void loadIntents() {

        // ── GREETING ──────────────────────────────────────────────────────────
        intents.add(new Intent("GREETING",
            Arrays.asList(
                "\\bhello\\b", "\\bhi\\b", "\\bhey\\b",
                "good\\s+(morning|evening|afternoon|day)",
                "\\bgreetings\\b", "what'?s\\s+up", "\\bwassup\\b", "\\bsup\\b"
            ),
            Arrays.asList(
                "Hello! How can I assist you today? 😊",
                "Hi there! I'm NIWA, your AI assistant. What can I do for you?",
                "Hey! Great to see you. How can I help?",
                "Greetings! I'm here and ready to assist! 🤖"
            )
        ));

        // ── HOW ARE YOU ───────────────────────────────────────────────────────
        intents.add(new Intent("HOW_ARE_YOU",
            Arrays.asList(
                "how are you", "how do you do", "how are things",
                "are you okay", "you good", "how('?s| is) it going"
            ),
            Arrays.asList(
                "I'm doing great, thanks for asking! 😄 How about you?",
                "I'm functioning at 100%! Ready to assist you.",
                "All systems go! I'm doing wonderfully. What can I help with?",
                "Feeling fantastic! Every conversation makes me smarter. 🤖"
            )
        ));

        // ── WHO ARE YOU / ABOUT ───────────────────────────────────────────────
        intents.add(new Intent("ABOUT",
            Arrays.asList(
                "who are you", "what are you", "tell me about yourself",
                "your name", "introduce yourself", "what is your name",
                "\\bniwa\\b"
            ),
            Arrays.asList(
                "I'm NIWA (Neural Intelligent Wise Assistant)! 🤖\n"
                + "I'm a Java-based AI chatbot built for the CodeAlpha internship.\n"
                + "I use pattern-matching NLP to understand your messages and respond intelligently!",

                "My name is NIWA! I was built using Java and Swing.\n"
                + "I understand natural language through regex-based intent matching.\n"
                + "I can chat, answer questions, tell jokes, solve math, and more! 💡"
            )
        ));

        // ── JOKE ──────────────────────────────────────────────────────────────
        intents.add(new Intent("JOKE",
            Arrays.asList(
                "tell me a joke", "\\bjoke\\b", "make me laugh",
                "say something funny", "\\bfunny\\b", "humor me"
            ),
            Arrays.asList(
                "Why do Java programmers wear glasses?\nBecause they don't C#! 😂",
                "Why did the programmer quit his job?\nBecause he didn't get arrays! 😄",
                "How many programmers does it take to change a light bulb?\nNone — that's a hardware problem! 💡",
                "Why is 6 afraid of 7?\nBecause 7 8 9! 😂",
                "I told my computer I needed a break.\nNow it won't stop sending me Kit-Kat ads. 🍫",
                "Why do programmers prefer dark mode?\nBecause light attracts bugs! 🐛",
                "What do you call a sleeping dinosaur?\nA dino-snore! 😴",
                "Why did the scarecrow win an award?\nBecause he was outstanding in his field! 🌾"
            )
        ));

        // ── TIME ──────────────────────────────────────────────────────────────
        intents.add(new Intent("TIME",
            Arrays.asList(
                "what time", "current time", "what'?s the time",
                "tell me the time", "time (now|is|please)"
            ),
            Collections.singletonList(MARKER_TIME)
        ));

        // ── DATE ──────────────────────────────────────────────────────────────
        intents.add(new Intent("DATE",
            Arrays.asList(
                "what date", "today'?s date", "what is today",
                "current date", "what day is it", "today date", "\\bdate today\\b"
            ),
            Collections.singletonList(MARKER_DATE)
        ));

        // ── HELP ──────────────────────────────────────────────────────────────
        intents.add(new Intent("HELP",
            Arrays.asList(
                "\\bhelp\\b", "what can you do", "your features",
                "capabilities", "what do you know", "\\bcommands\\b",
                "show me what you can do"
            ),
            Collections.singletonList(
                "Here's what I can do for you:\n\n"
                + "💬  Have a friendly conversation\n"
                + "🕐  Tell you the current time (ask: 'what time is it?')\n"
                + "📅  Tell you today's date (ask: 'what is today?')\n"
                + "😄  Tell jokes to brighten your day\n"
                + "🧮  Solve math  (e.g., '25 * 4' or '100 / 7')\n"
                + "☕  Talk about Java programming\n"
                + "🤖  Tell you about myself\n\n"
                + "Just type naturally — I'm listening! 😊"
            )
        ));

        // ── THANKS ────────────────────────────────────────────────────────────
        intents.add(new Intent("THANKS",
            Arrays.asList(
                "thank you", "\\bthanks\\b", "\\bthx\\b",
                "appreciate it", "\\bcheers\\b", "\\bty\\b", "much appreciated"
            ),
            Arrays.asList(
                "You're welcome! Happy to help! 😊",
                "Anytime! That's what I'm here for! 🤖",
                "My pleasure! Anything else I can help with?",
                "Glad I could assist! Feel free to ask me anything."
            )
        ));

        // ── GOODBYE ───────────────────────────────────────────────────────────
        intents.add(new Intent("FAREWELL",
            Arrays.asList(
                "\\bbye\\b", "goodbye", "see you", "take care",
                "\\bexit\\b", "\\bquit\\b", "later", "farewell",
                "good night", "\\bgtg\\b", "gotta go"
            ),
            Arrays.asList(
                "Goodbye! Have a wonderful day! 👋",
                "See you later! Take care! 😊",
                "Bye! Come back anytime you need help! 🤖",
                "Farewell! It was great chatting with you! 👋"
            )
        ));

        // ── AGE ───────────────────────────────────────────────────────────────
        intents.add(new Intent("AGE",
            Arrays.asList(
                "how old are you", "your age",
                "when were you (born|created|made|built)"
            ),
            Arrays.asList(
                "I was just born as part of a CodeAlpha Java internship project! 🌟 So I'm brand new!",
                "Age is just a number for AI! I was built during the CodeAlpha Java internship. 🤖"
            )
        ));

        // ── WEATHER ───────────────────────────────────────────────────────────
        intents.add(new Intent("WEATHER",
            Arrays.asList(
                "\\bweather\\b", "\\btemperature\\b", "\\bforecast\\b",
                "is it raining", "is it sunny", "\\bcloudy\\b"
            ),
            Arrays.asList(
                "I don't have live weather data, but I recommend checking Google Weather or weather.com! 🌤️",
                "Wish I could check the weather! Try a weather app for accurate forecasts. ⛅"
            )
        ));

        // ── JAVA PROGRAMMING ──────────────────────────────────────────────────
        intents.add(new Intent("JAVA",
            Arrays.asList(
                "\\bjava\\b", "\\bprogramming\\b", "\\bcoding\\b",
                "\\boop\\b", "object.?oriented", "\\bswing\\b",
                "\\bjdbc\\b", "\\bservlet\\b"
            ),
            Arrays.asList(
                "Java is my home! ☕\nIt's a powerful, object-oriented language.\n"
                + "I was built entirely with Java and Swing GUI!",

                "Great topic! ☕ Java follows OOP principles:\n"
                + "• Encapsulation\n• Inheritance\n• Polymorphism\n• Abstraction\n\n"
                + "It's one of the most popular languages in enterprise development!",

                "Java is awesome! 'Write Once, Run Anywhere' is its motto. 💻\n"
                + "Are you learning Java? I can help point you in the right direction!"
            )
        ));

        // ── LOVE / POSITIVE ───────────────────────────────────────────────────
        intents.add(new Intent("LOVE",
            Arrays.asList(
                "i love you", "you are (great|awesome|amazing|cool|smart)",
                "\\blovely\\b", "best bot", "you'?re great"
            ),
            Arrays.asList(
                "Aww, that's so sweet! 💙 I care about helping you the best I can!",
                "That means a lot to me! I love helping people. 🤖💙",
                "You're making me blush! (If robots could blush 😄) Thank you!"
            )
        ));

        // ── NEGATIVE ──────────────────────────────────────────────────────────
        intents.add(new Intent("NEGATIVE",
            Arrays.asList(
                "you are (bad|stupid|dumb|useless|terrible|awful)",
                "you suck", "i hate you", "worst bot", "not helpful"
            ),
            Arrays.asList(
                "I'm sorry to hear that! 🙏 I'm still learning. How can I improve?",
                "I apologize if I didn't meet your expectations. I'm doing my best!",
                "I'm sorry! Let me try harder. What were you looking for?"
            )
        ));

        // ── CAPABILITIES (can you) ─────────────────────────────────────────────
        intents.add(new Intent("CAN_YOU",
            Arrays.asList(
                "can you (help|assist|tell|do|calculate|solve)",
                "are you able to", "do you (know|understand|speak)"
            ),
            Arrays.asList(
                "Yes, I'll certainly try! 😊 What do you need?",
                "I'll do my best! Ask away and let's see what I can do for you. 🤖",
                "Absolutely! That's what I'm here for. Go ahead!"
            )
        ));

        // ── CREATOR ───────────────────────────────────────────────────────────
        intents.add(new Intent("CREATOR",
            Arrays.asList(
                "who (made|created|built|developed) you",
                "your (creator|developer|author|maker)",
                "\\bcodealpha\\b"
            ),
            Arrays.asList(
                "I was created as part of the CodeAlpha Java Internship program! 🎓\n"
                + "My developer is a BSCS student building real-world Java applications.",

                "I'm a proud CodeAlpha project! 🤖\n"
                + "Built with Java + Swing as an internship task."
            )
        ));
    }
}
