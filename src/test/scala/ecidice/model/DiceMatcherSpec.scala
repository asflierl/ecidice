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
import ModelTestHelpers._

import scala.collection.breakOut

import org.specs2._
import matcher._
import org.specs2.mutable._
import specification._

object DiceMatcherSpec extends UnitSpec {
  "A dice matcher" should {
    
    """
    find group 'a' on this 3 x 3 board:
    
    |----|----|----|
    | 6a | 6a | 4b |
    | 6a | 4b | 6a |
    | 6a | 6a | 6a |
    """ forExample { case TestBoard(board, List(similarDice, separators)) =>
      forall(similarDice)(die => DiceMatcher(board) find die must be equalTo similarDice)
    }
    
    "be able to search a larger board" in {
      val dice: Map[Space, Dice] = Board.sized(40, 40).spaces.keySet.map(_.tile).map(_.floor -> Dice.default)(breakOut)
      val board = Board.sized(40, 40) ++ dice
      
      DiceMatcher(board) find dice.head must be equalTo dice
    }
    
    """ 
    correctly identify the groups 'a', 'b' and 'c' on this 3 x 3 board:
    
    |----|----|----|
    |    |    | 5a |
    |    | 4c | 5a |
    | 6b | 6b |    |
    """ forExample { case TestBoard(board, groups) =>
      forall(groups)(dice => 
        forall(dice)(die => 
          DiceMatcher(board) find die must be equalTo dice))
    }
    
    """ 
    correctly find no matches of the isolated dice on this 3 x 3 board:
    
    |----|----|----|
    | 6a |    | 6a |
    |    | 6a |    |
    | 6a |    | 6a |
    """ forExample { case TestBoard(board, List(isolatedDice)) =>
      forall(isolatedDice)(die => DiceMatcher(board).find(die) must be equalTo Map(die))
    }
  }
}
