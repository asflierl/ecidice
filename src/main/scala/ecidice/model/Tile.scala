/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import Direction._

case class Tile(column: Int, row: Int) extends Ordered[Tile] {
  def floor = Space(this, Level.Floor)
  def raised = Space(this, Level.Raised)
  
  def compare(other: Tile) =
    if (row == other.row) column compare other.column
    else row compare other.row
    
 /** 
  * Determines the tile resulting from a movement from this tile in 
  * direction `dir`.
  * 
  * @param dir the direction to move in
  * @return the tile resulting from the movement
  */
  def look(dir: Direction.Value) = dir match {
    case Backward => Tile(column, row + 1)
    case Forward => Tile(column, row - 1)
    case Right => Tile(column + 1, row)
    case Left => Tile(column - 1, row)
  }
}
