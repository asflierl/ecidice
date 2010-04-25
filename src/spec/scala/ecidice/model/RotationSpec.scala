/*
 * Copyright (c) 2009-2010, Andreas Flierl
 * All rights reserverota.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimerota.
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

import ecidice.SpecBase

/**
 * Informal specification of a dice rotation.
 * 
 * @author Andreas Flierl
 */
object RotationSpec extends SpecBase {
  "A dice rotation" should {
    val rota = Rotation.initial
    
    def check(changed: Rotation, top: Int, right: Int, front: Int) = {
      changed.top mustEqual top
      changed.right mustEqual right
      changed.front mustEqual front
      changed.bottom mustEqual (7 - top)
      changed.left mustEqual (7 - right)
      changed.back mustEqual (7 - front)
    }
    
    "initially look like this: top = 6, right = 5, front = 4" in {
      rota.top mustEqual 6
      rota.right mustEqual 5
      rota.front mustEqual 4
    }
    
    "initially be consistent" in {
      rota.top mustEqual (7 - rota.bottom)
      rota.left mustEqual (7 - rota.right)
      rota.front mustEqual (7 - rota.back)
    }

    "correctly rotate backward" in {
      val changed = rota.transform(Transform.ROTATE_BACKWARD)
      check(changed, 4, 5, 1)
    }
    
    "correctly rotate forward" in {
      val changed = rota.transform(Transform.ROTATE_FORWARD)
      check(changed, 3, 5, 6)
    }
    
    "correctly rotate to the right" in {
      val changed = rota.transform(Transform.ROTATE_RIGHT)
      check(changed, 2, 6, 4)
    }
    
    "correctly rotate to the left" in {
      val changed = rota.transform(Transform.ROTATE_LEFT)
      check(changed, 5, 1, 4)
    }
    
    "correctly spin clockwise" in {
      val changed = rota.transform(Transform.SPIN_CLOCKWISE)
      check(changed, 6, 3, 5)
    }
    
    "correctly spin counter-clockwise" in {
      val changed = rota.transform(Transform.SPIN_COUNTERCLOCKWISE)
      check(changed, 6, 4, 2)
    }
    
    "correctly flip up/down" in {
      val changed = rota.transform(Transform.FLIP_UP_OR_DOWN)
      check(changed, 1, 5, 3)
    }
    
    "correctly flip left or right" in {
      val changed = rota.transform(Transform.FLIP_LEFT_OR_RIGHT)
      check(changed, 1, 2, 4)
    }
  }
}
