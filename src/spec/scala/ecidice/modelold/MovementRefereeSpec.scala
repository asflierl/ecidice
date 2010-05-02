/*
 * Copyright (c) 2009-2010, Andreas Flierl
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
 * - Neither the names of the copyright holders nor the names of the
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

package ecidice.modelold

import ecidice.SpecBase

/**
 * 
 * 
 * @author Andreas Flierl
 */
object MovementRefereeSpec extends SpecBase with GameContexts {
  def referee = game.movementReferee
  
  "The movement referee" when inASimpleGame should {
 
    "allow a player in the center to move in all directions" in {
      for (dir <- Direction.values) {
        "from the center " + dir >> {
          placePlayer(p1, (1, 1))
        
          val request = "movement request: " + dir
          game.movementReferee.requestMove(p1, dir) aka request must beTrue
          
          p1.isMoving must beTrue
        }
      }
    }
    
    "correctly handle player movement in the corners" in {
      inASimpleGame     |
      "corner position" | "allowed movement directions"              |>
      (0, 0)            ! Set(Direction.BACKWARD, Direction.RIGHT)   |
      (2, 0)            ! Set(Direction.BACKWARD, Direction.LEFT)    |
      (0, 2)            ! Set(Direction.FORWARD, Direction.RIGHT)    |
      (2, 2)            ! Set(Direction.FORWARD, Direction.LEFT)     | {  
        
      (corner, allowed) =>
        for (dir <- Direction.values) {
          "player moving " + dir in {
            placePlayer(p1, corner)
            val correct = allowed.contains(dir)
            game.movementReferee.requestMove(p1, dir) must be (correct)
          }
        }
      }
    }
    
    "correctly handle player movement at the board edge" in {
      
      "edge position" | "disallowed movement direction" |>
      (1, 0)          ! Direction.FORWARD               |
      (0, 1)          ! Direction.LEFT                  |
      (1, 2)          ! Direction.BACKWARD              |
      (2, 1)          ! Direction.RIGHT                 | {
        
      (pos, disallowed) =>
        for (dir <- Direction.values) {
          "player moving " + dir in {
            placePlayer(p1, pos)
            val correct = (dir != disallowed)
            game.movementReferee.requestMove(p1, dir) must be (correct)
          }
        }
      }
      
    }
    
    "allow a player to move in any direction with a floor dice from the center" in {
      for (somewhere <- Direction.values) {
        "from the center " + somewhere >> {
          placePlayer(p1, (1, 1))
          val d1 = placeDice(1, 1)
          
          game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
          game.movementReferee.requestMove(p1, somewhere) aka "move " + somewhere must beTrue
          d1.movement.destination.isFloor must beTrue
        }
      }
    }
    
    "allow a player to move in any direction with an upper dice from the center" in {
      for (somewhere <- Direction.values) {
        "from the center " + somewhere >> {
          placePlayer(p1, (1, 1))
          placeDice(1, 1)
          val d1 = placeDice(1, 1)
          
          game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
          game.movementReferee.requestMove(p1, somewhere) aka "move " + somewhere must beTrue
        }
      }
    }
    
    "allow a player to move with a dice from the floor onto another dice" in {
      placePlayer(p1, (1, 1))
      val d1 = placeDice(1, 1)
      val d2 = placeDice(1, 2)
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.BACKWARD) must beTrue
      
      val m = Activity.on(game.clock).diceMovement(d1, board(1, 1).floor,
        board(1, 2).raised, Transform.FLIP_UP_OR_DOWN, p1)
      
      d1.isMoving must beTrue
      d1.movement mustEqual m
      board(1, 1).floor.movement mustEqual m
      board(1, 2).raised.movement mustEqual m
    }
    
    "allow a player to grab the upper of 2 dice and move onto another dice" in {
      placePlayer(p1, (1, 1))
      placeDice(1, 1)
      val d1 = placeDice(1, 1)
      val d2 = placeDice(0, 1)
      
      game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.LEFT) aka "movement request" must beTrue
      
      val m = Activity.on(game.clock).diceMovement(d1, board(1, 1).raised,
        board(0, 1).raised, Transform.ROTATE_LEFT, p1)

      d1.isMoving must beTrue
      d1.movement mustEqual m
      board(1, 1).raised.movement mustEqual m
      board(0, 1).raised.movement mustEqual m
    }
    
    "allow a player to grab the upper of 2 dice and move to an empty tile" in {
      placePlayer(p1, (1, 1))
      placeDice(1, 1)
      val d1 = placeDice(1, 1)
      
      game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.RIGHT) aka "movement request" must beTrue
      
      val m = Activity.on(game.clock).diceMovement(d1, board(1, 1).raised,
        board(2, 1).floor, Transform.FLIP_LEFT_OR_RIGHT, p1)

      d1.movement mustEqual m
      board(1, 1).raised.movement mustEqual m
      board(2, 1).floor.movement mustEqual m
    }
    
    "not allow a player to move onto an appearing dice" in {
      placePlayer(p1, (2, 1))
      val d1 = placeDice(2, 1)
      
      game.spawnDice(1, 1) aka "spawning" must beSome[Dice]
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.LEFT) must beFalse
    }
    
    "not let a player move with a dice from the floor onto a " +
    "dice that is controlled by another player" in {
      placePlayer(p1, (1, 1))
      placePlayer(p2, (2, 1))
      
      val d1 = placeDice(1, 1)
      val d2 = placeDice(2, 1)
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.controlReferee.requestControl(p2) mustEqual Some(d2)
      
      game.movementReferee.requestMove(p2, Direction.LEFT) must beFalse
    }
    
    "not let a player move with a dice from the raised level " +
    "onto a dice that is controlled by another player" in {
      placePlayer(p1, (1, 1))
      placePlayer(p2, (2, 1))
      
      val d1 = placeDice(1, 1)
      placeDice(2, 1)
      val d2 = placeDice(2, 1)
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.controlReferee.requestControl(p2) mustEqual Some(d2)
      
      game.movementReferee.requestMove(p2, Direction.LEFT) must beFalse
    }
    
    "not let 2 players move onto the same floor space" in {
      placePlayer(p1, (0, 2))
      placePlayer(p2, (1, 1))
      
      placeDice(0, 2)
      val d1 = placeDice(0, 2)
      val d2 = placeDice(1, 1)
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.controlReferee.requestControl(p2) mustEqual Some(d2)
      
      game.movementReferee.requestMove(p1, Direction.RIGHT) must beTrue
      game.movementReferee.requestMove(p2, Direction.BACKWARD) must beFalse
    }
    
    "not let 2 players move onto the same raised space" in {
      placePlayer(p1, (0, 0))
      placePlayer(p2, (1, 1))
      
      val d1 = placeDice(0, 0)
      placeDice(1, 1)
      val d2 = placeDice(1, 1)
      
      placeDice(0, 1)
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.controlReferee.requestControl(p2) mustEqual Some(d2)
      
      game.movementReferee.requestMove(p1, Direction.BACKWARD) must beTrue
      game.movementReferee.requestMove(p2, Direction.LEFT) must beFalse
    }
    
    "allow a player to move into a space with a bursting dice" in {
      val group = buildDiceGroup(Set((0, 0), (1, 0)))
      val locations = group.dice.map(_.location)
      val dice = placeDice(1, 1)
      placePlayer(p1, (1, 1))
      game.controlReferee.requestControl(p1)
      
      game.clock.tick(Activity.CHARGE_DURATION)
      game.updateMechanics.update
      
      game.movementReferee.requestMove(p1, Direction.FORWARD) must beTrue
      dice.movement.destination.isBusy must beTrue
      dice.movement.destination.hasBursting must beTrue
      
      game.clock.tick(Activity.BURST_DURATION)
      game.updateMechanics.update

      dice.location.tile mustEqual board(1, 0)
      dice.location.isFloor must beTrue
      dice.location.isBusy must beFalse
      dice.location.isOccupied must beTrue
      dice.location.hasBursting must beFalse
    }
  }
}
