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
 * Informal specification of player and dice movement.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithMovementSpec[A <: Mode[A] with Movement[A]](game: A) 
extends SpecBase with ModelTestHelpers {
  
  "Any mode with player and dice movement" should {
    "allow a player in the center to move in all directions" in {
      for (dir <- Direction.values) {
//        "from the center " + dir >> {
          val testGame = game.spawnPlayer(Tile(1, 1))
                             .move(Player(1), dir, now)
        
          testGame.players(Player(1)) aka
            "assignment of player 1" must beLike { case MovingAlone(_) => true }
          
          //TODO this could use a data table with destination tiles
//        }
      }
    }
    
    "correctly handle player movement in the corners" in {

      "corner position" | "allowed movement directions"              |>
      Tile(0, 0)        ! Set(Direction.Backward, Direction.Right)   |
      Tile(2, 0)        ! Set(Direction.Backward, Direction.Left)    |
      Tile(0, 2)        ! Set(Direction.Forward, Direction.Right)    |
      Tile(2, 2)        ! Set(Direction.Forward, Direction.Left)     | {  
        
      (corner, allowed) =>
        for (dir <- Direction.values) {
//          "player moving " + dir in {
            val before = game.spawnPlayer(corner)
            val after = before.move(Player(1), dir, now)
            
            if (allowed.contains(dir)) after must !=(before)
            else after must ==(before)
//          }
        }
      }
    }
    
    "correctly handle player movement at the board edge" in {
      
      "edge position" | "disallowed movement direction" |>
      Tile(1, 0)      ! Direction.Forward               |
      Tile(0, 1)      ! Direction.Left                  |
      Tile(1, 2)      ! Direction.Backward              |
      Tile(2, 1)      ! Direction.Right                 | {
        
      (pos, disallowed) =>
        for (dir <- Direction.values) {
//          "player moving " + dir in {
            val before = game.spawnPlayer(pos)
            val after = before.move(Player(1), dir, now)
            
            if (disallowed == dir) after must ==(before)
            else after must !=(before)
//          }
        }
      }
      
    }
  }
}
