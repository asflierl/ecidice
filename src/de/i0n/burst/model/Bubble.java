package de.i0n.burst.model;

public class Bubble {
    private final int[] neighbours = new int[Direction.values().length];
    
    private BubbleColor color;
    
    public Bubble(BubbleColor color) {
        this.color = color;
    }
    
    public void resetNeighbours() {
        for (Direction d : Direction.values()) {
            neighbours[d.ordinal()] = 0;
        }
    }
    
    public void updateNeighbours(Direction dir, Bubble neighbour) {
        
    }
    
    public int getNeighbours(Direction dir) {
        return neighbours[dir.ordinal()];
    }
}
