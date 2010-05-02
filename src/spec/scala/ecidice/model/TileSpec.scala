/*
 * Copyright (c) 2009-2010, Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the names of the copyright holders nor the names of the
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.model

import ecidice.SpecBase
import org.scalacheck._
import Prop.forAll
import Arbitrary.arbitrary

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
  }
  
  def positive = arbitrary[Int] suchThat (_ >= 0)
  def tile = for (col <- positive; row <- positive) yield Tile(col, row)
  implicit def arbTile: Arbitrary[Tile] = Arbitrary(tile)
}
