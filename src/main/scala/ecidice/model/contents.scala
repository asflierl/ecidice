/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

/**
 * Marker trait for things that can occupy a space.
 */
trait Contents

case object Empty extends Contents

case class SolidControlled(
  dice: Dice,
  controller: Player
) extends Contents

case object Charging extends Contents
