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
import Transform._

import scala.collection.breakOut

import org.specs2._
import matcher._
import org.specs2.mutable._
import specification._

object DiceMatcherSpec extends UnitSpec {
  "A dice matcher" should {
    
    """ 
    correctly find a group of matching dice on this 3 x 3 board:
    
    |---|---|---|
    | 6 | 6 | 4 |
    | 6 | 4 | 6 |
    | 6 | 6 | 6 |
    """.m in {
      val similarDice = sixOnTop(Set(Tile(0,0), Tile(1,0), Tile(2,0), Tile(0,1),
                                     Tile(2,1), Tile(0,2), Tile(1,2)))
      val separators = fourOnTop(Set(Tile(1, 1), Tile(2, 2)))
      val board = Board.sized(3, 3) ++ similarDice ++ separators
      
      DiceMatcher(board) find similarDice.head must be equalTo similarDice
    }
    
    "be able to search a larger board" in {
      val dice = sixOnTop(Board.sized(40, 40).spaces.keySet.map(_.tile))
      val board = Board.sized(40, 40) ++ dice
      
      DiceMatcher(board) find dice.head must be equalTo dice
    }
    
    """ 
    correctly find only one of two (separated) groups of matching dice on this 3 x 3 board:
    
    |---|---|---|
    |   |   | 5 |
    |   | 4 | 5 |
    | 6 | 6 |   |
    """.m in {
      val groupOne = sixOnTop(Set(Tile(0, 0), Tile(1, 0)))
      val groupTwo = fiveOnTop(Set(Tile(2, 2), Tile(2, 1)))
      val separator = fourOnTop(Set(Tile(1, 1)))
      val board = Board.sized(3, 3) ++ groupOne ++ groupTwo ++ separator
      
      val matcher = DiceMatcher(board)
      
      matcher find groupOne.head must be equalTo groupOne
      matcher find groupTwo.head must be equalTo groupTwo
      matcher find separator.head must be equalTo separator
    }
    
    """ 
    correctly find no matches of isolated dice on this 3 x 3 board:
    
    |---|---|---|
    | 6 |   | 6 |
    |   | 6 |   |
    | 6 |   | 6 |  
    """.m in {
      val isolated = sixOnTop(Set(Tile(0, 0), Tile(2, 0), Tile(1, 1),
                                  Tile(0, 2), Tile(2, 2)))
      val board = Board.sized(3, 3) ++ isolated
                   
      ((dice: (Space, Dice)) =>
        DiceMatcher(board).find(dice) must be equalTo Map(dice)
      ) foreach isolated
    }
  }
  
  def sixOnTop(locs: Set[Tile]) = place(locs, Dice.default)
    
  def fiveOnTop(locs: Set[Tile]) = place(locs, Dice.default transform RotateLeft)
  
  def fourOnTop(locs: Set[Tile]) = place(locs, Dice.default transform RotateBackward)
  
  def place(locs: Set[Tile], factory: => Dice): Map[Space, Dice] =
    locs.map(_.floor -> factory)(breakOut)
}
