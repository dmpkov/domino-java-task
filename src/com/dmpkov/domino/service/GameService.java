package com.dmpkov.domino.service;

import com.dmpkov.domino.model.DominoBoard;
import com.dmpkov.domino.model.DominoTile;
import com.dmpkov.domino.model.Player;

import java.util.*;

public class GameService {
    private DominoBoard board;
    private List<DominoTile> deck;
    private List<Player> players;

    private Map<Player, List<DominoTile>> playerHands;

    private int currentPlayerIndex;
    private boolean isGameOver;
    private String statusMessage;

    public GameService() {
        this.board = new DominoBoard();
        this.players = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.playerHands = new HashMap<>();
    }

    public void startNewGame(int numberOfPlayers) {
        board.clear();
        players.clear();
        playerHands.clear();
        deck = generateDeck();
        isGameOver = false;
        statusMessage = "Игра началась! Ваш ход.";

        for (int i = 0; i < numberOfPlayers; i++) {
            boolean isBot = (i > 0);
            Player newPlayer = new Player(isBot ? "Бот " + i : "Игрок", isBot);
            players.add(newPlayer);
            playerHands.put(newPlayer, new ArrayList<>());
        }

        for (int k = 0; k < 7; k++) {
            for (Player p : players) {
                if (!deck.isEmpty()) {
                    playerHands.get(p).add(deck.remove(0));
                }
            }
        }
        currentPlayerIndex = 0;
    }

    public void handleDraw(Player player) {
        if (hasAnyMove(player)) {
            statusMessage = "У вас есть подходящая карта! Ходите.";
            return;
        }

        boolean foundPlayable = false;

        while (!deck.isEmpty()) {
            DominoTile t = deck.remove(0);
            giveTileToPlayer(player, t);

            if (canPlaceTile(t)) {
                statusMessage = player.getName() + " вытянул " + t + ". Теперь можно ходить.";
                foundPlayable = true;
                break;
            }
        }

        if (!foundPlayable && deck.isEmpty()) {
            statusMessage = player.getName() + " не нашел ход и ПАСУЕТ (базар пуст).";
            nextTurn();
        }
    }


    public boolean humanTurn(DominoTile tile, boolean toLeft) {
        Player p = getCurrentPlayer();
        if (p.isBot()) return false;

        if (!getPlayerHand(p).contains(tile)) return false;

        if (tryToPlace(tile, toLeft)) {
            removeTileFromPlayer(p, tile);
            checkWinCondition();
            if (!isGameOver) nextTurn();
            return true;
        }
        return false;
    }

    public void botTurn() {
        Player p = getCurrentPlayer();
        if (!p.isBot() || isGameOver) return;

        statusMessage = "Ходит " + p.getName() + "...";

        if (tryBotMove(p)) {
            checkWinCondition();
            if (!isGameOver) nextTurn();
            return;
        }

        handleDraw(p);

        if (getCurrentPlayer() == p && !isGameOver) {
            if (tryBotMove(p)) {
                checkWinCondition();
                if (!isGameOver) nextTurn();
            } else {
                nextTurn();
            }
        }
    }

    private boolean tryBotMove(Player p) {
        List<DominoTile> hand = new ArrayList<>(getPlayerHand(p));
        for (DominoTile tile : hand) {
            if (tryToPlace(tile, true)) {
                removeTileFromPlayer(p, tile);
                return true;
            }
            if (tryToPlace(tile, false)) {
                removeTileFromPlayer(p, tile);
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceTile(DominoTile tile) {
        if (board.isEmpty()) return true;
        int l = board.getLeftValue();
        int r = board.getRightValue();
        return tile.getLeft() == l || tile.getRight() == l || tile.getLeft() == r || tile.getRight() == r;
    }

    private boolean hasAnyMove(Player p) {
        List<DominoTile> hand = getPlayerHand(p);
        if (hand == null) return false;
        for (DominoTile t : hand) {
            if (canPlaceTile(t)) return true;
        }
        return false;
    }

    private boolean tryToPlace(DominoTile tile, boolean toLeft) {
        if (board.isEmpty()) {
            board.addTile(tile, true);
            return true;
        }
        int target = toLeft ? board.getLeftValue() : board.getRightValue();
        if (toLeft) {
            if (tile.getRight() == target) {
                board.addTile(tile, true);
                return true;
            } else if (tile.getLeft() == target) {
                tile.flip();
                board.addTile(tile, true);
                return true;
            }
        } else {
            if (tile.getLeft() == target) {
                board.addTile(tile, false);
                return true;
            } else if (tile.getRight() == target) {
                tile.flip();
                board.addTile(tile, false);
                return true;
            }
        }
        return false;
    }

    private void checkWinCondition() {
        Player current = getCurrentPlayer();
        if (getPlayerHand(current).isEmpty()) {
            isGameOver = true;
            statusMessage = "ПОБЕДА! " + current.getName() + " выиграл!";
        }

    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        statusMessage = "Ход: " + getCurrentPlayer().getName();
    }

    private List<DominoTile> generateDeck() {
        List<DominoTile> newDeck = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                newDeck.add(new DominoTile(i, j));
            }
        }
        Collections.shuffle(newDeck);
        return newDeck;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public List<Player> getPlayers() {
        return players;
    }
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public DominoBoard getBoard() { return board; }
    public String getStatusMessage() { return statusMessage; }
    public boolean isGameOver() { return isGameOver; }

    public List<DominoTile> getPlayerHand(Player p) {
        return playerHands.get(p);
    }

    private void removeTileFromPlayer(Player p, DominoTile tile) {
        playerHands.get(p).remove(tile);
    }

    private void giveTileToPlayer(Player p, DominoTile tile) {
        playerHands.get(p).add(tile);
    }
}