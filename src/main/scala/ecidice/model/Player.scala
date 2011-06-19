/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

case class Player(id: Int) extends Ordered[Player] {
  def compare(other: Player) = id compare(other id)
}
