package com.dmpkov.domino.model;

import java.util.LinkedList;

public class DominoBoard {
    private LinkedList<DominoTile> chain;

    public DominoBoard() {
        this.chain = new LinkedList<>();
    }

    public void addTile(DominoTile tile, boolean toLeft) {
        if (chain.isEmpty()) {
            chain.add(tile);
        } else if (toLeft) {
            chain.addFirst(tile);
        } else {
            chain.addLast(tile);
        }
    }

    public LinkedList<DominoTile> getChain() {
        return chain;
    }

    public int getLeftValue() {
        return chain.isEmpty() ? -1 : chain.getFirst().getLeft();
    }

    public int getRightValue() {
        return chain.isEmpty() ? -1 : chain.getLast().getRight();
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    public void clear() {
        chain.clear();
    }
}