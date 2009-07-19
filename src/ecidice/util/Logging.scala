package ecidice.util

import java.util.logging.{ Logger => JLogger }
import java.util.logging.Level

/**
 * Simple trait that adds logging support to a class.
 * 
 * @author Andreas Flierl
 */
trait Logging {
  private val className = getClass.getName
  
  /** 
   * Wraps a Java logger instance and provides some convenience methods.
   */
  object Logger {
    private val logger = JLogger.getLogger(className)
    
    /**
     * Logs a message with SEVERE priorty.
     */
    def severe(msg: String) {
      logger.log(Level.SEVERE, msg)
    }
    
    /**
     * Logs a message and an attached exception with SEVERE priority.
     */
    def severe(msg: String, exc: Throwable) {
      logger.log(Level.SEVERE, msg, exc)
    }
    
    /**
     * Logs a message with WARNING priorty.
     */
    def warn(msg: String) {
      logger.log(Level.WARNING, msg)
    }
    
    /**
     * Logs a message and an attached exception with WARNING priority.
     */
    def warn(msg: String, exc: Throwable) {
      logger.log(Level.WARNING, msg, exc)
    }
    
    /**
     * Logs a message with INFO priorty.
     */
    def info(msg: String) {
      logger.log(Level.INFO, msg)
    }
    
    /**
     * Logs a message and an attached exception with INFO priority.
     */
    def info(msg: String, exc: Throwable) {
      logger.log(Level.INFO, msg, exc)
    }
  }
}
