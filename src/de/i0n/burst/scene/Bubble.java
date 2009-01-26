package de.i0n.burst.scene;

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

/**
 * Represents a single bubble on the game board.
 * 
 * @author i0n
 */
public class Bubble extends Node {
    private static final long serialVersionUID = 1L;
    private static final float R = 1f;   // radius
    private static final float D = 2.5f; // distance
    
    private static final ColorRGBA alphaMask = new ColorRGBA(1f, 1f, 1f, 0.25f);
    
    private final int row;
    private final int column;
    private final int maxindex;
    
    /**
     * Creates a new bubble and places it on the board.
     * 
     * @param row the row this bubble is in
     * @param column the column this bubble is in
     * @param maxindex the maximum number of columns and rows
     */
    public Bubble(int row, int column, int maxindex) {
        super(String.format("BubbleNode-%d-%d", row, column));
        this.row = row;
        this.column = column;
        this.maxindex = maxindex;
        
        Sphere sphere = createSphere();
        addLight(sphere);
        
        attachChild(sphere);
        updateRenderState();
    }
    
    /**
     * Creates and returns the surrounding sphere.
     * 
     * @return a new, transparent sphere
     */
    private Sphere createSphere() {
        Sphere sphere = new Sphere(String.format("Bubble-%d-%d", row, column));
        
        float xpos = column * D - (maxindex - 1) * D / 2f;
        float ypos = row * D - (maxindex - 1) * D / 2f;
        
        sphere.updateGeometry(new Vector3f(xpos, ypos, 0f), 8, 24, R);

        final ColorRGBA color;
        switch (row) {
        case 0:
            color = ColorRGBA.white;
            break;
        case 1:
            color = ColorRGBA.red;
            break;
        case 2:
            color = ColorRGBA.green;
            break;
        case 3:
            color = ColorRGBA.yellow;
            break;
        case 4:
            color = ColorRGBA.blue;
            break;
        case 5:
            color = ColorRGBA.orange;
            break;
        case 6:
            color = ColorRGBA.magenta;
            break;
        case 7:
            color = ColorRGBA.cyan;
            break;
        default:
            color = ColorRGBA.black;
        }
        
        // Material
        MaterialState materialState = DisplaySystem.getDisplaySystem()
            .getRenderer().createMaterialState();
        materialState.setAmbient(ColorRGBA.black.mult(alphaMask));
        materialState.setDiffuse(color.mult(alphaMask));
        materialState.setSpecular(ColorRGBA.white);
        materialState.setShininess(128);
        materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        materialState.setEnabled(true);
        
        sphere.setRenderState(materialState);
        
        sphere.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        
        // ZBuffer
        ZBufferState z = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        z.setEnabled(true);
        z.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        z.setWritable(false);
        sphere.setRenderState(z);

        return sphere;
    }
    
    /**
     * Adds a light on top of the sphere.
     * 
     * @param sphere the sphere that shall be decorated
     * @return the newly added light
     */
    private PointLight addLight(Sphere sphere) {
        PointLight light = new PointLight();
        light.setLocation(sphere.getCenter().add(new Vector3f(R / 2f, R / 2f, 
                R + .5f)));
        light.setAttenuate(true);
        light.setConstant(.5f);
        light.setLinear(0);
        light.setQuadratic(.1f);
        light.setSpecular(ColorRGBA.white);
        light.setDiffuse(ColorRGBA.white);
        light.setLightMask(LightState.MASK_AMBIENT | 
                LightState.MASK_GLOBALAMBIENT);
        light.setEnabled(true);
        
        LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        ls.setTwoSidedLighting(true);
        sphere.setRenderState(ls);
        ls.attach(light);
        
        sphere.setRenderState(ls);
        
        return light;
    }
}
