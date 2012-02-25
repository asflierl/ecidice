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

/**
 * Informal specification of a player relinquishing control of a dice.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithRelinquishRequestSpec[A <: Mode[A] with RelinquishRequest[A]](game: A) 
extends UnitSpec with ModelTestHelpers {
  
  "Any mode that allows a player to relinquish control of a dice" should {
    
    "allow relinquishing when the player is controlling a dice" in {
      val beforeControl = game.spawnPlayer(Tile(1, 1))
                         .addSolidDice(Tile(1, 1).floor)
                         
      val afterRelinquish =
        beforeControl.control(Player(1))
                     .relinquish(Player(1))
      
      afterRelinquish must be equalTo beforeControl
    }
    //TODO specifiy movement cases
  }
}
object AnyModeWithRelinquishRequestSpec {
  def apply[A <: Mode[A] with RelinquishRequest[A]]()(implicit game: A) = 
    new AnyModeWithRelinquishRequestSpec(game)
}
