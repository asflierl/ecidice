package de.i0n.burst;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.math.Vector3f;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

import de.i0n.burst.i18n.Localizer;
import de.i0n.burst.i18n.scopes.Application;
import de.i0n.burst.shape.Bubble;

public class App {
    
    public static void main(String[] args) throws Exception {
        new App().init();
    }
        
    public void init() {
        StandardGame app = new StandardGame(Localizer.get(Application.AppName));
        app.setConfigShowMode(ConfigShowMode.NeverShow);
        app.getSettings().setWidth(1024);
        app.getSettings().setHeight(768);
        app.getSettings().setDepth(32);
        app.getSettings().setFullscreen(true);
        app.start();
        
        DebugGameState gameState = new DebugGameState();
        GameStateManager.getInstance().attachChild(gameState);
        gameState.setActive(true);
        
        app.getCamera().setLocation(new Vector3f(0f, 0f, 40f));
        
        final List<Bubble> bubbles = new ArrayList<Bubble>();
        for (int ring = 0; ring < Bubble.MAXINDEX; ++ring) {
            for (int index = 0; index < Bubble.MAXINDEX; ++index) {
                bubbles.add(new Bubble(ring, index));
            }
        }
        
        GameTaskQueueManager.getManager().update(new Callable<Void>() {
            public Void call() {
                for (Bubble obj : bubbles) {
                    obj.lock();
                }
                return null;
            }
        });
        
        for (Bubble obj : bubbles) {
            obj.updateRenderState();
            gameState.getRootNode().attachChild(obj);
        }
        
        gameState.getRootNode().updateRenderState();
    }
}
