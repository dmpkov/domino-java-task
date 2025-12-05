package com.dmpkov.domino;

public class DominoTile {
    private int left;
    private int right;

    public DominoTile(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() { return left; }
    public int getRight() { return right; }

    public void flip() {
        int temp = left;
        left = right;
        right = temp;
    }

    @Override
    public String toString() {
        return "[" + left + "|" + right + "]";
    }
}
