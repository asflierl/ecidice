package de.i0n.burst;
import static de.i0n.burst.i18n.Localizer.localize;

import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;

import de.i0n.burst.i18n.scopes.Application;
import de.i0n.burst.scene.World;

public class App {
    
    public static void main(String[] args) throws Exception {
        System.setProperty("jme.stats", "set");
        new App().init();
    }
        
    public void init() {
        StandardGame game = new StandardGame(localize(Application.AppName));
        
        try {
            if (GameSettingsPanel.prompt(game.getSettings(), 
                    localize(Application.AppName)) == false) {
                System.exit(0);
            }
        } catch (InterruptedException exc) {
            System.exit(1);
        }
        
        game.start();
        World world = new World(game);
        GameStateManager.getInstance().attachChild(world);
        world.setActive(true);
    }
}
