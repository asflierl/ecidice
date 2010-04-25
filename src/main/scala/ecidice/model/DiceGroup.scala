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
 * - Neither the names of the copyright holders nor the names of its 
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

final class DiceGroup private {
  private var state: DiceGroup.State = _
  private var diceSet: Set[Dice] = Set.empty[Dice]
  
  private def init(state: DiceGroup.State, dice: Set[Dice]) = {
    this.state = state
    diceSet ++= dice
    this
  }
  
  def isCharging = (state == DiceGroup.Charging)
  def isBursting = (state == DiceGroup.Bursting)
  def cloneAsBursting = new DiceGroup().init(DiceGroup.Bursting, diceSet)
  
  def +=(d: Dice) = (diceSet += d)
  def ++(otherGroup: DiceGroup) = (diceSet ++ otherGroup.dice)
  def dice = diceSet
  def contains(d: Dice) = diceSet.contains(d)
  
  
}
object DiceGroup {
  def createCharging(dice: Set[Dice]) = new DiceGroup().init(Charging, dice)
  def createBursting(dice: Set[Dice]) = new DiceGroup().init(Bursting, dice)
  
  private sealed abstract class State
  private object Charging extends State
  private object Bursting extends State
}