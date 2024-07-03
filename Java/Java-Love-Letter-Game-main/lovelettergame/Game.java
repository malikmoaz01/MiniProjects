package org.example.lovelettergame;
import org.example.client.Server;

import java.util.*;

public class Game {
    private Deck deck;
    private List<String> players;
    private Map<String, Integer> scores;
    private Map<String, List<Card>> hands;
    private Map<String, Boolean> protectionStatus;  // To handle Handmaid card protection
    private boolean gameStarted;

    public Game() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.scores = new HashMap<>();
        this.hands = new HashMap<>();
        this.protectionStatus = new HashMap<>();
        this.gameStarted = false;
    }

    public void addPlayer(String player) {
        if (!players.contains(player) && players.size() < 4) {
            players.add(player);
            scores.put(player, 0);
            hands.put(player, new ArrayList<>()); // Initialize the player's hand
            protectionStatus.put(player, false);
            broadcastGameState("Player " + player + " has joined the game.");
        }
    }

    public synchronized void startGame() {
        if (players.size() >= 2 && !gameStarted) {
            gameStarted = true;
            deck.shuffle();
            dealInitialCards();
            System.out.println("Game started with players: " + String.join(", ", players));
        }
    }

    private void dealInitialCards() {
        for (String player : players) {
            hands.get(player).add(deck.drawCard());
        }
    }

    public synchronized void playCard(String playerName, Card card, String targetPlayer) throws Exception {
        if (!gameStarted) {
            throw new Exception("Game has not started yet.");
        }
        if (!players.contains(playerName) || !hands.get(playerName).contains(card)) {
            throw new Exception("Invalid card or player.");
        }
        if (protectionStatus.getOrDefault(targetPlayer, false) && !playerName.equals(targetPlayer)) {
            throw new Exception("Target is protected and cannot be targeted.");
        }

        // Apply card effects
        applyCardEffect(playerName, card, targetPlayer);

        hands.get(playerName).remove(card);  // Remove card from player's hand
        deck.discardCard(card);  // Discard the card to the discard pile
        System.out.println(playerName + " played " + card + " targeting " + targetPlayer);
    }

    private void applyCardEffect(String playerName, Card card, String targetPlayer) throws Exception {
        switch (card.getCharacter()) {
            case GUARD:
                if (targetPlayer == null || targetPlayer.isEmpty()) {
                    throw new Exception("Guard requires a target player.");
                } // Example: Guessing the card should be done here with additional input handling
                break;
            case PRIEST:
                System.out.println(playerName + " views " + targetPlayer + "'s hand secretly: " + hands.get(targetPlayer));
                break;
            case BARON:
                compareHands(playerName, targetPlayer);
                break;
            case HANDMAID:
                protectionStatus.put(playerName, true);
                System.out.println(playerName + " is now protected until their next turn.");
                break;
            case PRINCE:
                forceDiscard(targetPlayer);
                break;
            case KING:
                tradeHands(playerName, targetPlayer);
                break;
            case COUNTESS:
                System.out.println("Countess card played. No special action required unless with King or Prince.");
                break;
            case PRINCESS:
                eliminatePlayer(playerName);
                System.out.println(playerName + " has been eliminated for discarding the Princess.");
                break;
        }
    }

    private void compareHands(String playerOne, String playerTwo) throws Exception {
        if (!hands.containsKey(playerOne) || !hands.containsKey(playerTwo)) {
            throw new Exception("One of the players does not exist.");
        }
        int playerOneValue = hands.get(playerOne).get(0).getCharacter().getValue();
        int playerTwoValue = hands.get(playerTwo).get(0).getCharacter().getValue();
        if (playerOneValue > playerTwoValue) {
            eliminatePlayer(playerTwo);
        } else if (playerOneValue < playerTwoValue) {
            eliminatePlayer(playerOne);
        }
    }

    private void forceDiscard(String player) throws Exception {
        if (hands.get(player).isEmpty()) {
            throw new Exception(player + " has no cards to discard.");
        }
        Card discardedCard = hands.get(player).remove(0);
        deck.discardCard(discardedCard);
        System.out.println(player + " was forced to discard " + discardedCard);
        if (!deck.isEmpty()) {
            hands.get(player).add(deck.drawCard());
        }
    }

    private void tradeHands(String playerOne, String playerTwo) throws Exception {
        if (!hands.containsKey(playerOne) || !hands.containsKey(playerTwo)) {
            throw new Exception("Invalid players specified for trade.");
        }
        List<Card> temp = hands.get(playerOne);
        hands.put(playerOne, hands.get(playerTwo));
        hands.put(playerTwo, temp);
        System.out.println(playerOne + " and " + playerTwo + " have traded hands.");
    }

    private void eliminatePlayer(String player) {
        hands.get(player).clear(); 
        players.remove(player); 
        System.out.println(player + " has been eliminated from the round.");
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public synchronized void startNewRound() {
        if (!gameStarted) {
            System.out.println("Cannot start a new round; the game has not yet begun.");
            return;
        }

        deck = new Deck();
        deck.shuffle();
        for (String player : players) {
            hands.get(player).clear();
            hands.get(player).add(deck.drawCard());  // Deal one card to each player
        }
        System.out.println("A new round has started.");
    }

    public void endGame() {
        gameStarted = false;
        String winner = players.stream()
                               .max(Comparator.comparing(scores::get))
                               .orElse(null);

        if (winner != null) {
            System.out.println("The winner is " + winner + " with " + scores.get(winner) + " points.");
        } else {
            System.out.println("Game ended with no clear winner.");
        }
    }

    public boolean isGameOver() {
        return scores.values().stream().anyMatch(score -> score >= 20);
    }

    private void updateScores(String player, int points) {
        if (scores.containsKey(player)) {
            scores.put(player, scores.get(player) + points);
            System.out.println("Updated " + player + "'s score to " + scores.get(player));
        }
    }
    
    public void printAllHands() {
        for (Map.Entry<String, List<Card>> entry : hands.entrySet()) {
            System.out.println("Player " + entry.getKey() + "'s hand: " + entry.getValue());
        }
    }
    public void broadcastGameState(String message) {
        for (String player : players) {
            broadcastToPlayer(player, message);
        }
    }
    public void broadcastToPlayer(String player, String message) {
    // Implement the logic to send the message to the specified player
    // This could involve network communication or some other means of sending messages
    System.out.println("Sending message to " + player + ": " + message);
}



}