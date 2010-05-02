/*
 * Copyright (c) 2009-2010, Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the names of the copyright holders nor the names of the
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.modelold

/**
 * Represents the game board, i.e. the floor tiles that the dice rest upon.
 * 
 * @author Andreas Flierl
 * 
 * @param width the number of tiles from left to right
 * @param depth the number of tiles from front to back
 */
class Board(val width: Int, val depth: Int) {
  private val tileArr = Array.tabulate(width, depth)(
    (x, y) => new Tile(x, y)) 
  
  /**
   * Returns an iterator that traverses over all tiles of this board. The tiles
   * are returned from front to back, from left to right.
   */
  def tiles = new Iterator[Tile] {
    var x, y = 0
    
    override def hasNext = x < width
    
    override def next = {
      val tile = tileArr(x)(y)
      y += 1
      if (y == depth) { x += 1; y = 0 }
      tile
    }
  }
  
  /**
   * Currently hard-coded for 4 players.
   */
  val spawnPoints = tileArr(0)(0) :: tileArr(width - 1)(0) :: 
    tileArr(width - 1)(depth - 1) :: tileArr(0)(depth - 1) :: Nil
  
  /**
   * Returns the tile at the specified position.
   * 
   * @param x the horizontal position (left to right)
   * @param y the depth position (front to back)
   * @return the tile at the specified coordinates
   */
  def apply(x: Int, y: Int): Tile = {
    tileArr(x)(y)
  }
  
  /**
   * Returns the tile at the specified position.
   * 
   * @param p the position as a tuple (x, y)
   * @return the tile at the specified coordinates
   */
  def apply(p: (Int, Int)): Tile = apply(p._1, p._2)
  
  /**
   * Determines the position resulting from a movement from tile <code>t</code>
   * in direction <code>dir</code>.
   * 
   * @param t a tile marking the starting position
   * @param dir the direction to move in
   * @return the position resulting from the movement
   */
  def positionInDir(t: Tile, dir: Direction.Value) = dir match {
    case Direction.BACKWARD => (t.x, t.y + 1)
    case Direction.FORWARD => (t.x, t.y - 1)
    case Direction.RIGHT => (t.x + 1, t.y)
    case Direction.LEFT => (t.x - 1, t.y)
  }
  
  /**
   * Returns whether the specified coordinates are within this board's bounds.
   * 
   * @param x the horizontal position (left to right)
   * @param y the depth position (front to back)
   * @return whether the specified position is within bounds
   */
  def isWithinBounds(x: Int, y: Int): Boolean = 
    (x >= 0 && x < width) && (y >= 0 && y < depth)
  
  /**
   * Returns whether the specified coordinates are within this board's bounds.
   * 
   * @param p the position to examine as tuple (x, y)
   * @return whether the specified position is within bounds
   */
  def isWithinBounds(p: (Int, Int)): Boolean = isWithinBounds(p._1, p._2)
}