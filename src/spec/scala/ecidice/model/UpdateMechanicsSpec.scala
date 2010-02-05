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

import ecidice.SpecBase

/**
 * Spec-based tests of the game's update mechanics.
 * 
 * @author Andreas Flierl
 */
object UpdateMechanicsSpec extends SpecBase with GameContexts {
  "The update mechanics" ->-(simpleGame) should {
    
    def updater = game.updateMechanics
    
    "make the activity tracker forget finished activities" in {
      val s = board(0, 0)
      val finished = Activity.on(game.clock).playerMovement(p1, s, s)
      game.clock.tick(Activity.MOVE_DURATION)
      val pending = Activity.on(game.clock).playerMovement(p1, s, s)

      game.tracker.track(finished)
      game.tracker.track(pending)
      
      updater.update
      
      game.tracker.activities must contain(pending)
      game.tracker.activities must notContain(finished)
    }
    
    "make a dice solid that's finished appearing" in {
      val dice = game.spawnDice(1, 1).get
      game.clock.tick(Activity.APPEAR_DURATION)
      
      updater.update
      
      dice.isSolid must beTrue
      dice.location must be (board(1, 1).floor)
      dice.isControlled must beFalse
      dice.controller must throwAn[IllegalStateException]
      
      board(1, 1).floor.isOccupied must beTrue
      board(1, 1).floor.dice mustEqual dice
    }
    
    "update a player's state after she finished moving" in {
      placePlayer(p1, (0, 1))
      game.movementReferee.requestMove(p1, Direction.BACKWARD)
      game.clock.tick(Activity.MOVE_DURATION)
      
      updater.update
      
      p1.isStanding must beTrue
      p1.location must be (board(0, 2))
    }
    
    "make a group of dice burst when they're finished charging" in {
      val group = buildDiceGroup(Set((0, 0), (1, 0)))
      game.clock.tick(Activity.CHARGE_DURATION)
      
      updater.update

      group.dice.foreach(dice => {
        dice.isLocked must beTrue
        dice.isBursting must beTrue
        dice.group.dice must contain (dice)
      })
    }
  }
}
