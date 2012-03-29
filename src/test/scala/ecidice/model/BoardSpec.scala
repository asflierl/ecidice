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

import ecidice.UnitSpec
import org.specs2._

object BoardSpec extends UnitSpec {
  "The game board" should {
    "correctly indicate board bounds" in {
      
      "tile"     | "within bounds" |>
      Tile(0, 0) ! true            |
      Tile(4, 2) ! true            |
      Tile(1, 2) ! true            |
      Tile(4, 1) ! true            |
      Tile(5, 0) ! false           |
      Tile(0, 3) ! false           |
      Tile(5, 3) ! false           | { 
        
      (tile, result) =>
        board.contains(tile) must be equalTo result
      }
    }
    
    "know its tiles" in {
      board.tiles must be equalTo allTiles
    }
    
    "know all its floor spaces" in {
      board.floorSpaces.keySet must be equalTo allTiles.map(_.floor)
    }
    
    "know all its raised spaces" in {
      board.raisedSpaces.keySet must be equalTo allTiles.map(_.raised)
    }
    
    "have spaces that can be divided into floor and raised spaces" in {
      board.spaces must be equalTo (board.floorSpaces ++ board.raisedSpaces)
    }
    
    "initially have empty spaces" in {
      forall(board.spaces.values) { _ must be(Empty) } 
    }
  }
  
  val board = Board.sized(5, 3)
  val allTiles = (for (x <- 0 to 4; y <- 0 to 2) yield Tile(x, y)) toSet
}
