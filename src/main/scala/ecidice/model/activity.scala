/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import time._

/**
 * Indicates that a game event is timed and needs to be tracked/managed.
 * 
 * @author Andreas Flierl
 */
sealed trait Activity {
  def start: Instant
  def duration: Duration
  
  val time = Timespan(start, duration)
}

case class DiceAppearing(
  dice: Dice,
  location: Space,
  start: Instant
) extends Activity with Contents {
  def duration = DiceAppearing.duration
}
object DiceAppearing {
  val duration = Duration(5d)
}
  
case class DiceMovement(
  dice: Dice,
  origin: Space,
  destination: Space, 
  transform: Transform.Value,
  controller: Player,
  start: Instant
) extends Activity with Contents {
  def duration = DiceMovement.duration
}
object DiceMovement {
  val duration = Duration(.25d)
}

case class DiceFalling(
  dice: Dice,
  location: Tile,
  controller: Option[Player],
  start: Instant
) extends Activity with Contents {
  def duration = DiceFalling.duration
}
object DiceFalling {
  val duration = Duration(.25d)
}

sealed trait DiceLock[T <: DiceGroup[T]] extends Activity {
  def group: T
}

case class ChargeLock(
  group: ChargeGroup,
  start: Instant
) extends DiceLock[ChargeGroup] {
  def duration = ChargeLock.duration
}
object ChargeLock {
  val duration = Duration(10d)
}

case class BurstLock(
  group: BurstGroup,
  start: Instant
) extends DiceLock[BurstGroup] {
  def duration = BurstLock.duration
}
object BurstLock {
  val duration = Duration(1d)
}

case class PlayerMovement(
  player: Player,
  origin: Tile,
  destination: Tile,
  start: Instant
) extends Activity {
  def duration = PlayerMovement.duration
}
object PlayerMovement {
  val duration = Duration(.25d)
}
