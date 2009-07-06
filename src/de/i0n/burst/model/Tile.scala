package de.i0n.burst.model

/**
 * Represents a tile in the game board. Each tile has an (inititally empty)
 * space on the floor and on the raised level.
 */
class Tile(val x: Int, val y: Int) {
  var visibility = Tile.Visibility.VISIBLE
  val floor = new Space(Empty())
  val raised = new Space(Empty())
}
object Tile {
  object Visibility extends Enumeration {
    val VISIBLE, HIDDEN = Value
  }
}
