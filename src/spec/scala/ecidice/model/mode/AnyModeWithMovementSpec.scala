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
package mode

import ecidice.SpecBase
import Direction._
import Transform._

/**
 * Informal specification of player and dice movement.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithMovementSpec[A <: Mode[A] with Movement[A]](game: A) 
extends SpecBase with ModelTestHelpers {
  
  "Any mode with player and dice movement" should {
    val center = Tile(1, 1)
    val dice = Dice.random
    
    "allow a player in the center to move in all directions" in {
      "direction" | "destination" |>
      Backward    ! Tile(1, 2)    |
      Right       ! Tile(2, 1)    |
      Forward     ! Tile(1, 0)    |
      Left        ! Tile(0, 1)    | {
        
        (direction, destination) => {
          val testGame = game.spawnPlayer(Tile(1, 1))
                             .move(Player(1), direction, now)
                             
          testGame.players(Player(1)) aka
            "assignment of player 1" mustEqual
            MovingAlone(PlayerMovement(Player(1), center, destination, now))
        }
      }
    }
    
    "correctly handle player movement in the corners" in {

      "corner position" | "allowed movement directions" |>
      Tile(0, 0)        ! Set(Backward, Right)          |
      Tile(2, 0)        ! Set(Backward, Left)           |
      Tile(0, 2)        ! Set(Forward, Right)           |
      Tile(2, 2)        ! Set(Forward, Left)            | {  
        
      (corner, allowed) => 
        for (dir <- Direction.values) {
          "player moving " + dir in {
            val before = game.spawnPlayer(corner)
            val after = before.move(Player(1), dir, now)
            
            if (allowed.contains(dir)) after must !=(before)
            else after must ==(before)
          }
        }
      }
    }
    
    "correctly handle player movement at the board edge" in {
      
      "edge position" | "disallowed movement direction" |>
      Tile(1, 0)      ! Forward                         |
      Tile(0, 1)      ! Left                            |
      Tile(1, 2)      ! Backward                        |
      Tile(2, 1)      ! Right                           | {
        
      (pos, disallowed) =>
        for (dir <- Direction.values) {
          "player moving " + dir in {
            val before = game.spawnPlayer(pos)
            val after = before.move(Player(1), dir, now)
            
            if (disallowed == dir) after must ==(before)
            else after must !=(before)
          }
        }
      }
    }
    
    "allow a player to move in any direction with a floor dice from the center" in {
      for (somewhere <- Direction.values) {
        "from the center " + somewhere in {
          val origin = center.floor
          val destination = center.look(somewhere).floor
          val transform = Transform(origin, destination, somewhere)
        
          val testGame = game.spawnPlayer(origin.tile)
                             .addSolidDice(origin -> dice)
                             .control(Player(1))
                             .move(Player(1), somewhere, now)
          
          check(DiceMovement(dice, origin, destination, transform, Player(1), now), testGame)
        }
      }
    }
    
    "allow a player to move in any direction with an upper dice from the center" in {
      for (somewhere <- Direction.values) {
        "from the center " + somewhere in {
          val origin = center.raised
          val destination = center.look(somewhere).floor
          val transform = Transform(origin, destination, somewhere)
          
          val testGame = game.spawnPlayer(origin.tile)
                             .addSolidDice(origin.floor -> Dice.random)
                             .addSolidDice(origin -> dice)
                             .control(Player(1))
                             .move(Player(1), somewhere, now)
          
          check(DiceMovement(dice, origin, destination, transform, Player(1), now), testGame)
        }
      }
    }
    
    "allow a player to move with a dice from the floor onto another dice" in {
      val origin = center.floor
      val destination = center.look(Backward).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDice(origin -> dice)
                         .addSolidDice(destination.floor -> Dice.random)
                         .control(Player(1))
                         .move(Player(1), Backward, now)
      
      check(DiceMovement(dice, origin, destination, FlipUpOrDown, Player(1), now), testGame)
    }
    
    "allow a player to grab the upper of 2 dice and move onto another dice" in {
      val origin = center.raised
      val destination = center.look(Left).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDice(origin.floor -> Dice.random)
                         .addSolidDice(origin -> dice)
                         .addSolidDice(destination.floor -> Dice.random)
                         .control(Player(1))
                         .move(Player(1), Left, now)
      
      check(DiceMovement(dice, origin, destination, RotateLeft, Player(1), now), testGame)
    }
    
    "allow a player to grab the upper of 2 dice and move to an empty tile" in {
      val origin = center.raised
      val destination = center.look(Right).floor
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDice(origin.floor -> Dice.random)
                         .addSolidDice(origin -> dice)
                         .control(Player(1))
                         .move(Player(1), Right, now)
      
      check(DiceMovement(dice, origin, destination, FlipLeftOrRight, Player(1), now), testGame)
    }
    
    "allow a player to move onto an appearing dice" in {
      val origin = center.floor
      val destination = center.look(Left).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDice(origin -> dice)
                         .spawnDice(destination.tile, now)
                         .control(Player(1))
                         .move(Player(1), Left, now)
      
      check(DiceMovement(dice, origin, destination, FlipLeftOrRight, Player(1), now), testGame)
    }
    
    "allow a player to move onto a charging dice" in {
      val origin = center.floor
      val destination = center.look(Forward).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDice(origin -> dice)
                         .addChargeGroup(threeOnTop, Set(
                             Tile(0, 0), Tile(1, 0), Tile(2, 0)))
                         .control(Player(1))
                         .move(Player(1), Forward, now)
      
      check(DiceMovement(dice, origin, destination, FlipUpOrDown, Player(1), now), testGame)
    }
    
    "not let a player move with a dice from the floor onto a " +
    "dice that is controlled by another player" in {
      val before = game.spawnPlayer(Tile(1, 1))
                       .addSolidDice(Tile(1, 1).floor)
                       .control(Player(1))
                       .spawnPlayer(Tile(2, 1))
                       .addSolidDice(Tile(2, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(1), Right, now)
      
      after aka "after move request" mustEqual before
    }
    
    "not let a player move with a dice from the raised level " +
    "onto a dice that is controlled by another player" in {
      val before = game.spawnPlayer(Tile(1, 1))
                       .addSolidDice(Tile(1, 1).floor)
                       .addSolidDice(Tile(1, 1).raised)
                       .control(Player(1))
                       .spawnPlayer(Tile(2, 1))
                       .addSolidDice(Tile(2, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(1), Right, now)
      
      after aka "after move request" mustEqual before
    }
    
    "not let 2 players move onto the same floor space" in {
      val before = game.spawnPlayer(Tile(0, 2))
                       .addSolidDice(Tile(0, 2).floor)
                       .control(Player(1))
                       .move(Player(1), Right, now)
                       .spawnPlayer(Tile(1, 1))
                       .addSolidDice(Tile(1, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(2), Backward, now)
      
      after aka "after move request" mustEqual before
    }
    
    "not let 2 players move onto the same raised space" in {
      val before = game.addSolidDice(Tile(1, 2).floor)
                       .spawnPlayer(Tile(0, 2))
                       .addSolidDice(Tile(0, 2).floor)
                       .control(Player(1))
                       .move(Player(1), Right, now)
                       .spawnPlayer(Tile(1, 1))
                       .addSolidDice(Tile(1, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(2), Backward, now)
      
      after aka "after move request" mustEqual before
    }
  }
  
  def check(move: DiceMovement, testGame: A) = {
    val p = move.controller
    
    testGame.players(p) aka
          "assignment of player " + p.id mustEqual MovingWithADice(move, false)
        
    testGame.board(move.origin)      aka "contents of origin"      mustEqual move
    testGame.board(move.destination) aka "contents of destination" mustEqual move
  }
  
  def threeOnTop = Dice.default.transform(RotateForward)
}
