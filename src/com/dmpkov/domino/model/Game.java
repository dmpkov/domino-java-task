package com.dmpkov.domino.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Game {
    private List<DominoTile> deck;
    private DominoBoard board;
    private Player human;
    private Player bot;
    private Scanner scanner;

    public Game() {
        board = new DominoBoard();
        deck = generateDeck();
        scanner = new Scanner(System.in);
        human = new Player("Human", true);
        bot = new Player("Computer", false);
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

    public void start() {
        for (int i = 0; i < 7; i++) {
            human.takeTile(deck.remove(0));
            bot.takeTile(deck.remove(0));
        }

        boolean humanTurn = true;
        boolean running = true;
        int passCount = 0;

        while (running) {
            Player current = humanTurn ? human : bot;

            boolean moved = current.makeMove(board, scanner);

            if (!moved) {
                if (!deck.isEmpty()) {
                    DominoTile t = deck.remove(0);
                    current.takeTile(t);
                    System.out.println(current.getName() + " drew a tile from the boneyard.");
                    if (current.makeMove(board, scanner)) {
                        passCount = 0;
                    } else {
                        System.out.println(current.getName() + " still cannot move. Pass.");
                        passCount++;
                    }
                } else {
                    System.out.println(current.getName() + " passes (deck empty).");
                    passCount++;
                }
            } else {
                passCount = 0;
            }

            if (!current.hasTiles()) {
                System.out.println("\nGAME OVER! " + current.getName() + " WINS!");
                System.out.println("Final Board: " + board);
                running = false;
            } else if (passCount >= 2) {
                System.out.println("\nGAME OVER! No one can move.");
                running = false;
            }

            humanTurn = !humanTurn;
        }
        scanner.close();
    }


}