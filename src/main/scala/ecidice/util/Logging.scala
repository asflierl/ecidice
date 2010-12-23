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

package ecidice.util

import java.util.logging.{ Logger => JLogger }
import java.util.logging.Level

/**
 * Simple and thin wrapper around java.util.logging to mix logging support
 * into a class.
 * 
 * @author Andreas Flierl
 */
trait Logging {
  private def logger = JLogger.getLogger(getClass.getName)
  
  def logSevere(msg: => String): Unit = Logger.log(logger, Level.SEVERE, msg)
  
  def logSevere(msg: => String, exc: Throwable): Unit = Logger.log(logger, Level.SEVERE, msg, exc)
  
  def logWarn(msg: => String): Unit = Logger.log(logger, Level.WARNING, msg)

  def logWarn(msg: => String, exc: Throwable): Unit = Logger.log(logger, Level.WARNING, msg, exc)
  
  def logInfo(msg: => String): Unit = Logger.log(logger, Level.INFO, msg)
  
  def logInfo(msg: => String, exc: Throwable): Unit = Logger.log(logger, Level.INFO, msg, exc)
}

object Logging {
  def disable(): Unit = rootLogger.getHandlers foreach rootLogger.removeHandler
    
  private lazy val rootLogger = JLogger.getLogger("")
}

private object Logger {
  def log(logger: JLogger, level: Level, msg: => String): Unit = 
    if (logger.isLoggable(level)) logger.log(level, msg)
    
  def log(logger: JLogger, level: Level, msg: => String, exc: Throwable): Unit = 
    if (logger.isLoggable(level)) logger.log(level, msg, exc)
}
