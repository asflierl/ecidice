package de.i0n.burst.model;

public class BoardModel {
    private final int dimension;
    private final BubbleModel[] bubbles;
    
    public BoardModel(int dimension) {
        this.dimension = dimension;
        bubbles = new BubbleModel[dimension * dimension];
    }
    
    public int getDimension() {
        return dimension;
    }
}
