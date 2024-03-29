/*
 * Copyright (c) 2009-2012 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice
package model

import collection.breakOut

case class Board(spaces: Map[Space, Contents]) {
  import Board._
  
  def apply(space: Space) = spaces(space)
  
  def contains(tile: Tile): Boolean = spaces.contains(tile.floor)
    
  def +(mapping: (Space, Contents)) = copy(spaces = spaces + mapping)
  def ++(contents: Map[Space, Contents]) = copy(spaces = spaces ++ contents)
  def -(x: Tile) = copy(spaces = spaces - x.floor - x.raised)
  def --(xs: Traversable[Tile]) = copy(spaces = spaces -- (xs map (_.floor)) -- (xs map (_.raised)))
  
  lazy val tiles: Set[Tile] = spaces.map(spacesToTiles)(breakOut)
  def floorSpaces = floor(spaces) 
  def raisedSpaces = raised(spaces)
}
object Board extends (Map[Space, Contents] => Board) {
  def sized(columns: Int, rows: Int) = Board(Map.empty ++ contentMappings(columns, rows))
    
  private def contentMappings(columns: Int, rows: Int) = spaces(columns, rows).map(_ -> Empty)
    
  private def spaces(columns: Int, rows: Int) = 
    for {
      x <- 0 until columns
      y <- 0 until rows
      l <- Level.values
    } yield Space(Tile(x, y), l)
  
  val spacesToTiles = (t: (Space, Contents)) => t._1.tile
  val floor = (spaces: Map[Space, Contents]) => spaces filterKeys (_.isFloor == true)
  val raised = (spaces: Map[Space, Contents]) => spaces filterKeys (_.isRaised == true)
}
