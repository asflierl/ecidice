/*
 * Board.scala
 *
 * Represents the game board, i.e. the floor tiles that the dice rest upon.
 */
package de.i0n.burst.model

class Board(val width: Int, val depth: Int) {
  val tiles = new Array[Array[Tile]](width, depth)
  for (x <- Stream.range(0, depth - 1)) {
    for (y <- Stream.range(0, width - 1)) {
      tiles(x)(y) = new Tile(x, y)
    }
  }
}
