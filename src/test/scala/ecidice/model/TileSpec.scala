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
import org.scalacheck._
import Generators._

import org.specs2._

object TileSpec extends UnitSpec {
  "A tile" should {
    "be consistent in equivalence and ordering" in check {
      (a: Tile, b: Tile) => (a == b) must be equalTo (implicitly[Ordering[Tile]].compare(a, b) == 0)
    }
    
    "have correct ordering behaviour" in {
      val tiles = List(Tile(3, 1), Tile(-1, -2), Tile(4, 0), Tile(0, 1), 
          Tile(-1, 2), Tile(3, 5), Tile(3, -2), Tile(2, 0))
      
      tiles.sorted aka "the ordered tiles" must be equalTo (
          List(Tile(-1, -2), Tile(3, -2), Tile(2, 0), Tile(4, 0), Tile(0, 1), 
              Tile(3, 1), Tile(-1, 2), Tile(3, 5))
      )
    }
    
    "return the correct tile when looking in any direction" in {
      
      "direction"         | "result position" |>
      Direction.Backward  ! Tile(2, 2)        |
      Direction.Forward   ! Tile(2, 0)        |
      Direction.Left      ! Tile(1, 1)        |  
      Direction.Right     ! Tile(3, 1)        | { 
        
      (dir, result) =>
        Tile(2, 1).look(dir) must be equalTo(result)
      }
    }
  }
}
