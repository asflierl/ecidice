package de.i0n.burst;
import java.util.concurrent.Callable;

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;
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
        
        final Sphere[] spheres = {
            new Sphere("s0", new Vector3f(0f, 0f, 0f), 32, 32, 1f),
            new Sphere("s1", new Vector3f(0f, 3f, 0f), 32, 32, 1f),
            new Sphere("s2", new Vector3f(3f, 0f, 0f), 32, 32, 1f),
            new Sphere("s3", new Vector3f(0f, -3f, 0f), 32, 32, 1f),
            new Sphere("s4", new Vector3f(-3f, 0f, 0f), 32, 32, 1f),
        };
        
        for (Sphere obj : spheres) {
            obj.setRandomColors();
        }
        
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() {
                for (Sphere obj : spheres) {
                    obj.lock();
                }
                return null;
            }
        });
        
        for (Sphere obj : spheres) {
            obj.updateRenderState();
            gameState.getRootNode().attachChild(obj);
        }
        
        gameState.getRootNode().updateRenderState();
    }
}
