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

/**
 * Informal specification of any game mode that supports dice spawning.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithControlRequestSpec[A <: Mode[A] with ControlRequest[A]](game: A) 
extends SpecBase with TestHelpers {
  
  "Any mode that supports control requests" should {

    "not grant control when player stands on an empty tile" in {
      val gameWithOnePlayer = game.spawnPlayer(Tile(0, 0))
      val assignment = gameWithOnePlayer.control(Player(1)).players(Player(1))
      
      assignment mustEqual Standing(Tile(0, 0))
    }
    
    "not grant control over an appearing dice" in {
      val before = game.spawnPlayer(Tile(0, 0))
                       .spawnDice(Tile(0, 0), now)
                       
      val after = before.control(Player(1))
      
      after mustEqual before
    }
    
    "grant control over a single dice on the floor" in {
      val dice = Dice.random
      val location = Tile(0, 0).floor

      val testGame = game.spawnPlayer(location.tile)
                         .addSolidDice(location -> dice)
                         .control(Player(1))
      
      testGame.players(Player(1)) aka 
        "assignment of player 1" mustEqual ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" mustEqual SolidControlled(dice, Player(1))
    }
    
    "not grant control over dice already controlled by another player" in {
      val dice = Dice.random
      val location = Tile(0, 0).floor
      
      val testGame = game.spawnPlayer(location.tile)
                         .spawnPlayer(location.tile)
                         .addSolidDice(location -> dice)
                         .control(Player(1))
                         .control(Player(2))
      
      testGame.players(Player(2)) aka
        "assignment of player 2" mustEqual Standing(location.tile)
                         
      testGame.players(Player(1)) aka 
        "assignment of player 1" mustEqual ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" mustEqual SolidControlled(dice, Player(1))
    }
    
    "not grant control over any of 2 stacked dice" in {
      val tile = Tile(0, 0)
      
      val before = game.spawnPlayer(tile)
                       .addSolidDice(tile.floor -> Dice.random)
                       .addSolidDice(tile.raised -> Dice.random)
            
      val after = before.control(Player(1))
      
      after mustEqual before
    }
  }
}
object AnyModeWithControlRequestSpec {
  def apply[A <: Mode[A] with ControlRequest[A]]()(implicit game: A) = 
    new AnyModeWithControlRequestSpec(game)
}
