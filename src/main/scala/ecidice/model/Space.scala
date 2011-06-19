/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

case class Space(tile: Tile, level: Level.Value) extends Ordered[Space] {
  def isFloor = level equals Level.Floor 
  def isRaised = level equals Level.Raised
  
  def floor =
    if (isFloor) this
    else Space(tile, Level.Floor)
    
  def raised =
    if (isRaised) this
    else Space(tile, Level.Raised)
    
  def compare(other: Space) =
    if (level == other.level) tile compare other.tile
    else level compare other.level
}
