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
import scalaz.Success

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
          val testGame = for {
            g1 <- game spawnPlayer Tile(1, 1)
            g2 <- g1 move(Player(1), direction, now)
          } yield g2
                             
          testGame.map(_.players(Player(1))) aka
            "assignment of player 1" must succeedWith(
            MovingAlone(PlayerMovement(Player(1), center, destination, now)))
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
        val Success(initial) = game spawnPlayer corner
        
        foreach (Direction values) { dir => 
          dir must beAllowedFrom(initial, corner) iff (allowed contains dir)
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
        val Success(initial) = game spawnPlayer pos
        
        foreach (Direction values) { dir =>
          dir must beAllowedFrom(initial, pos) iff (dir != disallowed)
        }
      }
    }
    
    "allow a player to move in any direction with a floor die from the center" in {
      foreach (Direction values) { somewhere =>
        val origin = center.floor
        val destination = center.look(somewhere).floor
        val transform = Transform(origin, destination, somewhere)
      
        val testGame = for {
          g1 <- game spawnPlayer origin.tile
          g2 =  g1 addSolidDie (origin -> die)
          g3 <- g2 control Player(1)
          g4 <- g3 move (Player(1), somewhere, now)
        } yield g4
        
        testGame must moveLike(DieMovement(die, origin, destination, transform, Player(1), now))
      }
    }
    
    "allow a player to move in any direction with an upper die from the center" in {
      foreach (Direction values)  { somewhere =>
        val origin = center.raised
        val destination = center.look(somewhere).floor
        val transform = Transform(origin, destination, somewhere)
        
        val testGame = for {
          g1 <- game spawnPlayer origin.tile
          g2 =  g1 addSolidDie (origin.floor -> Die.random)
          g3 =  g2 addSolidDie (origin -> die)
          g4 <- g3 control Player(1)
          g5 <- g4 move (Player(1), somewhere, now)
        } yield g5
        
        testGame must moveLike(DieMovement(die, origin, destination, transform, Player(1), now))
      }
    }
    
    "allow a player to move with a die from the floor onto another die" in {
      val origin = center.floor
      val destination = center.look(Backward).raised
      
      val testGame = for {
        g1 <- game spawnPlayer origin.tile
        g2 =  g1 addSolidDie (origin -> die)
        g3 =  g2 addSolidDie (destination.floor -> Die.random)
        g4 <- g3 control Player(1)
        g5 <- g4 move (Player(1), Backward, now)
      } yield g5
      
      testGame must moveLike(DieMovement(die, origin, destination, FlipUpOrDown, Player(1), now))
    }
    
    "allow a player to grab the upper of 2 dice and move onto another die" in {
      val origin = center.raised
      val destination = center.look(Left).raised
      
      val testGame = for {
        g1 <- game spawnPlayer origin.tile
        g2 =  g1 addSolidDie (origin.floor -> Die.random)
        g3 =  g2 addSolidDie (origin -> die)
        g4 =  g3 addSolidDie (destination.floor -> Die.random)
        g5 <- g4 control Player(1)
        g6 <- g5 move (Player(1), Left, now)
      } yield g6
      
      testGame must moveLike(DieMovement(die, origin, destination, RotateLeft, Player(1), now))
    }
    
    "allow a player to grab the upper of 2 dice and move to an empty tile" in {
      val origin = center.raised
      val destination = center.look(Right).floor
      
      val testGame = for {
        g1 <- game spawnPlayer origin.tile
        g2 =  g1 addSolidDie (origin.floor -> Die.random)
        g3 =  g2 addSolidDie (origin -> die)
        g4 <- g3 control Player(1)
        g5 <- g4 move (Player(1), Right, now)
      } yield g5

      testGame must moveLike(DieMovement(die, origin, destination, FlipLeftOrRight, Player(1), now))
    }
    
    "allow a player to move onto an appearing die" in {
      val origin = center.floor
      val destination = center.look(Left).raised
      
      val testGame = for {
        g1 <- game spawnPlayer origin.tile
        g2 =  g1 addSolidDie (origin -> die)
        g3 <- g2 spawnDie (destination.tile, now)
        g4 <- g3 control Player(1)
        g5 <- g4 move (Player(1), Left, now)
      } yield g5
      
      testGame must moveLike(DieMovement(die, origin, destination, FlipLeftOrRight, Player(1), now))
    }
    
    "allow a player to move onto a charging die" in {
      val origin = center.floor
      val destination = center.look(Forward).raised
      
      val testGame = for { 
        g1 <- game spawnPlayer origin.tile
        g2 =  g1 addSolidDie (origin -> die)
        g3 =  g2 addChargeGroup(threeOnTop, Set(Tile(0, 0), Tile(1, 0), Tile(2, 0)))
        g4 <- g3 control Player(1)
        g5 <- g4 move (Player(1), Forward, now)
      } yield g5
      
      testGame must moveLike(DieMovement(die, origin, destination, FlipUpOrDown, Player(1), now))
    }
    
    "not let a player move with a die from the floor onto a " +
    "die that is controlled by another player" in {
      val request = for {
        g1 <- game spawnPlayer Tile(1, 1)
        g2 =  g1 addSolidDie Tile(1, 1).floor
        g3 <- g2 control Player(1)
        g4 <- g3 spawnPlayer Tile(2, 1)
        g5 =  g4 addSolidDie Tile(2, 1).floor
        g6 <- g5 control Player(2)
        g7 <- g6 move (Player(1), Right, now)
      } yield g7
      
      request must fail
    }
    
    "not let a player move with a die from the raised level " +
    "onto a die that is controlled by another player" in {
      val request = for { 
        g1 <- game spawnPlayer Tile(1, 1)
        g2 =  g1 addSolidDie Tile(1, 1).floor
        g3 =  g2 addSolidDie Tile(1, 1).raised
        g4 <- g3 control Player(1)
        g5 <- g4 spawnPlayer Tile(2, 1)
        g6 =  g5 addSolidDie Tile(2, 1).floor
        g7 <- g6 control Player(2)
        g8 <- g7 move (Player(1), Right, now)
      } yield g8
      
      request must fail
    }
    
    "not let 2 players move onto the same floor space" in {
      val request = for {
        g1 <- game spawnPlayer Tile(0, 2)
        g2 =  g1 addSolidDie Tile(0, 2).floor
        g3 <- g2 control Player(1)
        g4 <- g3 move (Player(1), Right, now)
        g5 <- g4 spawnPlayer Tile(1, 1)
        g6 =  g5 addSolidDie Tile(1, 1).floor
        g7 <- g6 control Player(2)
        g8 <- g7 move (Player(2), Backward, now)
      } yield g8
      
      request must fail
    }
    
    "not let 2 players move onto the same raised space" in {
      val request = for {
        g1 <- game spawnPlayer Tile(0, 2)
        g2 =  g1 addSolidDie Tile(1, 2).floor
        g3 =  g2 addSolidDie Tile(0, 2).floor
        g4 <- g3 control Player(1)
        g5 <- g4 move (Player(1), Right, now)
        g6 <- g5 spawnPlayer Tile(1, 1)
        g7 =  g6 addSolidDie(Tile(1, 1).floor)
        g8 <- g7 control Player(2)
        g9 <- g8 move(Player(2), Backward, now)
      } yield g9
      
      request must fail
    }
  }
  
  def moveLike(move: DieMovement) =
    (succeedWith[Assignment](MovingWithADie(move, false)) ^^ ((_: Valid[A]).map(_.players(move.controller)) aka "assignment of " + move.controller)) and
    (succeedWith[Contents](move)                          ^^ ((_: Valid[A]).map(_.board(move.origin))       aka "contents of origin")) and
    (succeedWith[Contents](move)                          ^^ ((_: Valid[A]).map(_.board(move.destination))  aka "contents of destination"))
  
  def beAllowedFrom(initial: A, t: Tile): Matcher[Direction] = 
    ((dir: Direction) => initial.move(Player(1), dir, now) match { 
        case Success(after) if after != initial => true
        case _ => false
      }, 
     (dir: Direction) => dir + " is not allowed from " + t)
  
  def threeOnTop = Die.default.transform(RotateForward)
}
object AnyModeWithMovementSpec {
  def apply[A <: Mode[A] with Movement[A]]()(implicit game: A) = new AnyModeWithMovementSpec(game)
}
