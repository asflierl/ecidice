/*
 * Copyright (c) 2009-2010, Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the names of the copyright holders nor the names of the
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.modelold

import ecidice.SpecBase

object DiceMatcherSpec extends SpecBase with GameContexts {
  def matcher = new DiceMatcher(board)
  
  "A dice matcher" ->-(simpleGame) should {
    
    "correctly find a group of matching dice" in {
      val d = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
      d(8).change(Transform.ROTATE_FORWARD)
      d(4).change(Transform.ROTATE_FORWARD)
      
      val inc = d.filter(_.top == 6)
      val exc = d.filter(_.top != 6)
      
      val searchResult = matcher.find(d(0), board(0, 0))
      
      inc.foreach(searchResult must contain(_))
      exc.foreach(searchResult must not contain(_))
    }
    
    "correctly find only one of two groups of matching dice" in {
      val d00 = placeDice(0, 0)
      val d10 = placeDice(1, 0)
      val d02 = placeDice(0, 2)
      val d12 = placeDice(1, 2)
      val d11 = placeDice(1, 1)
      d11.change(Transform.ROTATE_LEFT)
      
      val s = matcher.find(d12, board(1, 2))
      
      List(d02, d12) foreach (s must contain(_))
      List(d00, d10, d11) foreach (s must not contain(_))
    }
    
    "correctly find a board full of matching dice" in {
      val dice = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
      
      val s = matcher.find(dice(4), board(1, 1))
      
      dice foreach (s must contain(_))
    }
    
    "correctly find no matches of an isolated dice" in {
      List((1, 0), (0, 1), (2, 1), (1, 2)).foreach(placeDice(_))
      val d = placeDice(1, 1)
      d.change(Transform.ROTATE_FORWARD)
      
      val matches = matcher.find(d, board(1, 1))
      
      matches mustEqual Set(d)
    }
  }
}
