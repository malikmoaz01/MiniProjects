package org.example.client;

import org.example.lovelettergame.Game;
import org.example.lovelettergame.Card;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final ServerSocket serverSocket;
    private final Map<String, ClientHandler> clientHandlers = new HashMap<>();
    private Game gameInstance;

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void startServer() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                if (clientHandlers.size() >= MAX_PLAYERS) {
                    System.out.println("Maximum players reached. Cannot accept more connections.");
                    socket.close();
                    continue;
                }
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        } finally {
            closeServerSocket();
        }
    }

    public synchronized Game getGameInstance() {
        if (gameInstance == null) {
            gameInstance = new Game();
            System.out.println("New game created.");
        }
        return gameInstance;
    }

    public synchronized void registerClientHandler(String username, ClientHandler handler) {
        clientHandlers.put(username, handler);
        System.out.println(username + " has joined the game!");
        broadcastMessage("Server Notification: " + username + " has joined the game!");
    }

    public synchronized void removeClientHandler(String username) {
        clientHandlers.remove(username);
        broadcastMessage("Server Notification: " + username + " has left the game!");
    }

    public void broadcastMessage(String message) {
        for (ClientHandler handler : clientHandlers.values()) {
            try {
                handler.sendDirectMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean canStartGame() {
        return clientHandlers.size() >= MIN_PLAYERS && clientHandlers.size() <= MAX_PLAYERS;
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(1212);
            server.startServer();
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }
}

