package de.i0n.burst;
import java.util.concurrent.Callable;

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

import de.i0n.burst.i18n.Localizer;
import de.i0n.burst.i18n.scopes.Application;

public class App {
    
    public static void main(String[] args) throws Exception {
        new App().init();
    }
        
    public void init() {
        StandardGame app = new StandardGame(Localizer.get(Application.AppName));
        app.setConfigShowMode(ConfigShowMode.NeverShow);
        app.start();
        
        DebugGameState gameState = new DebugGameState();
        GameStateManager.getInstance().attachChild(gameState);
        gameState.setActive(true);
        
        final Box box = new Box("TestBox", new Vector3f(), 1.0f, 1.0f, 1.0f);
        box.setRandomColors();
        
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() throws Exception {
                box.lock();
                return null;
            }
        });
        
        box.updateRenderState();
        gameState.getRootNode().attachChild(box);
        gameState.getRootNode().updateRenderState();
    }
}
