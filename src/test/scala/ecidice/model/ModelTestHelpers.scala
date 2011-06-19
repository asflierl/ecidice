/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import mode._
import time._
import scala.collection.breakOut

trait ModelTestHelpers {
  implicit def pimpMode[A <: Mode[A]](m: A) = new ModeTestHelpers(m)
  
  val now = Instant()
}

class ModeTestHelpers[A <: Mode[A]](m: A) {
  def addSolidDice(contents: (Space, Dice)) = 
    m.dupe(board = m.board + (contents))
    
  def addSolidDice(sp: Space) =
    m.dupe(board = m.board + (sp -> Dice.random))
    
  def addChargeGroup(dice: => Dice, tiles: Set[Tile]) = {
    val spaces = tiles.map(_.floor)
    val newBoard = m.board ++ spaces.map((_, Charging))(breakOut)
    val lock = ChargeLock(ChargeGroup(spaces.map((_, dice))(breakOut)), Instant())
    
    m.dupe(board = newBoard, locks = m.locks + lock)
  }
}
