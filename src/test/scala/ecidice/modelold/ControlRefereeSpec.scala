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

package ecidice.modelold

import ecidice.SpecBase
import org.specs.ScalaCheck

/**
 * Informal specification of the control request referee.
 * 
 * @author Andreas Flierl
 */
object ControlRefereeSpec extends SpecBase with GameContexts with ScalaCheck {
  def referee = game.controlReferee
  
  "A control referee" when inASimpleGame should {  
    
    "not grant control on any tile when the board is empty" in {
      for (x <- 0 to 2; y <- 0 to 2) {
        placePlayer(p1, (x, y))
        
        val request = "control request on (%d, %d)".format(x, y)
        referee.requestControl(p1) aka request must be (None)
        
        p1.isStanding must beTrue
        p1.location must be (board(x, y))
      }
    }
    
    "grant control over a single dice on the floor" in {
      placePlayer(p1, (1, 1))
      val d = placeDice(1, 1)
      
      referee.requestControl(p1) mustEqual Some(d)
      
      d.isSolid must beTrue
      d.location must be (board(1, 1).floor)
      d.controller must be (p1)
    }
    
    "grant control over the upper of 2 stacked dice" in {
      placePlayer(p1, (1, 1))
      val d1 = placeDice(1, 1)
      val d2 = placeDice(1, 1)
      
      referee.requestControl(p1) mustEqual Some(d2)
      d2.isSolid must beTrue
      d2.location must be (board(1, 1).raised)
      d2.controller must be (p1)
      d1.isSolid must beTrue
      d1.location must be (board(1, 1).floor)
      d1.isControlled must beFalse
      d1.controller must throwAn[IllegalStateException]
      p1.isController must beTrue
      p1.dice must be (d2)
    }
    
    "not grant control over an appearing dice" in {
      placePlayer(p1, (1, 1))
      game.spawnDice(1, 1) aka "spawning" must beSome[Dice]
      referee.requestControl(p1) aka "control request" must beNone
    }
    
    "not grant a player control over a dice at the floor level " +
    "that is already controlled by another player" in {
      placePlayer(p1, (1, 1))
      placePlayer(p2, (1, 1))
      val d1 = placeDice(1, 1)
      
      referee.requestControl(p1) mustEqual Some(d1)
      referee.requestControl(p2) must beNone
    }
    
    "not grant a player control over a dice at the raised level " +
    "that is already controlled by another player" in {
      placePlayer(p1, (1, 1))
      placePlayer(p2, (1, 1))
      placeDice(1, 1)
      val d1 = placeDice(1, 1)
      
      referee.requestControl(p1) mustEqual Some(d1)
      referee.requestControl(p2) must beNone
    }
  }
}
