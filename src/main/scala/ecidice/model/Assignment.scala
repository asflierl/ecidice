/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

/**
 * Things a player can do. 
 */
sealed trait Assignment

case class Standing(location: Tile) extends Assignment
case class MovingAlone(activity: PlayerMovement) extends Assignment
case class ControllingADice(location: Space) extends Assignment
case class MovingWithADice(activity: DiceMovement, relinquishAfter: Boolean) extends Assignment
case class FallingWithADice(activity: DiceFalling) extends Assignment
