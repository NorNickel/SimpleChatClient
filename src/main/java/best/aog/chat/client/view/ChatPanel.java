package best.aog.chat.client.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
class ChatPanel extends JPanel {
    private JTextArea messages;
    private JButton sendButton;

    public ChatPanel() {
        messages = new JTextArea(30, 50);
        add(new JScrollPane(messages));

        sendButton = new JButton("Send");
        add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(e ->
        {

            sendButton.setEnabled(true);
            messages.append("sendButton clicked");
            /*
            connectThread = new Thread(() -> {
                try {
                    connectInterruptibly();
                } catch (IOException e1) {
                    messages.append("\nInterruptibleSocketTest.connectInterruptibly: " + e1);
                }
            });
            connectThread.start();
        });

            */
        });
    }
}