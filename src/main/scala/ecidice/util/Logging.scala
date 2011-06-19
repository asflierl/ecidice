/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package util

import java.util.logging.{ ConsoleHandler,
                           Formatter,
                           Handler,
                           Level, 
                           Logger => JLogger, 
                           LogRecord }
import java.util.logging.Level._
import org.joda.time.DateTime
import java.io.StringWriter
import java.io.PrintWriter

/**
 * Simple and thin wrapper around java.util.logging to mix logging support
 * into a class.
 * 
 * @author Andreas Flierl
 */
trait Logging {
  private def logger = JLogger.getLogger(getClass.getName)
  
  def logSevere(msg: => String): Unit = Logger.log(logger, SEVERE, msg)
  def logSevere(msg: => String, exc: Throwable): Unit = Logger.log(logger, SEVERE, msg, exc)
  def logWarn(msg: => String): Unit = Logger.log(logger, WARNING, msg)
  def logWarn(msg: => String, exc: Throwable): Unit = Logger.log(logger, WARNING, msg, exc)
  def logInfo(msg: => String): Unit = Logger.log(logger, INFO, msg)
  def logInfo(msg: => String, exc: Throwable): Unit = Logger.log(logger, INFO, msg, exc)
}

object Logging {
  private lazy val rootLogger = JLogger.getLogger("")
  
  def disable(): Unit = removeAllHandlers()
    
  def showInfoAndHigher(): Unit = rootLogger setLevel INFO
  def showWarningsAndHigher(): Unit = rootLogger setLevel WARNING
  def showSevereOnly(): Unit = rootLogger setLevel SEVERE

  def writeTo(handlers: Handler*) = {
    removeAllHandlers()
    handlers foreach rootLogger.addHandler
  }
  
  def console: Handler = init(new ConsoleHandler())(_ setFormatter ShortMessageFormatter)
  
  private def removeAllHandlers(): Unit = rootLogger.getHandlers foreach rootLogger.removeHandler 
}

private object Logger {
  def log(logger: JLogger, level: Level, msg: => String): Unit = 
    if (logger.isLoggable(level)) logger.log(level, msg)
    
  def log(logger: JLogger, level: Level, msg: => String, exc: Throwable): Unit = 
    if (logger.isLoggable(level)) logger.log(level, msg, exc)
}

private object ShortMessageFormatter extends Formatter {
  def format(record: LogRecord): String = {
    val msg = java.text.MessageFormat.format(record.getMessage, record.getParameters:_*)
    val className = compress(record.getLoggerName)
    val time = new DateTime(record.getMillis)
    
    val builder = new StringBuilder
    builder append time append " "
    builder append record.getLevel append " " append className 
    builder append ": " append msg append '\n'
      
    if (record.getThrown != null) {
      val stringWriter = new StringWriter
      val printWriter = new PrintWriter(stringWriter)
      record.getThrown printStackTrace printWriter
      printWriter flush()
      builder.toString + stringWriter.toString
    } else builder.toString
  }
  
  private def compress(name: String) = pattern.matcher(name).replaceAll("$1.")
  
  private val pattern = java.util.regex.Pattern.compile("([^.])([^.]*)\\.")
}
