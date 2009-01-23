package de.i0n.burst.shape;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;

public class Bubble extends Sphere {
    public static final int MAXINDEX = 8;
    
    private static final float R = 1f;   // radius
    private static final float D = 2.5f; // distance
    
    private final int row;
    private final int column;
    
    public Bubble(int row, int column) {
        super(String.format("Bubble-%d-%d", row, column));
        
        this.row = row;
        this.column = column;
        
        float xpos = column * D - (MAXINDEX - 1) * D / 2f;
        float ypos = (MAXINDEX - 1) * D / 2f - row * D;
        
        updateGeometry(new Vector3f(xpos, ypos, 0f), 24, 24, R);
        
        setRandomColors();
    }
}
