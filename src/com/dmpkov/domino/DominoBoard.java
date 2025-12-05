package com.dmpkov.domino;

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

    public int getLeftValue() {
        return chain.isEmpty() ? -1 : chain.getFirst().getLeft();
    }

    public int getRightValue() {
        return chain.isEmpty() ? -1 : chain.getLast().getRight();
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    @Override
    public String toString() {
        if (chain.isEmpty()) return "Empty Board";
        StringBuilder sb = new StringBuilder();
        sb.append("(L) ");
        for (DominoTile tile : chain) {
            sb.append(tile).append(" ");
        }
        sb.append("(R)");
        return sb.toString();
    }
}