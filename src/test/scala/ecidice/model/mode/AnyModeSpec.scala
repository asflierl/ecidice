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

package ecidice
package model
package mode

import ecidice.SpecBase

/**
 * Informal specification of basic stuff in any game mode.
 * 
 * @author Andreas Flierl
 */
class AnyModeSpec[A <: Mode[A]](game: A) extends SpecBase with ModelTestHelpers {
  "Any mode" should {
    "correctly dupe itself" in {
      game.dupe() aka "the duped game" mustEqual game
    }
    
    "correctly spawn new players" in {
      val gameWithOnePlayer = game.spawnPlayer(Tile(0, 0))
      val gameWithTwoPlayers = gameWithOnePlayer.spawnPlayer(Tile(2, 1))
      
      gameWithTwoPlayers.players mustEqual 
        Map(Player(1) -> Standing(Tile(0, 0)),
            Player(2) -> Standing(Tile(2, 1)))
    }
  }
}
object AnyModeSpec {
  def apply[A <: Mode[A]]()(implicit game: A) = new AnyModeSpec(game)
}
