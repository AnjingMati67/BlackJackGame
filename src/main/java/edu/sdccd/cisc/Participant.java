package edu.sdccd.cisc;

import java.util.ArrayList;
import java.util.List;

public abstract class Participant {
    protected List<Card> hand = new ArrayList<>();

    public void addCard(Card c) { hand.add(c); }
    public void clearHand() { hand.clear(); }
    public List<Card> getHand() { return hand; }

    public int handValue() {
        int sum = 0, aces = 0;
        for (Card c : hand) {
            sum += c.getValue();
            if (c.toString().startsWith("Ace")) aces++;
        }
        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }
        return sum;
    }
}