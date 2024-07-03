package org.example.lovelettergame;

public class Card {
    public enum Character {
        GUARD(1, 5, "Guess a player's hand and name a card (other than Guard). If correct, that player is out of the round."),
        PRIEST(2, 2, "Look at another player's hand secretly."),
        BARON(3, 2, "Compare hands with another player; lower hand is out."),
        HANDMAID(4, 2, "Immunity from effects until your next turn."),
        PRINCE(5, 2, "Choose a player to discard their hand and draw a new one."),
        KING(6, 1, "Trade hands with another player."),
        COUNTESS(7, 1, "Must be discarded if caught with King or Prince."),
        PRINCESS(8, 1, "If discarded, you are out of the game.");

        private final int value;
        private final int count;
        private final String effect;

        Character(int value, int count, String effect) {
            this.value = value;
            this.count = count;
            this.effect = effect;
        }

        public int getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }

        public String getEffect() {
            return effect;
        }
    }

    private final Character character;

    public Card(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return character.name() + " (" + character.getValue() + "): " + character.getEffect();
    }
}