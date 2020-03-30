package best.aog.chat.client.view;

import best.aog.chat.client.controller.TCPConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientWindow extends JFrame implements ActionListener {



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField();
    private final JTextField fieldInput = new JTextField();

    private TCPConnection TCPConnection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true); // автоматический перенос слов
        add(log, BorderLayout.CENTER);

        add(fieldInput, BorderLayout.SOUTH);
        fieldInput.addActionListener(this);

        add(fieldNickname, BorderLayout.NORTH);

        setVisible(true);
        //connection =

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
