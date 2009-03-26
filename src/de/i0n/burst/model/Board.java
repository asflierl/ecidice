package de.i0n.burst.model;

public class Board {
    private final int dimension;
    private final Bubble[] bubbles;
    
    public Board(int dimension) {
        this.dimension = dimension;
        bubbles = new Bubble[dimension * dimension];
    }
    
    public int getDimension() {
        return dimension;
    }
}
