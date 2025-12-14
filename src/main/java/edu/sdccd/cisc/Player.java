package edu.sdccd.cisc;

public class Player extends Participant {
    private final String name;
    private int chips;

    public Player(String name, int chips) { this.name = name; this.chips = chips; }

    public String getName() { return name; }
    public int getChips() { return chips; }
    public void adjustChips(int amount) { chips += amount; }
}