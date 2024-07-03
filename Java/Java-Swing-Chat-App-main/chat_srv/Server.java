package chat_srv;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final String ADMIN_USERNAME = "user1";
    private static final String ADMIN_PASSWORD = "pass1";

    private static Set<String> loggedInUsers = new HashSet<>();
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // Authentication
                writer.println("Enter Name:");
                String name = reader.readLine().trim();
                if (!name.equals(ADMIN_USERNAME)) {
                    writer.println("Invalid username. Connection closed.");
                    socket.close();
                    return;
                }

                writer.println("Enter Password:");
                String password = reader.readLine().trim();
                if (!password.equals(ADMIN_PASSWORD)) {
                    writer.println("Invalid password. Connection closed.");
                    socket.close();
                    return;
                }

                writer.println("Login successful.");

                // Main chat loop
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("quit")) {
                        break;
                    } else if (message.startsWith("DM:")) {
                        handleDirectMessage(message);
                    } else if (message.startsWith("all:")) {
                        broadcastMessage(message.substring(4));
                    } else {
                        broadcastMessage(name + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e);
            } finally {
                if (username != null) {
                    loggedInUsers.remove(username);
                    clients.remove(username);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleDirectMessage(String message) {
            String[] parts = message.split(":", 3);
            String recipient = parts[1].trim();
            String msg = "Direct Message from " + username + ": " + parts[2].trim();
            if (clients.containsKey(recipient)) {
                clients.get(recipient).println(msg);
            } else {
                writer.println("User not found or offline.");
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (PrintWriter client : clients.values()) {
                    client.println(message);
                }
            }
        }
    }
}
