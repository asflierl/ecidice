package de.i0n.burst;
import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

import de.i0n.burst.i18n.Localizer;
import de.i0n.burst.i18n.scopes.Application;

public class App {
    
    public static void main(String[] args) throws Exception {
        System.out.println(Localizer.get(Application.AppName));
    }
        
    public void init() {
        StandardGame app = new StandardGame("Rune");
        app.setConfigShowMode(ConfigShowMode.NeverShow);
        app.start();
        
        DebugGameState gameState = new DebugGameState();    // Create our game state
        GameStateManager.getInstance().attachChild(gameState);  // Attach it to the GameStateManager
        gameState.setActive(true);  // Activate it
        
        Box box = new Box("TestBox", new Vector3f(), 1.0f, 1.0f, 1.0f);     // Create a Box
        box.setRandomColors();  // Set random colors on it
        box.updateRenderState();    // Update the render state so the colors appear (the game is already running, so this must always be done)
        gameState.getRootNode().attachChild(box);   // Attach the box to rootNode in DebugGameState
        gameState.getRootNode().updateRenderState();
    }
}
