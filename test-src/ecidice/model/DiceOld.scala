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

import org.scalatest._
import org.scalatest.matchers._

/**
 * Spec-based tests of the dice model.
 * 
 * @author Andreas Flierl
 */
class DiceOld extends Spec with Assertions with ShouldMatchers 
    with BeforeAndAfter {
  
  private var d : Dice = _
  
  override def beforeEach() = {
    d = new Dice
  }
  
  def checkDice(top: Int, right: Int, front: Int) = {
    d.top should be (top)
    d.right should be (right)
    d.front should be (front)
    d.bottom should be (7 - top)
    d.left should be (7 - right)
    d.back should be (7 - front)
  }
  
  describe("A dice") {
    
    it("should initially be t6 r5 f4") {
      d.top should be (6)
      d.right should be (5)
      d.front should be (4)
    }
    
    it("should initially be consistent") {
      d.top should equal (7 - d.bottom)
      d.left should equal (7 - d.right)
      d.front should equal (7 - d.back)
    }

    it("should correctly rotate upwards (towards the back)") {
      d.change(Transform.ROTATE_UP)
      checkDice(4, 5, 1)
    }
    
    it("should correctly rotate downwards (towards the front)") {
      d.change(Transform.ROTATE_DOWN)
      checkDice(3, 5, 6)
    }
    
    it("should correctly rotate to the right") {
      d.change(Transform.ROTATE_RIGHT)
      checkDice(2, 6, 4)
    }
    
    it("should correctly rotate to the left") {
      d.change(Transform.ROTATE_LEFT)
      checkDice(5, 1, 4)
    }
    
    it("should correctly spin clockwise") {
      d.change(Transform.SPIN_CLOCKWISE)
      checkDice(6, 3, 5)
    }
    
    it("should correctly spin counter-clockwise") {
      d.change(Transform.SPIN_COUNTERCLOCKWISE)
      checkDice(6, 4, 2)
    }
    
    it("should correctly flip up/down") {
      d.change(Transform.FLIP_UP_OR_DOWN)
      checkDice(1, 5, 3)
    }
    
    it("should correctly flip left or right") {
      d.change(Transform.FLIP_LEFT_OR_RIGHT)
      checkDice(1, 2, 4)
    }
  }
}
