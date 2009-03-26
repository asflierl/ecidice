package de.i0n.burst;
import static de.i0n.burst.i18n.Localizer.localize;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.MouseInput;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;

import de.i0n.burst.i18n.scopes.Application;
import de.i0n.burst.view.World;

/**
 * Entry point (aka main class) for the startup of the whole application.
 * 
 * @author i0n
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    /**
     * Starts up the application.
     * 
     * @param args will be ignored
     */
    public static void main(String[] args) {
        new App().init();
    }
        
    /**
     * Prompts for game settings and bootstraps the game world.
     */
    public void init() {
        final StandardGame game = new StandardGame(localize(
                Application.AppName));
        
        try {
            if (GameSettingsPanel.prompt(game.getSettings(), 
                    localize(Application.AppName)) == false) {
                logger.log(Level.INFO, "game startup cancelled");
                return;
            }
        } catch (InterruptedException exc) {
            logger.log(Level.WARNING, "game startup interrupted", exc);
            return;
        }
        
        game.start();
        game.setUncaughtExceptionHandler(new FallbackHandler());
        
        World world = new World(game);
        GameStateManager.getInstance().attachChild(world);
        MouseInput.get().setCursorVisible(true);
        world.setActive(true);
    }
    
    /**
     * Handles any exceptions from the GL thread that were not properly caught.
     * 
     * @author i0n
     */
    private class FallbackHandler implements Thread.UncaughtExceptionHandler {        
        /* (non-Javadoc)
         * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
         */
        public void uncaughtException(Thread thread, Throwable thrown) {
            logger.log(Level.SEVERE, "uncaught exception in thread " + thread, 
                    thrown);
            System.exit(1);
        }
    }
}
