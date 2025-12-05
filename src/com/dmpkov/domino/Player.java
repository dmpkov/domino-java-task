package com.dmpkov.domino;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
    private String name;
    private List<DominoTile> hand;
    private boolean isHuman;

    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.hand = new ArrayList<>();
    }

    public void takeTile(DominoTile tile) {
        hand.add(tile);
    }

    public boolean hasTiles() {
        return !hand.isEmpty();
    }

    public String getName() { return name; }

    public boolean makeMove(DominoBoard board, Scanner scanner) {
        if (isHuman) {
            return makeHumanMove(board, scanner);
        } else {
            return makeBotMove(board);
        }
    }

    private boolean makeHumanMove(DominoBoard board, Scanner scanner) {
        System.out.println("\n=== YOUR TURN (" + name + ") ===");
        System.out.println("Board: " + board);
        System.out.println("Your Hand:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ": " + hand.get(i));
        }

        if (!canMove(board)) {
            System.out.println("No matching tiles! You need to draw.");
            return false;
        }

        while (true) {
            System.out.print("Enter tile number to play (1-" + hand.size() + "): ");
            int index;
            try {
                index = Integer.parseInt(scanner.nextLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            if (index < 0 || index >= hand.size()) {
                System.out.println("Invalid number.");
                continue;
            }

            DominoTile tile = hand.get(index);


            if (board.isEmpty()) {
                board.addTile(tile, true);
                hand.remove(index);
                return true;
            }

            int leftVal = board.getLeftValue();
            int rightVal = board.getRightValue();
            boolean fitsLeft = (tile.getRight() == leftVal || tile.getLeft() == leftVal);
            boolean fitsRight = (tile.getLeft() == rightVal || tile.getRight() == rightVal);

            if (!fitsLeft && !fitsRight) {
                System.out.println("That tile doesn't fit anywhere!");
                continue;
            }


            if (fitsLeft && fitsRight && leftVal != rightVal) {
                System.out.print("Play to (L)eft or (R)ight? (l/r): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if (choice.equals("l")) {
                    processMove(board, tile, true);
                    hand.remove(index);
                    return true;
                } else {
                    processMove(board, tile, false);
                    hand.remove(index);
                    return true;
                }
            }


            if (fitsLeft) {
                processMove(board, tile, true);
                hand.remove(index);
                return true;
            }

            if (fitsRight) {
                processMove(board, tile, false);
                hand.remove(index);
                return true;
            }
        }
    }


    private void processMove(DominoBoard board, DominoTile tile, boolean toLeft) {
        int target = toLeft ? board.getLeftValue() : board.getRightValue();

        if (toLeft) {
            if (tile.getRight() != target) tile.flip();
        } else {
            if (tile.getLeft() != target) tile.flip();
        }
        board.addTile(tile, toLeft);
        System.out.println("You played " + tile);
    }

    private boolean canMove(DominoBoard board) {
        if (board.isEmpty()) return true;
        int l = board.getLeftValue();
        int r = board.getRightValue();
        for (DominoTile t : hand) {
            if (t.getLeft() == l || t.getRight() == l || t.getLeft() == r || t.getRight() == r) return true;
        }
        return false;
    }


    private boolean makeBotMove(DominoBoard board) {
        System.out.println("\nBot " + name + " is thinking...");
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        if (board.isEmpty()) {
            DominoTile tile = hand.remove(0);
            board.addTile(tile, true);
            System.out.println("Bot played " + tile);
            return true;
        }

        int l = board.getLeftValue();
        int r = board.getRightValue();

        for (int i = 0; i < hand.size(); i++) {
            DominoTile tile = hand.get(i);

            if (tile.getRight() == l) {
                board.addTile(tile, true);
                hand.remove(i);
                System.out.println("Bot played " + tile + " (Left)");
                return true;
            } else if (tile.getLeft() == l) {
                tile.flip();
                board.addTile(tile, true);
                hand.remove(i);
                System.out.println("Bot played " + tile + " (Left)");
                return true;
            } else if (tile.getLeft() == r) {
                board.addTile(tile, false);
                hand.remove(i);
                System.out.println("Bot played " + tile + " (Right)");
                return true;
            } else if (tile.getRight() == r) {
                tile.flip();
                board.addTile(tile, false);
                hand.remove(i);
                System.out.println("Bot played " + tile + " (Right)");
                return true;
            }
        }
        System.out.println("Bot has no moves.");
        return false;
    }
}