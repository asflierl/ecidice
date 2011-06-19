/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import collection.breakOut

case class Board(
  columns: Int,
  rows: Int,
  spaces: Map[Space, Contents]
) {
  def apply(space: Space) = spaces(space)
  
  def contains(tile: Tile): Boolean = spaces.contains(tile.floor)
    
  def +(mapping: (Space, Contents)) = copy(spaces = spaces + mapping)
  def ++(contents: Map[Space, Contents]) = copy(spaces = spaces ++ contents)
  
  lazy val tiles: Set[Tile] = spaces.map(Board.spacesToTiles)(breakOut) 
}
object Board {
  def sized(columns: Int, rows: Int): Board =
    Board(columns, rows, Map.empty ++ contentMappings(columns, rows))
    
  private def contentMappings(columns: Int, rows: Int) =
    spaces(columns, rows).map(_ -> Empty)
    
  private def spaces(columns: Int, rows: Int) = 
    for (
      x <- 0 until columns;
      y <- 0 until rows;
      l <- Level.values
    ) yield Space(Tile(x, y), l)
  
  val spacesToTiles = (t: (Space, Contents)) => t._1.tile
}
