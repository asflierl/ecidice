package de.i0n.burst

import com.jme.input.MouseInput;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;

import de.i0n.burst.controller.WorldController;
import de.i0n.burst.i18n.Localizer;
import de.i0n.burst.util.Logging;

/**
 * The application's entry point object. 
 */
object Main extends Logging {
  /**
   * Bootstraps the game classes.
   * 
   * @param args will be ignored
   */
  def main(args: Array[String]) {
    val game = new StandardGame(Localizer.translate.appName)

    try {
      if (GameSettingsPanel.prompt(game.getSettings(), 
          Localizer.translate.appName) == false) {
        Logger.info("game startup cancelled")
        return
      }
    } catch {
      case exc: InterruptedException => {
        Logger.warn("game startup interrupted", exc)
        return
      }
    }

    game.start();
    game.setUncaughtExceptionHandler(Terminator);
    
    val worldController = new WorldController(game);
    GameStateManager.getInstance().attachChild(worldController);
    MouseInput.get().setCursorVisible(true);
    worldController.setActive(true);
  }
  
  /**
   * Handler for uncaught exceptions in JME threads.  Logs the exception and
   * terminates the program.
   */
  private object Terminator extends Thread.UncaughtExceptionHandler {
    def uncaughtException(thread: Thread, thrown: Throwable) {
        Logger.severe("uncaught exception in thread " + thread, 
            thrown);
        System.exit(1);
      }
  }
}
