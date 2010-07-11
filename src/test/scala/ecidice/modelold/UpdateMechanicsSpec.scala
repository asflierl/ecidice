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

/**
 * Spec-based tests of the game's update mechanics.
 * 
 * @author Andreas Flierl
 */
object UpdateMechanicsSpec extends SpecBase with GameContexts {
  "The update mechanics" when inASimpleGame should {
    
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
        dice.location.hasBursting must beTrue
      })
      
      game.tracker.activities must notContain (group)
    }
    
    "remove burst dice from the board" in {
      val group = buildDiceGroup(Set((0, 0), (1, 0)))
      val locations = group.dice.map(_.location)
      
      game.clock.tick(Activity.CHARGE_DURATION)
      updater.update
      
      game.clock.tick(Activity.BURST_DURATION)
      updater.update

      group.dice.foreach(dice => dice.isBurst aka "isBurst: " + dice must beTrue)
      locations.foreach(loc => loc.isEmpty aka "isEmpty: " + loc must beTrue)
      locations.foreach(loc => loc.hasBursting aka "hasBursting: " + loc must beFalse)
      
      game.tracker.activities must notContain (group)
    }
  }
}
