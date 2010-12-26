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
  def format(r: LogRecord): String = {
    val msg = java.text.MessageFormat.format(r.getMessage, r.getParameters:_*)
    val className = compress(r.getSourceClassName)
    val time = new DateTime(r.getMillis)
    
    val b = new StringBuilder
    b append time append " "
    b append r.getLevel append " " append className append "#"
    b append r.getSourceMethodName append ": " append msg append '\n'
    b toString
  }
  
  private def compress(name: String) = pattern.matcher(name).replaceAll("$1.")
  
  private val pattern = java.util.regex.Pattern.compile("([^.])([^.]*)\\.")
}