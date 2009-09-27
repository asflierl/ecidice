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
 * Spec-based tests of the game model.
 * 
 * @author Andreas Flierl
 */
class GameSpec extends SpecBase with GameSetupHelper {
  "A game of ecidice (on a 3 x 3 board)" should {
    doBefore { reset() }
        
    "when the board is empty" >> {
      "not grant control on any tile" in {
        for (x <- 0 to 2; y <- 0 to 2) {
          reset()
          placePlayer(p1, (x, y))
          
          val request = "control request on (%d, %d)".format(x, y)
          p1.requestControl() aka request must be (None)
          
          p1.state mustEqual Player.Standing(b(x, y))
        }
      }
    }
    
    "with some dice on it" >> {
      "grant control over a single dice on the floor" in {
        placePlayer(p1, (1, 1))
        val d = placeDice(1, 1)
        
        p1.requestControl() mustEqual Some(d)
        d.state mustEqual Dice.Solid(b(1,1).floor, Some(p1))
        p1.state mustEqual Player.Controlling(d)
      }
      
      "grant control over the upper of 2 stacked dice" in {
        placePlayer(p1, (1, 1))
        val d1 = placeDice(1, 1)
        val d2 = placeDice(1, 1)
        
        p1.requestControl() mustEqual Some(d2)
        d2.state mustEqual Dice.Solid(b(1,1).raised, Some(p1))
        d1.state mustEqual Dice.Solid(b(1,1).floor, None)
        p1.state mustEqual Player.Controlling(d2)
      }
      
      
      "not grant control over an appearing dice" in {
        placePlayer(p1, (1, 1))
        g.spawnDice(1, 1) aka "spawning" must beSome[Dice]
        p1.requestControl() aka "control request" must beNone
      }
      
      "correctly find a group of matching dice" in {
        val d = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
        d(8).change(Transform.ROTATE_UP)
        d(4).change(Transform.ROTATE_UP)
        
        val inc = d.filter(_.top == 6)
        val exc = d.filter(_.top != 6)
        
        val searchResult = g.find(d(0), b(0, 0))
        
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
        
        val s = g.find(d12, b(1, 2))
        
        List(d02, d12) foreach (s must contain(_))
        List(d00, d10, d11) foreach (s must not contain(_))
      }
      
      "correctly find a board full of matching dice" in {
        val dice = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
        
        val s = g.find(dice(4), b(1, 1))
        
        dice foreach (s must contain(_))
      }
    }
    
    "with 2 players involved" >> {
      "not grant a player control over a dice at the floor level " +
      "that is already controlled by another player" in {
        placePlayer(p1, (1, 1))
        placePlayer(p2, (1, 1))
        val d1 = placeDice(1, 1)
        
        p1.requestControl() mustEqual Some(d1)
        p2.requestControl() must beNone
      }
      
      "not grant a player control over a dice at the raised level " +
      "that is already controlled by another player" in {
        placePlayer(p1, (1, 1))
        placePlayer(p2, (1, 1))
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        
        p1.requestControl() mustEqual Some(d1)
        p2.requestControl() must beNone
      }
      
      

    }
  }
}
