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

/**
 * Informal specification of any game mode that supports control requests.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithSpawningOfDiceSpec[A <: Mode[A] with SpawningOfDice[A]](game: A) extends UnitSpec {
  
  "Any mode that supports the spawning of dice" should {

    "spawn a new dice on an empty tile" in {
      val tile = Tile(1, 2)
      val dice = Dice(6, 5, 4)
      
      val contents = game.spawnDice(tile, now, dice)
                         .board(tile.floor)
                         
      contents aka "contents" must be equalTo DiceAppearing(dice, tile.floor, now)
    }
    
    "not spawn a dice on a tile with an appearing dice" in {
      val tile = Tile(1, 0)
      val before = game.spawnDice(tile, now)
      val after = before.spawnDice(tile, now)
      
      after must be equalTo before
    }
    
    "not spawn a dice on a tile with a solid dice" in {
      val tile = Tile(1, 0)
      val before = game.addSolidDice(tile.floor)
      val after = before.spawnDice(tile, now)
      
      after must be equalTo before
    }
    
    "not spawn a dice on a tile with two solid dice" in {
      val tile = Tile(1, 0)
      val before = game.addSolidDice(tile.floor)
                       .addSolidDice(tile.raised)
      val after = before.spawnDice(tile, now)
      
      after must be equalTo before
    }
    
    //TODO specify interaction with movement
  }
}
object AnyModeWithSpawningOfDiceSpec {
  def apply[A <: Mode[A] with SpawningOfDice[A]]()(implicit game: A) = 
    new AnyModeWithSpawningOfDiceSpec(game)
}
