package org.example.client;

import org.example.lovelettergame.Card;
import org.example.lovelettergame.GameState;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private static GameState gameInstance;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.clientUsername = bufferedReader.readLine().trim();
            while (isUsernameTaken(this.clientUsername)) {
                bufferedWriter.write("Username already taken, please choose again:");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                this.clientUsername = bufferedReader.readLine().trim();
            }

            clientHandlers.add(this);
            bufferedWriter.write("Username accepted! Welcome to Love Letter, " + this.clientUsername + "!");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println(clientUsername + " has entered the game!");
            broadcastMessage("Server Notification: " + clientUsername + " has entered the game!");

            synchronized (ClientHandler.class) {
                if (gameInstance == null) {
                    gameInstance = new GameState();
                }
                gameInstance.addPlayer(clientUsername);
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        try {
            while (socket.isConnected() && (messageFromClient = bufferedReader.readLine()) != null) {
                if ("bye".equalsIgnoreCase(messageFromClient.trim())) {
                    broadcastMessage(clientUsername + " left the room");
                    closeEverything(socket, bufferedWriter, bufferedReader);
                    break;
                } else if (messageFromClient.startsWith("/")) {
                    handleGameCommand(messageFromClient.trim());
                } else if (messageFromClient.startsWith("@")) {
                    sendDirectMessage(messageFromClient);
                } else {
                    broadcastMessage(clientUsername + ": " + messageFromClient);
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    private void handleGameCommand(String command) throws IOException {
        String[] parts = command.split(" ");
        switch (parts[0].toLowerCase()) {
            case "/playcard":
                handlePlayCardCommand(parts);
                break;
            case "/creategame":
                if (gameInstance == null) {
                    gameInstance = new GameState();
                    gameInstance.addPlayer(clientUsername);
                    bufferedWriter.write("New game created and you joined it.");
                } else {
                    bufferedWriter.write("Game already exists, join it instead.");
                }
                bufferedWriter.newLine();
                bufferedWriter.flush();
                break;
            case "/joingame":
                if (gameInstance != null && gameInstance.getCurrentStage() == GameState.Stage.WAITING_FOR_PLAYERS) {
                    gameInstance.addPlayer(clientUsername);
                    bufferedWriter.write("You joined the game.");
                } else {
                    bufferedWriter.write("No game available to join or game already started.");
                }
                bufferedWriter.newLine();
                bufferedWriter.flush();
                break;
            case "/startgame":
                if (gameInstance != null && gameInstance.getPlayers().contains(clientUsername)) {
                    gameInstance.startGame();
                    broadcastMessage("Game started by " + clientUsername);
                } else {
                    bufferedWriter.write("Cannot start the game.");
                }
                bufferedWriter.newLine();
                bufferedWriter.flush();
                break;
            case "/score":
                if (gameInstance != null) {
                    bufferedWriter.write("Current scores: " + gameInstance.getScores().toString());
                } else {
                    bufferedWriter.write("No game in progress.");
                }
                bufferedWriter.newLine();
                bufferedWriter.flush();
                break;
            case "/secretcardsend":
                sendSecretCards();
                break;
            default:
                bufferedWriter.write("Unknown command.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
        }
    }

    private void sendSecretCards() throws IOException {
        if (gameInstance == null || !gameInstance.isInProgress() || !gameInstance.getPlayers().contains(clientUsername)) {
            bufferedWriter.write("Error: You cannot receive secret cards at this moment.");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            return;
        }

        List<Card> secretCards = gameInstance.getSecretCardsForPlayer(clientUsername);
        if (secretCards == null || secretCards.isEmpty()) {
            bufferedWriter.write("You have no secret cards.");
        } else {
            String secretCardsMessage = "Your secret cards: " + secretCards.toString();
            bufferedWriter.write(secretCardsMessage);
        }
        bufferedWriter.newLine();
        bufferedWriter.flush();

        // This assumes marking is done elsewhere or removed for simplicity
    }
    
    private void handlePlayCardCommand(String[] parts) throws IOException {
        if (parts.length < 4) {  // Ensure you have enough parts to include the guess
            bufferedWriter.write("Usage: /playcard [card] [targetPlayer] [guess]");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            return;
        }
        String card = parts[1];
        String target = parts[2];
        int guess;
        try {
            guess = Integer.parseInt(parts[3]);  // Parse the guess as integer
        } catch (NumberFormatException e) {
            bufferedWriter.write("Invalid number format for guess.");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            return;
        }

        try {
            gameInstance.playCard(clientUsername, card, target, guess);
            broadcastMessage(clientUsername + " played " + card + " targeting " + target);
        } catch (Exception e) {
            bufferedWriter.write("Error: " + e.getMessage());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }

    public void sendDirectMessage(String message) throws IOException {
        String[] messageParts = message.trim().split(" ", 2);
        if (messageParts.length > 1) {
            String targetUsername = messageParts[0].substring(1);
            String directMessage = messageParts[1];
            boolean found = false;
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.clientUsername.equalsIgnoreCase(targetUsername)) {
                    clientHandler.bufferedWriter.write("Direct message from " + this.clientUsername + ": " + directMessage);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                    found = true;
                    break;
                }
            }
            if (!found) {
                bufferedWriter.write("User " + targetUsername + " not found.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (!clientHandler.socket.isClosed() && !clientHandler.clientUsername.equals(this.clientUsername)) {
                try {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(clientHandler.socket, clientHandler.bufferedWriter, clientHandler.bufferedReader);
                }
            }
        }
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("Server Notification: " + clientUsername + " has left the game!");
    }

    private boolean isUsernameTaken(String username) {
        return clientHandlers.stream().anyMatch(handler -> handler.clientUsername.equalsIgnoreCase(username));
    }
}
