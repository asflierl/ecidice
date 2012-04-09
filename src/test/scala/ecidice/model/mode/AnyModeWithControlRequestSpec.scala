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
import ModelTestHelpers._
import scalaz.Success
import scalaz.Failure

class AnyModeWithControlRequestSpec[A <: Mode[A] with ControlRequest[A]](game: A) extends UnitSpec {
  
  "Any mode that allows a player to control a die" should {

    "not grant control when player stands on an empty tile" in {
      val request = for {
        gameWithOnePlayer <- game spawnPlayer Tile(0, 0)
        gameWithController <- gameWithOnePlayer control Player(1)
      } yield gameWithController players Player(1)
      
      request must fail
    }
    
    "not grant control over an appearing die" in {
      val request = for {
        g1 <- game spawnPlayer Tile(0, 0)
        g2 <- g1 spawnDie (Tile(0, 0), now)
        g3 <- g2 control Player(1)
      } yield g3
      
      request must fail
    }

    "grant control over a single die on the floor" in {
      val die = Die.random
      val location = Tile(0, 0).floor

      val testGame = for {
        g1 <- game spawnPlayer location.tile
        g2 =  g1 addSolidDie (location -> die)
        g3 <- g2 control Player(1)
      } yield g3
      
      testGame.map(_.players(Player(1))) aka 
        "assignment of player 1" must succeedWith(ControllingADie(location))
      
      testGame.map(_.board(location)) aka
        "contents of dice space" must succeedWith(SolidControlled(die, Player(1)))
    }
    
    "not grant control over dice on the floor level already controlled by another player" in {
      val die = Die.random
      val location = Tile(0, 0).floor
      
      val testGame = for {
        g1 <- game spawnPlayer location.tile
        g2 <- g1 spawnPlayer location.tile
        g3 =  g2 addSolidDie (location -> die)
        g4 <- g3 control Player(1)
        g5 <- g4 control Player(2)
      } yield g5
      
      testGame must fail
    }
    
    "grant control over the upper of 2 stacked dice" in {
      val lowerDie, upperDie = Die.random
      val tile = Tile(0, 0)
      
      val testGame = for {
        g1 <- game.spawnPlayer(tile)
        g2 =  g1.addSolidDie(tile.floor -> lowerDie)
        g3 =  g2.addSolidDie(tile.raised -> upperDie)
        g4 <- g3.control(Player(1))
      } yield g4
      
      testGame.map(_.players(Player(1))) aka 
        "assignment of player 1" must succeedWith(ControllingADie(tile.raised))
      
      testGame.map(_.board(tile.floor)) aka
        "contents of floor space" must succeedWith(lowerDie)
      
      testGame.map(_.board(tile.raised)) aka
        "contents of raised space" must succeedWith(SolidControlled(upperDie, Player(1)))
    }
    
    "not grant control over dice on the raised level already controlled by another player" in {
      val die = Die.random
      val location = Tile(0, 0).raised
      
      val testGame = for {
        g1 <- game.spawnPlayer(location.tile)
        g2 <- g1.spawnPlayer(location.tile)
        g3 =  g2.addSolidDie(location.floor -> Die.random)
        g4 =  g3.addSolidDie(location -> die)
        g5 <- g4.control(Player(2))
        g6 <- g5.control(Player(1))
      } yield g6
      
      testGame must fail
    }
    
    //TODO specifiy movement cases
  }
}

object AnyModeWithControlRequestSpec {
  def apply[A <: Mode[A] with ControlRequest[A]]()(implicit game: A) = new AnyModeWithControlRequestSpec(game)
}
