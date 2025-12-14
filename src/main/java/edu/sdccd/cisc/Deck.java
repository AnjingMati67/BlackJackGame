package edu.sdccd.cisc;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private int index = 0;

    public Deck() { initializeDeck(); }

    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2","3","4","5","6","7","8","9","10","Jack","Queen","King","Ace"};
        int[] values = {2,3,4,5,6,7,8,9,10,10,10,10,11};
        cards.clear();
        for (String suit : suits)
            for (int i = 0; i < ranks.length; i++)
                cards.add(new Card(suit, ranks[i], values[i]));
        shuffle();
    }

    public void shuffle() { Collections.shuffle(cards); index = 0; }

    public Card dealCard() {
        if (index >= cards.size()) shuffle();
        return cards.get(index++);
    }
}