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

class AnyModeWithSpawningOfDieSpec[A <: Mode[A] with SpawningOfDie[A]](game: A) extends UnitSpec {
  
  "Any mode that supports the spawning of die" should {

    "spawn a new die on an empty tile" in {
      val tile = Tile(1, 2)
      val die = Die(6, 5, 4)
      
      val contents = game.spawnDie(tile, now, die)
                         .board(tile.floor)
                         
      contents aka "contents" must be equalTo DieAppearing(die, tile.floor, now)
    }
    
    "not spawn a die on a tile with an appearing die" in {
      val tile = Tile(1, 0)
      val before = game.spawnDie(tile, now)
      val after = before.spawnDie(tile, now)
      
      after must be equalTo before
    }
    
    "not spawn a die on a tile with a solid die" in {
      val tile = Tile(1, 0)
      val before = game.addSolidDie(tile.floor)
      val after = before.spawnDie(tile, now)
      
      after must be equalTo before
    }
    
    "not spawn a die on a tile with two solid die" in {
      val tile = Tile(1, 0)
      val before = game.addSolidDie(tile.floor)
                       .addSolidDie(tile.raised)
      val after = before.spawnDie(tile, now)
      
      after must be equalTo before
    }
    
    //TODO specify interaction with movement
  }
}
object AnyModeWithSpawningOfDieSpec {
  def apply[A <: Mode[A] with SpawningOfDie[A]]()(implicit game: A) = new AnyModeWithSpawningOfDieSpec(game)
}
