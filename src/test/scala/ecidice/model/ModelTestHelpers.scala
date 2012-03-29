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

import mode._
import time._
import scala.collection.breakOut
import Transform._

object ModelTestHelpers {
  implicit def pimpMode[A <: Mode[A]](m: A) = new ModeTestHelpers(m)
  
  val now = Instant()
  
  val dieWithTop: PartialFunction[Int, Die] = {
    case 1 => Die.default transform RotateLeft transform RotateLeft
    case 2 => Die.default transform RotateRight
    case 3 => Die.default transform RotateForward
    case 4 => Die.default transform RotateBackward
    case 5 => Die.default transform RotateLeft
    case 6 => Die.default
  }
  
  object TestBoard {
    def unapply(desc: String): Option[(Board, List[Map[Space, Die]])] = {
      val rows = desc.lines.dropWhile(_.trim.isEmpty).toList.tail.map(_.trim.split('|').tail.map(_.trim).toSeq)
  
      if (rows.exists(_.size != rows.head.size)) None
      else {
        val parsedBoard = for {
          (row, y) <- rows.zipWithIndex
          (content, x) <- row.zipWithIndex
          (group, die) <- parse(content)
        } yield (group, die, Tile(x, y))
        
        Some((Board.sized(rows.head.size, rows.size) ++ groupBySpace(parsedBoard),
             parsedBoard.groupBy(_._1).toList.sortBy(_._1).map(_._2).map(groupBySpace)))
      } 
    }
    
    private def groupBySpace(s: Seq[(String, Die, Tile)]): Map[Space, Die] = (s map { case (g, d, t) => t.floor -> d })(breakOut)
    
    private def parse(tileContent: String): Option[(String, Die)] = 
      for {
        contentPattern(top, group) <- Some(tileContent)
        die <- (dieWithTop lift)(top toInt)
      } yield (group, die)
        
    private val contentPattern = "([1-6])([a-z])".r 
  }
}

class ModeTestHelpers[A <: Mode[A]](m: A) {
  def addSolidDie(contents: (Space, Die)) = 
    m.dupe(board = m.board + (contents))
    
  def addSolidDie(sp: Space) =
    m.dupe(board = m.board + (sp -> Die.random))
    
  def addChargeGroup(die: => Die, tiles: Set[Tile]) = {
    val spaces = tiles.map(_.floor)
    val newBoard = m.board ++ spaces.map((_, Charging))(breakOut)
    val lock = ChargeLock(ChargeGroup(spaces.map((_, die))(breakOut)), Instant())
    
    m.dupe(board = newBoard, locks = m.locks + lock)
  }
}
