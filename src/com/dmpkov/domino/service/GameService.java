package com.dmpkov.domino.service;

import com.dmpkov.domino.model.DominoBoard;
import com.dmpkov.domino.model.DominoTile;
import com.dmpkov.domino.model.Game;
import com.dmpkov.domino.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameService {

    private Game game;

    public GameService() {
        this.game = new Game();
    }

    public void startNewGame(int numberOfPlayers) {
        game.reset();

        game.setDeck(generateDeck());
        game.setStatusMessage("Игра началась! Ваш ход.");

        for (int i = 0; i < numberOfPlayers; i++) {
            boolean isBot = (i > 0);
            Player newPlayer = new Player(isBot ? "Бот " + i : "Игрок", isBot);
            game.getPlayers().add(newPlayer);
            game.getPlayerHands().put(newPlayer, new ArrayList<>());
        }

        for (int k = 0; k < 7; k++) {
            for (Player p : game.getPlayers()) {
                if (!game.getDeck().isEmpty()) {
                    game.getPlayerHands().get(p).add(game.getDeck().remove(0));
                }
            }
        }
    }


    public DominoBoard getBoard() { return game.getBoard(); }
    public String getStatusMessage() { return game.getStatusMessage(); }
    public boolean isGameOver() { return game.isGameOver(); }
    public int getDeckSize() { return game.getDeck().size(); }
    public List<Player> getPlayers() { return game.getPlayers(); }

    public Player getCurrentPlayer() {
        if (game.getPlayers().isEmpty()) return null;
        return game.getPlayers().get(game.getCurrentPlayerIndex());
    }

    public List<DominoTile> getPlayerHand(Player p) {
        return game.getPlayerHands().get(p);
    }

    public void handleDraw(Player player) {
        if (hasAnyMove(player)) {
            game.setStatusMessage("У вас есть подходящая карта! Ходите.");
            return;
        }

        boolean foundPlayable = false;

        while (!game.getDeck().isEmpty()) {
            DominoTile t = game.getDeck().remove(0);
            giveTileToPlayer(player, t);

            if (canPlaceTile(t)) {
                game.setStatusMessage(player.getName() + " вытянул " + t + ". Теперь можно ходить.");
                foundPlayable = true;
                break;
            }
        }

        if (!foundPlayable && game.getDeck().isEmpty()) {
            game.setStatusMessage(player.getName() + " не нашел ход и ПАСУЕТ (базар пуст).");
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
            if (!game.isGameOver()) nextTurn();
            return true;
        }
        return false;
    }

    public void botTurn() {
        Player p = getCurrentPlayer();
        if (!p.isBot() || game.isGameOver()) return;

        game.setStatusMessage("Ходит " + p.getName() + "...");

        if (tryBotMove(p)) {
            checkWinCondition();
            if (!game.isGameOver()) nextTurn();
            return;
        }

        handleDraw(p);

        if (getCurrentPlayer() == p && !game.isGameOver()) {
            if (tryBotMove(p)) {
                checkWinCondition();
                if (!game.isGameOver()) nextTurn();
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
        if (game.getBoard().isEmpty()) return true;
        int l = game.getBoard().getLeftValue();
        int r = game.getBoard().getRightValue();
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
        DominoBoard board = game.getBoard();

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
            game.setGameOver(true);
            game.setStatusMessage("ПОБЕДА! " + current.getName() + " выиграл!");
        }
    }

    private void nextTurn() {
        int nextIndex = (game.getCurrentPlayerIndex() + 1) % game.getPlayers().size();
        game.setCurrentPlayerIndex(nextIndex);
        game.setStatusMessage("Ход: " + getCurrentPlayer().getName());
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

    private void removeTileFromPlayer(Player p, DominoTile tile) {
        game.getPlayerHands().get(p).remove(tile);
    }

    private void giveTileToPlayer(Player p, DominoTile tile) {
        game.getPlayerHands().get(p).add(tile);
    }
}