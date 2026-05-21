package com.codealpha.chatbot.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single chat message.
 * Stores content, who sent it (USER or BOT), and the timestamp.
 */
public class Message {

    // ─── Enum ──────────────────────────────────────────────────────────────────
    public enum Sender {
        USER, BOT
    }

    // ─── Fields ────────────────────────────────────────────────────────────────
    private final String  content;
    private final Sender  sender;
    private final String  timestamp;

    // ─── Constructor ───────────────────────────────────────────────────────────
    public Message(String content, Sender sender) {
        this.content   = content;
        this.sender    = sender;
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    // ─── Getters ───────────────────────────────────────────────────────────────
    public String getContent()   { return content;   }
    public Sender getSender()    { return sender;    }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + sender + " @ " + timestamp + "]: " + content;
    }
}
