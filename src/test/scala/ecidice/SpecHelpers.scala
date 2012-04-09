/*
 * Copyright (c) 2009-2012 Andreas Flierl
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

import org.specs2.matcher._

import scala.util.Properties.lineSeparator
import scala.collection.breakOut
import scalaz.Validation
import scalaz.Success
import scalaz.Failure

trait SpecHelpers { this: DataTables with Matchers with StandardMatchResults =>
  val floatDelta = 1E-6f
  val delta = 1E-12d
  implicit def enrichString(s: String) = new EnrichedString(s)
  
  def detectAndStripMargin(s: String): List[String] = {
    val text = s.lines.dropWhile(_.trim.isEmpty).toList.reverse.dropWhile(_.trim.isEmpty).reverse
    val indentation = text.head.takeWhile(' '==)
    text.map(_.stripPrefix(indentation))
  }
  
  def joinLines(lines: List[String]) = lines.mkString("", lineSeparator, lineSeparator)
  
  class EnrichedString(pre: String) {
    def /(post: String) = pre + "\n" + post
    
    def m = joinLines(detectAndStripMargin(pre))
  }
  
  def succeedWith[A](a: A) = be_===(Success(a): Valid[A])
  
  def fail[A]: Matcher[Valid[A]] = (v: Valid[A]) => (
    v match { 
      case Failure(_) => true
      case _ => false
    }, 
    v.toString + " is not a failure")
}
