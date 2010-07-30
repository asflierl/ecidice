/*
 * Copyright (c) 2009-2010 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

//package ecidice
//
//import com.jme.input.MouseInput
//import com.jmex.editors.swing.settings.GameSettingsPanel
//import com.jmex.game.StandardGame
//import com.jme.app.AbstractGame
//import com.jme.system.PreferencesGameSettings
//import com.jmex.game.state.GameStateManager
//
//import ecidice.controller.WorldController
//import ecidice.i18n.L10n
//import ecidice.util.Logging
//
//import java.util.prefs.Preferences
//
///**
// * The application's entry point object.
// * 
// * @author Andreas Flierl
// */
//object Main extends Logging {
//  /**
//   * Bootstraps the game classes.
//   * 
//   * @param args will be ignored
//   */
//  def main(args: Array[String]) {
//    val settings = new PreferencesGameSettings(Preferences.userRoot().node(
//        L10n.of.appName))
//
//    try {
//      if (false == GameSettingsPanel.prompt(settings, L10n.of.appName)) {
//        Logger.info("game startup cancelled")
//        return
//      }
//    } catch {
//      case exc: InterruptedException => {
//        Logger.warn("game startup interrupted", exc)
//        return
//      }
//    }
//
//    val game = new StandardGame(L10n.of.appName,
//                                StandardGame.GameType.GRAPHICAL, settings)
//
//    game.start()
//    game.setUncaughtExceptionHandler(Terminator)
//    
//    val worldController = new WorldController(game)
//    GameStateManager.getInstance().attachChild(worldController)
//    MouseInput.get.setCursorVisible(true)
//    worldController.setActive(true)
//  }
//  
//  /**
//   * Handler for uncaught exceptions in JME threads.
//   */
//  private object Terminator extends Thread.UncaughtExceptionHandler {
//    def uncaughtException(thread: Thread, thrown: Throwable) {
//      Logger.severe("uncaught exception in thread " + thread, thrown)
//      System.exit(1)
//    }
//  }
//}
