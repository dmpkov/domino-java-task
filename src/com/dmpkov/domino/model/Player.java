package com.dmpkov.domino.model;

public class Player {
    private String name;
    private boolean isBot;

    public Player(String name, boolean isBot) {
        this.name = name;
        this.isBot = isBot;
    }

    public String getName() { return name; }
    public boolean isBot() { return isBot; }

    @Override
    public String toString() { return name; }

}