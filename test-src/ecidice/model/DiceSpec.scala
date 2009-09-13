/*
 * Copyright (c) 2009, Andreas Flierl
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
 * - Neither the names of the copyright holders nor the names of its 
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

package ecidice.model

/**
 * Spec-based tests of the dice model.
 * 
 * @author Andreas Flierl
 */
class DiceSpec extends SpecBase {
  "A dice" should {
    val d = new Dice
    
    def checkDice(top: Int, right: Int, front: Int) = {
      d.top mustEqual top
      d.right mustEqual right
      d.front mustEqual front
      d.bottom mustEqual (7 - top)
      d.left mustEqual (7 - right)
      d.back mustEqual (7 - front)
    }
    
    "initially look like this: top = 6, right = 5, front = 4" in {
      d.top mustEqual 6
      d.right mustEqual 5
      d.front mustEqual 4
    }
    
    "initially be consistent" in {
      d.top mustEqual (7 - d.bottom)
      d.left mustEqual (7 - d.right)
      d.front mustEqual (7 - d.back)
    }

    "correctly rotate upwards (towards the back)" in {
      d.change(Transform.ROTATE_UP)
      checkDice(4, 5, 1)
    }
    
    "correctly rotate downwards (towards the front)" in {
      d.change(Transform.ROTATE_DOWN)
      checkDice(3, 5, 6)
    }
    
    "correctly rotate to the right" in {
      d.change(Transform.ROTATE_RIGHT)
      checkDice(2, 6, 4)
    }
    
    "correctly rotate to the left" in {
      d.change(Transform.ROTATE_LEFT)
      checkDice(5, 1, 4)
    }
    
    "correctly spin clockwise" in {
      d.change(Transform.SPIN_CLOCKWISE)
      checkDice(6, 3, 5)
    }
    
    "correctly spin counter-clockwise" in {
      d.change(Transform.SPIN_COUNTERCLOCKWISE)
      checkDice(6, 4, 2)
    }
    
    "correctly flip up/down" in {
      d.change(Transform.FLIP_UP_OR_DOWN)
      checkDice(1, 5, 3)
    }
    
    "correctly flip left or right" in {
      d.change(Transform.FLIP_LEFT_OR_RIGHT)
      checkDice(1, 2, 4)
    }
  }
}
