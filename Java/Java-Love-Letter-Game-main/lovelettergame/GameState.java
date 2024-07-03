package org.example.lovelettergame;

import java.util.*;

public class GameState {
    public enum Stage {
        WAITING_FOR_PLAYERS, // Before game starts, waiting for enough players to join
        IN_PROGRESS,         // Game is currently being played
        COMPLETED            // Game has ended, scores are finalized
    }
    
    private final int NUM_SECRET_CARDS = 1; 
    private final Map<String, List<Card>> playerSecretCardsMap = new HashMap<>();
    private final Set<String> playersAwaitingSecretCards = new HashSet<>();

    private Stage currentStage;
    private final List<String> players;
    private final Map<String, Integer> scores;
    private final Map<String, List<Card>> playerHands;
    private final List<Card> discardPile;
    private int currentPlayerIndex; // Index of the current player
    private Deck deck;
    private final Map<String, Boolean> protectionStatus;

    public GameState() {
        this.currentStage = Stage.WAITING_FOR_PLAYERS;
        this.players = new ArrayList<>();
        this.scores = new HashMap<>();
        this.playerHands = new HashMap<>();
        this.discardPile = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.deck = new Deck();
        this.protectionStatus = new HashMap<>();
    }

    public void addPlayer(String player) {
        if (!players.contains(player) && players.size() < 4) {
            players.add(player);
            scores.put(player, 0);
            playerHands.put(player, new ArrayList<>());
            protectionStatus.put(player, false);
            broadcastGameState("Player " + player + " has joined the game.");
        }
    }

    public void startGame() {
        if (players.size() >= 2 && currentStage == Stage.WAITING_FOR_PLAYERS) {
            currentStage = Stage.IN_PROGRESS;
            deck.shuffle();
            dealInitialCards();
            broadcastGameState("Game started with players: " + String.join(", ", players));
            playRound();
        }
    }

    private void dealInitialCards() {
        for (String player : players) {
            List<Card> hand = playerHands.get(player);
            hand.add(deck.drawCard());
            hand.add(deck.drawCard());
            broadcastToPlayer(player, "Your cards: " + hand);
        }
    }

    private void playRound() {
        if (currentStage != Stage.IN_PROGRESS) return;

        String currentPlayer = players.get(currentPlayerIndex);
        broadcastGameState("It's " + currentPlayer + "'s turn to play.");
        // Players take their actions on their client side
    }

    public void playCard(String playerName, String cardName, String targetName, int cardGuess) {
        if (!playerName.equals(players.get(currentPlayerIndex))) {
            broadcastToPlayer(playerName, "It's not your turn!");
            return;
        }
        
        Card cardPlayed = findCardByName(playerName, cardName);
        if (cardPlayed == null) {
            broadcastToPlayer(playerName, "Card not found or already played!");
            return;
        }
        
        applyCardEffect(playerName, cardPlayed, targetName, cardGuess);
        nextTurn();
    }

    private Card findCardByName(String playerName, String cardName) {
        return playerHands.get(playerName).stream()
            .filter(card -> card.getCharacter().name().equalsIgnoreCase(cardName))
            .findFirst()
            .orElse(null);
    }

    private void applyCardEffect(String playerName, Card card, String targetName, int cardGuess) 
    {
            // Reset protection before action if needed
            if (card.getCharacter() != Card.Character.HANDMAID) {
                protectionStatus.put(playerName, false);
            }
            
            switch (card.getCharacter()) {
                case GUARD:
                    if (!protectionStatus.getOrDefault(targetName, false)) {
                        guessPlayerCard(playerName, targetName, cardGuess);
                    } else {
                        broadcastGameState(targetName + " is protected and cannot be targeted.");
                    }
                    break;
                case PRIEST:
                    if (!protectionStatus.getOrDefault(targetName, false)) {
                        broadcastToPlayer(playerName, targetName + "'s hand: " + playerHands.get(targetName));
                    } else {
                        broadcastGameState(targetName + " is protected and cannot be targeted.");
                    }
                    break;
                case BARON:
                    if (!protectionStatus.getOrDefault(targetName, false)) {
                        compareHands(playerName, targetName);
                    } else {
                        broadcastGameState(targetName + " is protected and cannot be targeted.");
                    }
                    break;
                case HANDMAID:
                    protectionStatus.put(playerName, true);
                    broadcastToPlayer(playerName, "You are protected until your next turn.");
                    break;
                case PRINCE:
                    if (!protectionStatus.getOrDefault(targetName, false)) {
                        forceDiscardAndRedraw(targetName);
                    } else {
                        broadcastGameState(targetName + " is protected and cannot be targeted.");
                    }
                    break;
                case KING:
                    if (!protectionStatus.getOrDefault(targetName, false)) {
                        tradeHands(playerName, targetName);
                    } else {
                        broadcastGameState(targetName + " is protected and cannot be targeted.");
                    }
                    break;
                case COUNTESS:
                    // The Countess' play might be forced due to holding the King or Prince, but has no immediate effect on play.
                    broadcastToPlayer(playerName, "Countess played. No immediate effect.");
                    break;
                case PRINCESS:
                    eliminatePlayer(playerName);
                    broadcastGameState(playerName + " has been eliminated for discarding the Princess.");
                    break;
            }
            discardCard(playerName, card);
        }

        private void forceDiscardAndRedraw(String playerName) {
            if (!playerHands.get(playerName).isEmpty()) {
                Card discardedCard = playerHands.get(playerName).remove(0);
                discardPile.add(discardedCard);
                broadcastGameState(playerName + " discards " + discardedCard + " and draws a new card.");
                if (!deck.isEmpty()) {
                    playerHands.get(playerName).add(deck.drawCard());
                }
            }
        }

        private void tradeHands(String playerOne, String playerTwo) {
            List<Card> temp = playerHands.get(playerOne);
            playerHands.put(playerOne, playerHands.get(playerTwo));
            playerHands.put(playerTwo, temp);
            broadcastGameState(playerOne + " and " + playerTwo + " have traded hands.");
        }

        private void guessPlayerCard(String playerName, String targetName, int cardGuess) {
            boolean guessedCorrectly = playerHands.get(targetName).stream()
                .anyMatch(card -> card.getCharacter().getValue() == cardGuess);
            if (guessedCorrectly) {
                broadcastGameState(playerName + " guessed correctly! " + targetName + " is out of the round.");
                playerHands.get(targetName).clear(); // Eliminate the player
            } else {
                broadcastGameState(playerName + " guessed wrong.");
            }
        }




    private void compareHands(String playerOne, String playerTwo) {
        int valueOne = playerHands.get(playerOne).get(0).getCharacter().getValue();
        int valueTwo = playerHands.get(playerTwo).get(0).getCharacter().getValue();
        if (valueOne > valueTwo) {
            eliminatePlayer(playerTwo);
        } else if (valueOne < valueTwo) {
            eliminatePlayer(playerOne);
        } else {
            broadcastGameState("It's a tie between " + playerOne + " and " + playerTwo);
        }
    }

    private void eliminatePlayer(String playerName) {
        playerHands.get(playerName).clear(); // Clear the player's hand
        broadcastGameState(playerName + " has been eliminated from the game.");
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) { // Start a new round or evaluate the game ending
            evaluateRound();
        } else {
            playRound();
        }
    }

    private void evaluateRound() {
        // Example logic to determine round winner, update scores, etc.
        // Placeholder for actual evaluation logic
        broadcastGameState("Evaluating round...");
        endGame();  // Assuming end game condition is met for demonstration
    }

    public void endGame() {
        currentStage = Stage.COMPLETED;
        broadcastGameState("Game has ended. Final scores: " + scores);
    }

    private void broadcastGameState(String message) {
        // Example of broadcasting game state updates to all connected clients
        System.out.println(message);  // Placeholder for actual network broadcast
    }

    private void broadcastToPlayer(String player, String message) {
        // Send messages to specific players; to be integrated with network capabilities
        System.out.println("To " + player + ": " + message);  // Placeholder for direct messaging
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public List<String> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Map<String, Integer> getScores() {
        return Collections.unmodifiableMap(scores);
    }

    public Map<String, List<Card>> getPlayerHands() {
        return Collections.unmodifiableMap(playerHands);
    }

    public List<Card> getDiscardPile() {
        return Collections.unmodifiableList(discardPile);
    }

    private void discardCard(String player, Card card) {
        playerHands.get(player).remove(card);
        discardPile.add(card);
    }

    public boolean isInProgress() {
    return currentStage == Stage.IN_PROGRESS;
    }



    private void assignSecretCards() {
        List<Card> deck = new ArrayList<>(); // Assuming you have a deck of cards
        // Shuffle the deck before distributing cards
        Collections.shuffle(deck);

        for (String player : players) {
            List<Card> playerSecretCards = new ArrayList<>();
            for (int i = 0; i < NUM_SECRET_CARDS; i++) {
                if (!deck.isEmpty()) {
                    Card secretCard = deck.remove(0); // Remove the top card from the deck
                    playerSecretCards.add(secretCard); // Add the card to the player's secret cards
                }else {
                System.out.println("Deck is empty, cannot assign more cards.");  
                break;
            }
            }
            playerSecretCardsMap.put(player, playerSecretCards); 
            System.out.println("Secret cards for " + player + ": " + playerSecretCards);
        }
    }

    // Method to retrieve secret cards for a specific player
    public List<Card> getSecretCardsForPlayer(String playerName) {
        return playerSecretCardsMap.getOrDefault(playerName, Collections.emptyList());
    }

    // Method to mark that a player has received their secret cards
    public void markPlayerReceivedSecretCards(String playerName) {
        playersAwaitingSecretCards.remove(playerName);
    }

    public boolean hasReceivedSecretCards(String playerName) {
    List<Card> secretCards = playerSecretCardsMap.get(playerName);
    return secretCards != null && !secretCards.isEmpty();
    }


}
