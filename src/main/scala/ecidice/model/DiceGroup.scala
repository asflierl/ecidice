/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

sealed trait DiceGroup[T <: DiceGroup[T]] { this: T =>
  def dice: Map[Space, Dice]
  protected def create(dice: Map[Space, Dice]): T

  def +(kv: (Space, Dice)) = create(dice = dice + kv)
  def ++(otherGroup: DiceGroup[_]) = create(dice = dice ++ otherGroup.dice)
  def contains(s: Space) = dice.contains(s)
}

case class ChargeGroup(dice: Map[Space, Dice]) extends DiceGroup[ChargeGroup] {
  def create(m: Map[Space, Dice]) = copy(m)
}

case class BurstGroup(dice: Map[Space, Dice]) extends DiceGroup[BurstGroup] {
  def create(m: Map[Space, Dice]) = copy(m)
}
