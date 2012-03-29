/*
 * Copyright (c) 2009-2012 Andreas Flierl
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

package ecidice
package model
package mode

import ecidice.UnitSpec
import Direction._
import Transform._
import org.specs2.matcher.Matcher
import ModelTestHelpers._

class AnyModeWithMovementSpec[A <: Mode[A] with Movement[A]](game: A) extends UnitSpec {
  
  "Any mode with player and dice movement" should {
    val center = Tile(1, 1)
    val die = Die.random
    
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
            "assignment of player 1" must be equalTo
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
        val initial = game.spawnPlayer(corner)
        
        ((dir: Direction.Value) => dir must beAllowedFrom(initial, corner).iff(allowed contains dir)) foreach Direction.values
      }
    }
    
    "correctly handle player movement at the board edge" in {
      
      "edge position" | "disallowed movement direction" |>
      Tile(1, 0)      ! Forward                         |
      Tile(0, 1)      ! Left                            |
      Tile(1, 2)      ! Backward                        |
      Tile(2, 1)      ! Right                           | {
        
      (pos, disallowed) =>
        val initial = game.spawnPlayer(pos)
        
        ((dir: Direction.Value) => dir must beAllowedFrom(initial, pos).iff(dir != disallowed)) foreach Direction.values
      }
    }
    
    "allow a player to move in any direction with a floor die from the center" in {
      ((somewhere: Direction.Value) => {
        val origin = center.floor
        val destination = center.look(somewhere).floor
        val transform = Transform(origin, destination, somewhere)
      
        val testGame = game.spawnPlayer(origin.tile)
                           .addSolidDie(origin -> die)
                           .control(Player(1))
                           .move(Player(1), somewhere, now)
        
        check(DieMovement(die, origin, destination, transform, Player(1), now), testGame)
      }) foreach Direction.values
    }
    
    "allow a player to move in any direction with an upper die from the center" in {
      ((somewhere: Direction.Value) => {
        val origin = center.raised
        val destination = center.look(somewhere).floor
        val transform = Transform(origin, destination, somewhere)
        
        val testGame = game.spawnPlayer(origin.tile)
                           .addSolidDie(origin.floor -> Die.random)
                           .addSolidDie(origin -> die)
                           .control(Player(1))
                           .move(Player(1), somewhere, now)
        
        check(DieMovement(die, origin, destination, transform, Player(1), now), testGame)
      }) foreach Direction.values
    }
    
    "allow a player to move with a die from the floor onto another die" in {
      val origin = center.floor
      val destination = center.look(Backward).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDie(origin -> die)
                         .addSolidDie(destination.floor -> Die.random)
                         .control(Player(1))
                         .move(Player(1), Backward, now)
      
      check(DieMovement(die, origin, destination, FlipUpOrDown, Player(1), now), testGame)
    }
    
    "allow a player to grab the upper of 2 dice and move onto another die" in {
      val origin = center.raised
      val destination = center.look(Left).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDie(origin.floor -> Die.random)
                         .addSolidDie(origin -> die)
                         .addSolidDie(destination.floor -> Die.random)
                         .control(Player(1))
                         .move(Player(1), Left, now)
      
      check(DieMovement(die, origin, destination, RotateLeft, Player(1), now), testGame)
    }
    
    "allow a player to grab the upper of 2 dice and move to an empty tile" in {
      val origin = center.raised
      val destination = center.look(Right).floor
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDie(origin.floor -> Die.random)
                         .addSolidDie(origin -> die)
                         .control(Player(1))
                         .move(Player(1), Right, now)
      
      check(DieMovement(die, origin, destination, FlipLeftOrRight, Player(1), now), testGame)
    }
    
    "allow a player to move onto an appearing die" in {
      val origin = center.floor
      val destination = center.look(Left).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDie(origin -> die)
                         .spawnDie(destination.tile, now)
                         .control(Player(1))
                         .move(Player(1), Left, now)
      
      check(DieMovement(die, origin, destination, FlipLeftOrRight, Player(1), now), testGame)
    }
    
    "allow a player to move onto a charging die" in {
      val origin = center.floor
      val destination = center.look(Forward).raised
      
      val testGame = game.spawnPlayer(origin.tile)
                         .addSolidDie(origin -> die)
                         .addChargeGroup(threeOnTop, Set(
                             Tile(0, 0), Tile(1, 0), Tile(2, 0)))
                         .control(Player(1))
                         .move(Player(1), Forward, now)
      
      check(DieMovement(die, origin, destination, FlipUpOrDown, Player(1), now), testGame)
    }
    
    "not let a player move with a die from the floor onto a " +
    "die that is controlled by another player" in {
      val before = game.spawnPlayer(Tile(1, 1))
                       .addSolidDie(Tile(1, 1).floor)
                       .control(Player(1))
                       .spawnPlayer(Tile(2, 1))
                       .addSolidDie(Tile(2, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(1), Right, now)
      
      after aka "after move request" must be equalTo before
    }
    
    "not let a player move with a die from the raised level " +
    "onto a die that is controlled by another player" in {
      val before = game.spawnPlayer(Tile(1, 1))
                       .addSolidDie(Tile(1, 1).floor)
                       .addSolidDie(Tile(1, 1).raised)
                       .control(Player(1))
                       .spawnPlayer(Tile(2, 1))
                       .addSolidDie(Tile(2, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(1), Right, now)
      
      after aka "after move request" must be equalTo before
    }
    
    "not let 2 players move onto the same floor space" in {
      val before = game.spawnPlayer(Tile(0, 2))
                       .addSolidDie(Tile(0, 2).floor)
                       .control(Player(1))
                       .move(Player(1), Right, now)
                       .spawnPlayer(Tile(1, 1))
                       .addSolidDie(Tile(1, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(2), Backward, now)
      
      after aka "after move request" must be equalTo before
    }
    
    "not let 2 players move onto the same raised space" in {
      val before = game.addSolidDie(Tile(1, 2).floor)
                       .spawnPlayer(Tile(0, 2))
                       .addSolidDie(Tile(0, 2).floor)
                       .control(Player(1))
                       .move(Player(1), Right, now)
                       .spawnPlayer(Tile(1, 1))
                       .addSolidDie(Tile(1, 1).floor)
                       .control(Player(2))

      val after = before.move(Player(2), Backward, now)
      
      after aka "after move request" must be equalTo before
    }
  }
  
  def check(move: DieMovement, testGame: A) = {
    val p = move.controller
    
    (
      testGame.players(p) aka "assignment of player " + p.id must be equalTo MovingWithADie(move, false)
    ) and (
      testGame.board(move.origin)      aka "contents of origin"      must be equalTo move
    ) and (
      testGame.board(move.destination) aka "contents of destination" must be equalTo move
    )
  }
  
  def beAllowedFrom(initial: A, t: Tile): Matcher[Direction.Value] = 
    ((dir: Direction.Value) => initial.move(Player(1), dir, now) != initial, 
     (dir: Direction.Value) => dir + " is not allowed from " + t)
  
  def threeOnTop = Die.default.transform(RotateForward)
}
object AnyModeWithMovementSpec {
  def apply[A <: Mode[A] with Movement[A]]()(implicit game: A) = 
    new AnyModeWithMovementSpec(game)
}
