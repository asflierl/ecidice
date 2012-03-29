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
package model

import ecidice.UnitSpec
import Transform._

import org.specs2._

object DieSpec extends UnitSpec {
  "A die" should {
    val die = Die.default
    
    "initially look like this: top = 6, right = 5, front = 4, bottom: 1, left: 2, back: 3" in {
      die must lookLike (6, 5, 4)
    }
    
    "know about all different die rotations" in {
      Die.allRotations must haveSize (24)
    }
    
    "correctly rotate backward" in {
      die transform RotateBackward must lookLike (4, 5, 1)
    }
    
    "correctly rotate forward" in {
      die transform RotateForward must lookLike (3, 5, 6)
    }
    
    "correctly rotate to the right" in {
      die transform RotateRight must lookLike (2, 6, 4)
    }
    
    "correctly rotate to the left" in {
      die transform RotateLeft must lookLike (5, 1, 4)
    }
    
    "correctly spin clockwise" in {
      die transform SpinClockwise must lookLike (6, 3, 5)
    }
    
    "correctly spin counter-clockwise" in {
      die transform SpinCounterclockwise must lookLike (6, 4, 2)
    }
    
    "correctly flip up/down" in {
      die transform FlipUpOrDown must lookLike (1, 5, 3)
    }
    
    "correctly flip left or right" in {
      die transform FlipLeftOrRight must lookLike (1, 2, 4)
    }
  }
  
  def lookLike(top: Int, right: Int, front: Int) =
    (equalTo(top)       ^^ ((_: Die).top)) and
    (equalTo(right)     ^^ ((_: Die).right)) and
    (equalTo(front)     ^^ ((_: Die).front)) and
    (equalTo(7 - top)   ^^ ((_: Die).bottom)) and
    (equalTo(7 - right) ^^ ((_: Die).left)) and
    (equalTo(7 - front) ^^ ((_: Die).back)) and
    (beOneOf(Die.allRotations:_*)) 
}
