package org.example.lovelettergame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> drawPile;
    private List<Card> discardPile;

    public Deck() {
        drawPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
        for (Card.Character character : Card.Character.values()) {
            for (int i = 0; i < character.getCount(); i++) {
                drawPile.add(new Card(character));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(drawPile);
    }

    public Card drawCard() {
        if (!drawPile.isEmpty()) {
            return drawPile.remove(0);
        }
        return null;  // Consider throwing an exception or other error handling if needed
    }

    public void discardCard(Card card) {
        discardPile.add(card);
    }

    public List<Card> getDrawPile() {
        return drawPile;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public boolean isEmpty() {
        return drawPile.isEmpty();
    }
}
