package chat_cl;
import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Client {
    BufferedReader in;
    PrintWriter out;
    Socket socket;
    String name;
    Gui g;

    public Client(JFrame f) {
        init();
        g = new Gui(f);
    }

    private void init() {
        try {
            name = JOptionPane.showInputDialog("Please enter your name");
            if (name == null || name.isEmpty()) {
                throw new Exception("No name entered, exiting.");
            }
            String server = "localhost"; // Example, replace with your server address
            InetAddress addr = InetAddress.getByName(server);
            socket = new Socket(addr, 9393);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection to server failed: " + e.getMessage());
            System.exit(1);
        }
    }

    class Gui {
        JFrame frame;
        JPanel loginPanel, chatPanel;
        JTextField usernameField, targetUserField, messageField;
        JPasswordField passwordField;
        JButton submitButton, sendButton, fileButton;
        JTextArea chatArea;
        JLabel statusLabel;

        Gui(JFrame f) {
            this.frame = f;
            frame.setLayout(new BorderLayout());

            createLoginPanel();
            frame.add(loginPanel, BorderLayout.CENTER);
            frame.setSize(500, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.setTitle(name + "'s Chat Interface");

            new Rcv().start();
        }

        void createLoginPanel() {
            loginPanel = new JPanel(new GridLayout(3, 2));
            loginPanel.add(new JLabel("Username:"));
            usernameField = new JTextField(10);
            loginPanel.add(usernameField);
            loginPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField(10);
            loginPanel.add(passwordField);

            submitButton = new JButton("Submit");
            submitButton.addActionListener(this::performLogin);
            loginPanel.add(submitButton);

            statusLabel = new JLabel("Please login to continue.");
            loginPanel.add(statusLabel);
        }

        void performLogin(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            out.println(username);
            out.println(password);
        }

        void createChatPanel() {
            loginPanel.setVisible(false);
            chatPanel = new JPanel(new BorderLayout());

            chatArea = new JTextArea(20, 50);
            chatArea.setEditable(false);
            chatArea.setBackground(new Color(230, 230, 250));
            chatArea.setFont(new Font("SansSerif", Font.BOLD, 12));
            JScrollPane scrollPane = new JScrollPane(chatArea);
            chatPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel southPanel = new JPanel(new FlowLayout());
            targetUserField = new JTextField(10);
            southPanel.add(new JLabel("To:"));
            southPanel.add(targetUserField);

            messageField = new JTextField(20);
            southPanel.add(messageField);

            sendButton = new JButton("Send");
            sendButton.addActionListener(this::sendMessage);
            southPanel.add(sendButton);

            fileButton = new JButton("Send File");
            fileButton.addActionListener(this::sendFile);
            southPanel.add(fileButton);

            chatPanel.add(southPanel, BorderLayout.SOUTH);

            frame.add(chatPanel);
            frame.validate();
            frame.repaint();
        }

        void sendMessage(ActionEvent e) {
            String target = targetUserField.getText().trim();
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                out.println("@msg " + target + " " + message);
                messageField.setText("");
            }
        }

        void sendFile(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Files", "txt", "pdf", "jpg", "png", "gif"));
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                out.println("@file " + targetUserField.getText().trim() + " " + file.getAbsolutePath());
            }
        }
    }

    class Rcv extends Thread {
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    final String message = serverMessage;
                    SwingUtilities.invokeLater(() -> {
                        if (message.startsWith("Authenticated")) {
                            g.statusLabel.setText("Login successful.");
                            g.createChatPanel();
                        } else {
                            g.chatArea.append(message + "\n");
                        }
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    g.statusLabel.setText("Connection error.");
                });
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Client");
        new Client(frame);
    }
}
