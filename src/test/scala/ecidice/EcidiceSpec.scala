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

import org.specs2._
import org.junit.runner._
import runner._

@RunWith(classOf[JUnitRunner])
class EcidiceSpec extends AcceptanceSpec { def is =
  "ecidice".title                                                                                  ^
                                                                                                  p^
    "ecidice consists of"                                                                          ^
                                                                                                  p^
      "the game board" ~ model.BoardSpec                                                           ^
      "dice" ~ model.DiceSpec                                                                      ^
      "the dice matcher" ~ model.DiceMatcherSpec                                                   ^
      "spaces on the game board" ~ model.SpaceSpec                                                 ^
      "the tiles of the board" ~ model.TileSpec                                                    ^
                                                                                                  p^
      "the game mode 'Gauntlet'" ~ model.mode.GauntletSpec                                         ^
                                                                                                  p^    
      "durations" ~ model.time.DurationSpec                                                        ^
      "timespans" ~ model.time.TimespanSpec                                                        ^
      "instants" ~ model.time.InstantSpec                                                          ^
                                                                                                end
}
