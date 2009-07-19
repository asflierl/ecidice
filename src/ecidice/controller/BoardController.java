package ecidice.controller;

import java.util.ArrayList;
import java.util.List;

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.BasicGameStateNode;
import com.jmex.game.state.GameStateNode;

import ecidice.view.BubbleView;

/**
 * Represents the game board, i.e. the area with 8 rows of 8 columns of bubbles.
 * 
 * @author i0n
 */
public class BoardController extends BasicGameStateNode<GameStateNode<?>> {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new game board with all its children objects.
     */
    public BoardController() {
        super("game board");
        
        addBubbles();
        setupBlending();
        addHighlights();
        
        rootNode.updateRenderState();
    }
    
    /**
     * Adds the 64 bubbles as children to this node.
     */
    private void addBubbles() {
        final List<BubbleView> bubbleViews = new ArrayList<BubbleView>();
        for (int ring = 0; ring < 8.; ++ring) {
            for (int index = 0; index < 8; ++index) {
                bubbleViews.add(new BubbleView(ring, index, 8));
            }
        }
        
        for (BubbleView obj : bubbleViews) {
            rootNode.attachChild(obj);
        }
    }
     
    /**
     * Enables alpha blending for the whole board.
     */
    private void setupBlending() {
        // Blending
        final BlendState alphaState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        alphaState.setBlendEnabled(true);
        alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        alphaState.setTestEnabled(true);
        alphaState.setTestFunction(BlendState.TestFunction.GreaterThan);
        alphaState.setEnabled(true);
        
        rootNode.setRenderState(alphaState);
    }
    
    /**
     * Adds a specular highlighting point light.
     */
    private void addHighlights() {
        LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();

        PointLight spec = new PointLight();
        spec.setLocation(new Vector3f(1, 20, 0));
        spec.setSpecular(ColorRGBA.white);
        spec.setAttenuate(true);
        spec.setConstant(.25f);
        spec.setLightMask(LightState.MASK_AMBIENT | 
                LightState.MASK_GLOBALAMBIENT | LightState.MASK_DIFFUSE);
        spec.setEnabled(true);
        ls.attach(spec);
        
        rootNode.setRenderState(ls);
    }
}
