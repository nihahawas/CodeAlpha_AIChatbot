package com.codealpha.chatbot.gui;

import com.codealpha.chatbot.engine.ChatEngine;
import com.codealpha.chatbot.model.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  ChatbotGUI — Main Swing window for NIWA chatbot.
 *
 *  Layout:
 *   ┌─────────────────────────┐
 *   │        HEADER           │  ← bot name, status, clear btn
 *   ├─────────────────────────┤
 *   │                         │
 *   │       CHAT AREA         │  ← scrollable message bubbles
 *   │                         │
 *   ├─────────────────────────┤
 *   │  [input field] [Send ➤] │  ← input panel
 *   └─────────────────────────┘
 * ============================================================
 */
public class ChatbotGUI extends JFrame {

    // ─── Color Palette ─────────────────────────────────────────────────────────
    private static final Color BG_COLOR          = new Color(15, 15, 28);      // deep navy background
    private static final Color HEADER_COLOR      = new Color(22, 22, 42);      // slightly lighter header
    private static final Color BOT_BUBBLE_COLOR  = new Color(40, 40, 72);      // muted purple bot bubble
    private static final Color USER_BUBBLE_COLOR = new Color(79,  91, 213);    // vibrant blue user bubble
    private static final Color INPUT_BG          = new Color(28, 28, 52);      // input field background
    private static final Color TEXT_COLOR        = new Color(235, 235, 255);   // near-white text
    private static final Color MUTED_COLOR       = new Color(130, 130, 165);   // timestamps & placeholders
    private static final Color ACCENT_COLOR      = new Color(79,  91, 213);    // primary accent
    private static final Color ONLINE_GREEN      = new Color(87,  215, 135);   // status dot
    private static final Color SEPARATOR_COLOR   = new Color(40,  40,  75);    // divider lines

    // ─── UI Components ─────────────────────────────────────────────────────────
    private final ChatEngine   chatEngine;
    private final List<Message> messageHistory = new ArrayList<>();

    private JPanel     chatPanel;
    private JScrollPane scrollPane;
    private JTextField  inputField;
    private JButton     sendButton;

    // ─── Constructor ───────────────────────────────────────────────────────────
    public ChatbotGUI() {
        chatEngine = new ChatEngine();
        initUI();
        addBotMessage(
            "Hello! I'm NIWA \uD83E\uDD16 — your Neural Intelligent Wise Assistant!\n\n"
            + "Here's what I can do:\n"
            + "  \u2022 Tell you the current time & date\n"
            + "  \u2022 Solve math  (e.g., 25 * 4 or 100 / 3)\n"
            + "  \u2022 Tell jokes & have fun conversations\n"
            + "  \u2022 Answer questions about Java & programming\n\n"
            + "Type 'help' to see all features. How can I assist you today?"
        );
    }

    // ─── UI Initialisation ─────────────────────────────────────────────────────

    private void initUI() {
        setTitle("NIWA - 2026 - AIChatbot");
        setSize(520, 720);
        setMinimumSize(new Dimension(400, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        add(buildHeader(),     BorderLayout.NORTH);
        add(buildChatArea(),   BorderLayout.CENTER);
        add(buildInputPanel(), BorderLayout.SOUTH);
    }

    // ── Header ─────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SEPARATOR_COLOR),
            new EmptyBorder(14, 20, 14, 20)
        ));

        // Left side: avatar circle + name + status
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftSide.setBackground(HEADER_COLOR);

        // Avatar – painted circle with "A"
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient fill
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 110, 230),
                                                     getWidth(), getHeight(), new Color(60, 70, 190));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Letter
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth("NW")) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("NW", tx, ty);
            }
        };
        avatar.setPreferredSize(new Dimension(42, 42));

        // Name + status row
        JPanel nameStack = new JPanel();
        nameStack.setLayout(new BoxLayout(nameStack, BoxLayout.Y_AXIS));
        nameStack.setBackground(HEADER_COLOR);

        JLabel nameLabel = new JLabel("NIWA");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_COLOR);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        statusRow.setBackground(HEADER_COLOR);

        JLabel dot = new JLabel("\u25CF");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        dot.setForeground(ONLINE_GREEN);

        JLabel statusLabel = new JLabel("Online \u2022 AI Chatbot");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(MUTED_COLOR);

        statusRow.add(dot);
        statusRow.add(statusLabel);

        nameStack.add(nameLabel);
        nameStack.add(statusRow);

        leftSide.add(avatar);
        leftSide.add(nameStack);

        // Right side: Clear button
        JButton clearBtn = createStyledButton("Clear Chat", false);
        clearBtn.addActionListener(e -> clearChat());

        header.add(leftSide,  BorderLayout.WEST);
        header.add(clearBtn,  BorderLayout.EAST);
        return header;
    }

    // ── Chat Area ──────────────────────────────────────────────────────────────
    private JScrollPane buildChatArea() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BG_COLOR);
        chatPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        // Style the scrollbar
        scrollPane.getVerticalScrollBar().setBackground(BG_COLOR);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        return scrollPane;
    }

    // ── Input Panel ────────────────────────────────────────────────────────────
    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, SEPARATOR_COLOR),
            new EmptyBorder(14, 18, 14, 18)
        ));

        // Text input
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(INPUT_BG);
        inputField.setForeground(MUTED_COLOR);
        inputField.setCaretColor(TEXT_COLOR);
        inputField.setText("Type a message...");
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SEPARATOR_COLOR, 1, true),
            new EmptyBorder(10, 16, 10, 16)
        ));

        // Placeholder behaviour
        inputField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if ("Type a message...".equals(inputField.getText())) {
                    inputField.setText("");
                    inputField.setForeground(TEXT_COLOR);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText("Type a message...");
                    inputField.setForeground(MUTED_COLOR);
                }
            }
        });

        // Enter key sends message
        inputField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();
            }
        });

        // Send button
        sendButton = createStyledButton("Send  \u27a4", true);
        sendButton.addActionListener(e -> sendMessage());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        return panel;
    }

    // ─── Sending Messages ──────────────────────────────────────────────────────

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || "Type a message...".equals(text)) return;

        // Show user message immediately
        addUserMessage(text);
        inputField.setText("");
        inputField.setForeground(TEXT_COLOR);
        sendButton.setEnabled(false);

        // Add typing indicator to chat panel (we're on EDT, so direct access is safe)
        JPanel typingWrapper = buildTypingIndicator();
        chatPanel.add(typingWrapper);
        chatPanel.add(Box.createVerticalStrut(4));
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();

        // Process bot response on background thread (simulates "thinking")
        final String userInput = text;
        new Thread(() -> {
            int delay = 800 + (int)(Math.random() * 700); // 800-1500 ms
            try { Thread.sleep(delay); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }

            String botResponse = chatEngine.getResponse(userInput);

            SwingUtilities.invokeLater(() -> {
                // Remove typing indicator
                chatPanel.remove(typingWrapper);
                // Remove the strut after it (last component)
                int count = chatPanel.getComponentCount();
                if (count > 0) {
                    Component last = chatPanel.getComponent(count - 1);
                    if (last instanceof Box.Filler) chatPanel.remove(last);
                }
                // Add bot reply
                addBotMessage(botResponse);
                sendButton.setEnabled(true);
                inputField.requestFocusInWindow();
            });
        }).start();
    }

    // ─── Message Bubble Builders ───────────────────────────────────────────────

    private void addUserMessage(String text) {
        Message msg = new Message(text, Message.Sender.USER);
        messageHistory.add(msg);
        appendBubble(msg);
    }

    private void addBotMessage(String text) {
        Message msg = new Message(text, Message.Sender.BOT);
        messageHistory.add(msg);
        appendBubble(msg);
    }

    /**
     * Adds a message bubble to the chat panel.
     * User bubbles are aligned right (blue), bot bubbles left (dark purple).
     */
    private void appendBubble(Message message) {
        boolean isUser = message.getSender() == Message.Sender.USER;
        Color   bubbleColor = isUser ? USER_BUBBLE_COLOR : BOT_BUBBLE_COLOR;

        // ── Outer row (full width, aligns bubble left or right) ────────────────
        JPanel row = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        row.setBackground(BG_COLOR);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Bubble with rounded rectangle background ───────────────────────────
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bubbleColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            }
        };
        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setMaximumSize(new Dimension(360, Integer.MAX_VALUE));

        // ── Content panel inside bubble (text + timestamp) ─────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 14, 8, 14));

        // Message text rendered as HTML — handles newlines and wrapping
        String htmlMsg = "<html><div style='width:260px; font-family:Segoe UI,NIWAl; font-size:11pt;"
                       + " color:rgb(235,235,255); line-height:1.5;'>"
                       + escapeHtml(message.getContent()).replace("\n", "<br>")
                       + "</div></html>";
        JLabel textLabel = new JLabel(htmlMsg);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Timestamp
        JLabel timeLabel = new JLabel(message.getTimestamp());
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(180, 180, 210));
        timeLabel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        content.add(textLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(timeLabel);

        bubble.add(content, BorderLayout.CENTER);
        row.add(bubble);

        chatPanel.add(row);
        chatPanel.add(Box.createVerticalStrut(6));
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    /** Small "NIWA is typing..." indicator panel. */
    private JPanel buildTypingIndicator() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        wrapper.setBackground(BG_COLOR);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("   \u22ef  NIWA is typing...");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        label.setForeground(MUTED_COLOR);

        wrapper.add(label);
        return wrapper;
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void clearChat() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Clear all chat messages?",
            "Clear Chat",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            chatPanel.removeAll();
            messageHistory.clear();
            chatPanel.revalidate();
            chatPanel.repaint();
            addBotMessage("Chat cleared! \uD83D\uDDD1\uFE0F How can I help you?");
        }
    }

    /** Escapes special HTML characters in message text. */
    private String escapeHtml(String text) {
        return text.replace("&",  "&amp;")
                   .replace("<",  "&lt;")
                   .replace(">",  "&gt;")
                   .replace("\"", "&quot;");
    }

    /**
     * Creates a styled button with two modes:
     *  - primary (filled accent background) for Send
     *  - secondary (outlined) for Clear
     */
    private JButton createStyledButton(String label, boolean primary) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primary) {
                    g2.setColor(isEnabled() ? ACCENT_COLOR : new Color(50, 50, 90));
                } else {
                    g2.setColor(INPUT_BG);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                if (!primary) {
                    g2.setColor(SEPARATOR_COLOR);
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(primary ? Color.WHITE : MUTED_COLOR);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
