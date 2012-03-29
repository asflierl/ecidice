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

class AnyModeWithControlRequestSpec[A <: Mode[A] with ControlRequest[A]](game: A) extends UnitSpec {
  
  "Any mode that allows a player to control a die" should {

    "not grant control when player stands on an empty tile" in {
      val gameWithOnePlayer = game.spawnPlayer(Tile(0, 0))
      val assignment = gameWithOnePlayer.control(Player(1)).players(Player(1))
      
      assignment must be equalTo Standing(Tile(0, 0))
    }
    
    "not grant control over an appearing die" in {
      val before = game.spawnPlayer(Tile(0, 0))
                       .spawnDie(Tile(0, 0), now)
                       
      val after = before.control(Player(1))
      
      after must be equalTo before
    }
    
    "grant control over a single die on the floor" in {
      val die = Die.random
      val location = Tile(0, 0).floor

      val testGame = game.spawnPlayer(location.tile)
                         .addSolidDie(location -> die)
                         .control(Player(1))
      
      testGame.players(Player(1)) aka 
        "assignment of player 1" must be equalTo ControllingADie(location)
      
      testGame.board(location) aka
        "contents of dice space" must be equalTo SolidControlled(die, Player(1))
    }
    
    "not grant control over dice on the floor level already controlled by another player" in {
      val die = Die.random
      val location = Tile(0, 0).floor
      
      val testGame = game.spawnPlayer(location.tile)
                         .spawnPlayer(location.tile)
                         .addSolidDie(location -> die)
                         .control(Player(1))
                         .control(Player(2))
      
      testGame.players(Player(2)) aka
        "assignment of player 2" must be equalTo Standing(location.tile)
                         
      testGame.players(Player(1)) aka 
        "assignment of player 1" must be equalTo ControllingADie(location)
      
      testGame.board(location) aka
        "contents of die space" must be equalTo SolidControlled(die, Player(1))
    }
    
    "grant control over the upper of 2 stacked dice" in {
      val lowerDie, upperDie = Die.random
      val tile = Tile(0, 0)
      
      val testGame = game.spawnPlayer(tile)
                         .addSolidDie(tile.floor -> lowerDie)
                         .addSolidDie(tile.raised -> upperDie)
                         .control(Player(1))
      
      testGame.players(Player(1)) aka 
        "assignment of player 1" must be equalTo ControllingADie(tile.raised)
      
      testGame.board(tile.floor) aka
        "contents of floor space" must be equalTo lowerDie
      
      testGame.board(tile.raised) aka
        "contents of raised space" must be equalTo SolidControlled(upperDie, Player(1))
    }
    
    "not grant control over dice on the raised level already controlled by another player" in {
      val die = Die.random
      val location = Tile(0, 0).raised
      
      val testGame = game.spawnPlayer(location.tile)
                         .spawnPlayer(location.tile)
                         .addSolidDie(location.floor -> Die.random)
                         .addSolidDie(location -> die)
                         .control(Player(2))
                         .control(Player(1))
      
      testGame.players(Player(1)) aka
        "assignment of player 1" must be equalTo Standing(location.tile)
                         
      testGame.players(Player(2)) aka 
        "assignment of player 2" must be equalTo ControllingADie(location)
      
      testGame.board(location) aka
        "contents of dice space" must be equalTo SolidControlled(die, Player(2))
    }
    
    //TODO specifiy movement cases
  }
}

object AnyModeWithControlRequestSpec {
  def apply[A <: Mode[A] with ControlRequest[A]]()(implicit game: A) = 
    new AnyModeWithControlRequestSpec(game)
}
