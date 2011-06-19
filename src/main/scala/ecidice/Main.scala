/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice

import ecidice.util.Logging
import com.jme3.app.Application

object Main extends Logging {
  def main(args: Array[String]): Unit = {
    Logging writeTo Logging.console
    Logging showWarningsAndHigher()
    
    val app = new App
    Thread setDefaultUncaughtExceptionHandler new ExceptionProxy(app)
    app start()
  }
  
  private class ExceptionProxy(app: Application) extends Thread.UncaughtExceptionHandler {
    def uncaughtException(thread: Thread, thrown: Throwable): Unit = {
      app.handleError("unhandled exception in thread " + thread, thrown)
    }
  }
}
