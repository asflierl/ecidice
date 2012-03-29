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
    
    "initially look like this: top = 6, right = 5, front = 4" in {
      die.top must be equalTo 6 
      die.right must be equalTo 5
      die.front must be equalTo 4
    }
    
    "initially be consistent " in {
      die.top must be equalTo (7 - die.bottom)
      die.left must be equalTo (7 - die.right)
      die.front must be equalTo (7 - die.back)
    }
    
    "know about all different die rotations" in {
      all.size must be equalTo 24
    }
    
    "correctly rotate backward" in {
      val changed = die.transform(RotateBackward)
      check(changed, 4, 5, 1)
    }
    
    "correctly rotate forward" in {
      val changed = die.transform(RotateForward)
      check(changed, 3, 5, 6)
    }
    
    "correctly rotate to the right" in {
      val changed = die.transform(RotateRight)
      check(changed, 2, 6, 4)
    }
    
    "correctly rotate to the left" in {
      val changed = die.transform(RotateLeft)
      check(changed, 5, 1, 4)
    }
    
    "correctly spin clockwise" in {
      val changed = die.transform(SpinClockwise)
      check(changed, 6, 3, 5)
    }
    
    "correctly spin counter-clockwise" in {
      val changed = die.transform(SpinCounterclockwise)
      check(changed, 6, 4, 2)
    }
    
    "correctly flip up/down" in {
      val changed = die.transform(FlipUpOrDown)
      check(changed, 1, 5, 3)
    }
    
    "correctly flip left or right" in {
      val changed = die.transform(FlipLeftOrRight)
      check(changed, 1, 2, 4)
    }
  }
  
  def check(changed: Die, top: Int, right: Int, front: Int) = {
    (changed.top must be equalTo top) and
    (changed.right must be equalTo right) and
    (changed.front must be equalTo front) and
    (changed.bottom must be equalTo (7 - top)) and
    (changed.left must be equalTo (7 - right)) and
    (changed.back must be equalTo (7 - front)) and
    (all must contain(changed))
  }
  
  val all = Die.allRotations.toSet
}
