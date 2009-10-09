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
 * 
 * 
 * @author Andreas Flierl
 */
class MovementRefereeSpec extends SpecBase with GameContexts {
  def referee = game.movementReferee
  
  "The movement referee" ->-(simpleGame) should {
 
    "allow a player in the center to move in all directions" in {
      Direction.elements.foreach((dir) => within (simpleGame) {
        placePlayer(p1, (1, 1))
        
        val request = "movement request: " + dir
        game.movementReferee.requestMove(p1, dir) aka request must beTrue
        
        p1.state must haveClass[Player.Moving]
      })
    }
    
    "correctly handle player movement in the corners" in {
      
      "corner position" | "allowed movement directions"     |>
      (0, 0)            ! (Direction.UP, Direction.RIGHT)   |
      (2, 0)            ! (Direction.UP, Direction.LEFT)    |
      (0, 2)            ! (Direction.DOWN, Direction.RIGHT) |
      (2, 2)            ! (Direction.DOWN, Direction.LEFT)  | {  
        
      (corner, allowed) =>
        for (dir <- Direction.elements) {
          placePlayer(p1, corner)
          game.movementReferee.requestMove(p1, dir) must be (dir == allowed._1 || dir == allowed._2)
        }
      }
    }
    
    "correctly handle player movement at the board edge" in {
      
      "edge position" | "disallowed movement direction" |>
      (1, 0)          ! Direction.DOWN                  |
      (0, 1)          ! Direction.LEFT                  |
      (1, 2)          ! Direction.UP                    |
      (2, 1)          ! Direction.RIGHT                 | {
        
      (pos, disallowed) =>
        for (dir <- Direction.elements) {
          placePlayer(p1, pos)
          val correct = (dir != disallowed)
          game.movementReferee.requestMove(p1, dir) must be (correct)
        }
      }
      
    }
    
    "allow a player to move in any direction with a floor dice from the center" in {
      for (somewhere <- Direction.elements) within (simpleGame) {
        placePlayer(p1, (1, 1))
        val d1 = placeDice(1, 1)
        
        game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
        game.movementReferee.requestMove(p1, somewhere) aka "move " + somewhere must beTrue
      }
    }
    
    "allow a player to move in any direction with an upper dice from the center" in {
      for (somewhere <- Direction.elements) {
        placePlayer(p1, (1, 1))
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        
        game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
        game.movementReferee.requestMove(p1, somewhere) aka "move " + somewhere must beTrue
      }
    }
    
    "allow a player to move with a dice from the floor onto another dice" in {
      placePlayer(p1, (1, 1))
      val d1 = placeDice(1, 1)
      val d2 = placeDice(1, 2)
      
      game.controlReferee.requestControl(p1) mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.UP) must beTrue
      
      val m = Movement(d1, board(1,1).floor, board(1,2).raised, 
                       Timespan(game.clock, Game.MOVE_DURATION), 
                       Transform.FLIP_UP_OR_DOWN)
      
      d1.state mustEqual Dice.Moving(m, p1) 
      board(1,1).floor.content mustEqual m
      board(1,2).raised.content mustEqual m
    }
    
    "allow a player to grab the upper of 2 dice and move onto another dice" in {
      placePlayer(p1, (1, 1))
      placeDice(1, 1)
      val d1 = placeDice(1, 1)
      val d2 = placeDice(0, 1)
      
      game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.LEFT) aka "movement request" must beTrue
      
      val m = Movement(d1, board(1,1).raised, board(0,1).raised, 
                       Timespan(game.clock, Game.MOVE_DURATION),
                       Transform.ROTATE_LEFT)
      
      d1.state aka "dice state" mustEqual Dice.Moving(m, p1) 
      board(1,1).raised.content aka "start content" mustEqual m
      board(0,1).raised.content aka "destination content" mustEqual m
    }
    
    "allow a player to grab the upper of 2 dice and move to an empty tile" in {
      placePlayer(p1, (1, 1))
      placeDice(1, 1)
      val d1 = placeDice(1, 1)
      
      game.controlReferee.requestControl(p1) aka "control request" mustEqual Some(d1)
      game.movementReferee.requestMove(p1, Direction.RIGHT) aka "movement request" must beTrue
      
      val m = Movement(d1, board(1,1).raised, board(2,1).floor, 
                       Timespan(game.clock, Game.MOVE_DURATION),
                       Transform.FLIP_LEFT_OR_RIGHT)

      d1.state aka "dice state" mustEqual Dice.Moving(m, p1) 
      board(1,1).raised.content aka "start content" mustEqual m
      board(2,1).floor.content aka "destination content" mustEqual m
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
      game.movementReferee.requestMove(p2, Direction.UP) must beFalse
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
      
      game.movementReferee.requestMove(p1, Direction.UP) must beTrue
      game.movementReferee.requestMove(p2, Direction.LEFT) must beFalse
    }
  }
}
