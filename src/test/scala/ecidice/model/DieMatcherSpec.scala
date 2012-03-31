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

import org.specs2._
import matcher._
import org.specs2.mutable._
import specification._

object DieMatcherSpec extends UnitSpec {
  "A die matcher" should {
    
    """ 
    find the correct groups of dice
    
    In particular, it should find and return all dice (including `startDie`) that show the same 
    top face as `startDie` and that are reachable from `startDie` via other such dice (by only 
    moving up, down, left or right once or several times). Only solid dice that are uncontrolled
    are to be considered.
    
    As an example consider the following 3 x 3 board:
      
    |----|----|----|
    | 6a | 4b | 6d |
    | 6a | 3c | 6d |
    | 6a | 6a | 3e |
    
    Starting from any die in group "a", it should return all dice in that group, i.e. the dice at
    `(x: 1, y: 1)`, `(x: 1, y: 2)`, `(x: 1, y: 3)`, `(x: 2, y: 3)` . Starting from the die at `(x: 2, y: 1)`
    it should only return that die (group "b"). Same thing for the die at `(x: 2, y: 2)` (group "c")
    and `(x: 3, y: 3)` (group "e"). Group "d" consists of the dice at `(x: 3, y: 1)` and `(x: 3, y: 2)`
    and should be found if and only if the search starts at one of those 2 dice.

    """ forExample { case TestBoard(board, groups) =>
      forall(groups)(diceInGroup => 
        forall(diceInGroup)(die => 
          Dice on board resemblingAndConnectedTo die must be equalTo diceInGroup))
    }
 
    
    """ 
    find group 'a' on this 3 x 3 board:
    
    |----|----|----|
    | 6a | 6a | 4b |
    | 6a | 4b | 6a |
    | 6a | 6a | 6a |
    
    """ forExample { case TestBoard(board, List(similarDice, separators)) =>
      forall(similarDice)(die => Dice on board resemblingAndConnectedTo die must be equalTo similarDice)
    }
    
    "be able to search a larger board" in {
      val empty = Board.sized(40, 40)
      val dice = empty.floorSpaces.mapValues(_ => Die.default)
      val board = empty ++ dice
      
      Dice on board resemblingAndConnectedTo dice.head must be equalTo dice
    }
    
    """ 
    correctly identify the groups 'a', 'b' and 'c' on this 3 x 3 board:
    
    |----|----|----|
    |    |    | 5a |
    |    | 4c | 5a |
    | 6b | 6b |    |
    
    """ forExample { case TestBoard(board, groups) =>
      forall(groups)(diceInGroup => 
        forall(diceInGroup)(die => 
          Dice on board resemblingAndConnectedTo die must be equalTo diceInGroup))
    }
    
    """ 
    correctly find no matches of the isolated dice on this 3 x 3 board:
    
    |----|----|----|
    | 6a |    | 6a |
    |    | 6a |    |
    | 6a |    | 6a |
    
    """ forExample { case TestBoard(board, List(isolatedDice)) =>
      forall(isolatedDice)(die => Dice on board resemblingAndConnectedTo die must be equalTo Map(die))
    }
  }
}
