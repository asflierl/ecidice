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

package ecidice.model

import ecidice.SpecBase
import Transform._

/**
 * Informal specification of a dice.
 * 
 * @author Andreas Flierl
 */
object DiceSpec extends SpecBase {
  "A dice" should {
    val dice = Dice.initial
    val all = Dice.allRotations.toSet
    
    def check(changed: Dice, top: Int, right: Int, front: Int) = {
      changed.top mustEqual top
      changed.right mustEqual right
      changed.front mustEqual front
      changed.bottom mustEqual (7 - top)
      changed.left mustEqual (7 - right)
      changed.back mustEqual (7 - front)
      all mustContain changed
    }
    
    "initially look like this: top = 6, right = 5, front = 4" in {
      dice.top mustEqual 6
      dice.right mustEqual 5
      dice.front mustEqual 4
    }
    
    "initially be consistent" in {
      dice.top mustEqual (7 - dice.bottom)
      dice.left mustEqual (7 - dice.right)
      dice.front mustEqual (7 - dice.back)
    }
    
    "know about all different dice rotations" in {
      all.size mustEqual 24
    }

    "correctly rotate backward" in {
      val changed = dice.transform(RotateBackward)
      check(changed, 4, 5, 1)
    }
    
    "correctly rotate forward" in {
      val changed = dice.transform(RotateForward)
      check(changed, 3, 5, 6)
    }
    
    "correctly rotate to the right" in {
      val changed = dice.transform(RotateRight)
      check(changed, 2, 6, 4)
    }
    
    "correctly rotate to the left" in {
      val changed = dice.transform(RotateLeft)
      check(changed, 5, 1, 4)
    }
    
    "correctly spin clockwise" in {
      val changed = dice.transform(SpinClockwise)
      check(changed, 6, 3, 5)
    }
    
    "correctly spin counter-clockwise" in {
      val changed = dice.transform(SpinCounterclockwise)
      check(changed, 6, 4, 2)
    }
    
    "correctly flip up/down" in {
      val changed = dice.transform(FlipUpOrDown)
      check(changed, 1, 5, 3)
    }
    
    "correctly flip left or right" in {
      val changed = dice.transform(FlipLeftOrRight)
      check(changed, 1, 2, 4)
    }
  }
}
