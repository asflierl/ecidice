package de.i0n.burst.util

import java.util.logging.{ Logger => JLogger }
import java.util.logging.Level

trait Logging {
  private val className = getClass.getName
    
  object Logger {
    private val logger = JLogger.getLogger(className)
    
    def severe(msg: String) {
      logger.log(Level.SEVERE, msg)
    }
    
    def severe(msg: String, exc: Throwable) {
      logger.log(Level.SEVERE, msg, exc)
    }
    
    def warn(msg: String) {
      logger.log(Level.WARNING, msg)
    }
    
    def warn(msg: String, exc: Throwable) {
      logger.log(Level.WARNING, msg, exc)
    }
    
    def info(msg: String) {
      logger.log(Level.INFO, msg)
    }
    
    def info(msg: String, exc: Throwable) {
      logger.log(Level.INFO, msg, exc)
    }
  }
}
