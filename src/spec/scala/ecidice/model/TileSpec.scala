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

import ecidice.SpecBase
import org.scalacheck._
import Prop.forAll
import Generators._

/**
 * Informal specification of a tile. 
 */
object TileSpec extends SpecBase {
  "A tile" should {
    "be consistent in equivalence and ordering" in {
      val consistency = forAll((a: Tile, b: Tile) =>
        (a == b) mustEqual ((a compare b) == 0))
      
      consistency must pass
    }
    
    "not allow negative rows or columns" in {
      "column" | "row" | "legal" |>
       8       !  42   ! true    |
      -7       !  3    ! false   |
       1       ! -100  ! false   |
      -1       !  0    ! false   |
       0       !  0    ! true    | {
        
      (col, row, legal) =>
        if (! legal) Tile(col, row) must throwAn[IllegalArgumentException]
        else Tile(col, row) mustNot beNull
      }
    }
    
    "have correct ordering behaviour" in {
      val tiles = List(Tile(3, 1), Tile(4, 0), Tile(0, 1), Tile(3, 5), Tile(2, 0))
      
      tiles.sortWith(_ < _) aka "the ordered tiles" mustEqual (
          List(Tile(2, 0), Tile(4, 0), Tile(0, 1), Tile(3, 1), Tile(3, 5))
      )
    }
  }
}
