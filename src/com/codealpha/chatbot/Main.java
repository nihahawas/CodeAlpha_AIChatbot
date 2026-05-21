package com.codealpha.chatbot;

import com.codealpha.chatbot.gui.ChatbotGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * ============================================================
 *  NIWA - Artificial Responsive Intelligence Assistant
 *  CodeAlpha Java Internship — Task 3: AI Chatbot
 *  Author : Niha Hawas
 *  Version: 1.0
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {
        // Use system look and feel for better font rendering
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default
        }

        // Launch GUI on the Event Dispatch Thread (EDT) — Swing requirement
        SwingUtilities.invokeLater(() -> {
            ChatbotGUI gui = new ChatbotGUI();
            gui.setVisible(true);
        });
    }
}
