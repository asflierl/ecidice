package de.i0n.burst.model;

public enum Direction {
    North,
    West,
    South,
    East;
    
    public Direction opposite() {
        switch (this) {
        case North:
            return South;
        case West:
            return East;
        case South:
            return North;
        case East:
            return West;
        }
        throw new AssertionError("unknown direction");
    }
}
