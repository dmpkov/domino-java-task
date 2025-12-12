package com.dmpkov.domino.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private DominoBoard board;
    private List<DominoTile> deck;
    private List<Player> players;
    private Map<Player, List<DominoTile>> playerHands;

    private int currentPlayerIndex;
    private boolean isGameOver;
    private String statusMessage;

    public Game() {
        this.board = new DominoBoard();
        this.deck = new ArrayList<>();
        this.players = new ArrayList<>();
        this.playerHands = new HashMap<>();
        this.currentPlayerIndex = 0;
        this.isGameOver = false;
        this.statusMessage = "Подготовка к игре...";
    }


    public DominoBoard getBoard() { return board; }

    public List<DominoTile> getDeck() { return deck; }
    public void setDeck(List<DominoTile> deck) { this.deck = deck; }

    public List<Player> getPlayers() { return players; }

    public Map<Player, List<DominoTile>> getPlayerHands() { return playerHands; }

    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int index) { this.currentPlayerIndex = index; }

    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public void reset() {
        board.clear();
        players.clear();
        playerHands.clear();
        deck.clear();
        isGameOver = false;
        currentPlayerIndex = 0;
        statusMessage = "Новая игра";
    }
}