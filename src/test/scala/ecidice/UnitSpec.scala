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

import org.specs2._
import mock._
import matcher._
import org.specs2.execute.Result
import org.specs2.specification.Example
import org.specs2.specification.FormattingFragments
import org.specs2.specification.StandardFragments

trait UnitSpec extends mutable.Specification
                  with DataTables 
                  with Mockito 
                  with ScalaCheck 
                  with SpecHelpers {
  import UnitSpec._
  
  implicit def enrichUnitSpecString(s: String) = new EnrichedString(s)
  
  class EnrichedString(pre: String) {
    def § = textFragment(pre.m)
    
    def forExample[A](r: String => A)(implicit ev: A => Result): Example = {
      val lines = detectAndStripMargin(pre)
      val exampleText = lines.head
      val preBoardText = joinLines(lines.tail.dropWhile(_.trim.isEmpty).takeWhile(line => ! tableHeaderLine.matcher(line).matches))
      val boardAndFollowingText = lines.dropWhile(line => ! tableHeaderLine.matcher(line).matches)
      val board = joinLines(boardAndFollowingText.takeWhile(! _.trim.isEmpty))
      val example = exampleText in r(board)
      addFragments(bt)
      textFragment(preBoardText + joinLines(boardAndFollowingText))
      example
    }
  }
}
object UnitSpec {
  private[UnitSpec] val tableHeaderLine = """\s*[|-]+\s*""".r.pattern
}
