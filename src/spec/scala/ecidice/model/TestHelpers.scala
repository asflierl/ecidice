package ecidice.model

import mode._
import time._

trait TestHelpers {
  implicit def firstSystemOfSpec(spec: org.specs.Specification) = spec.systems.head
  
  implicit def pimpGauntlet[A <: Mode[A]](m: A) = new ModeTestHelpers(m)
  
  val now = Instant()
}

class ModeTestHelpers[A <: Mode[A]](m: A) {
  def addSolidDice(contents: (Space, Dice)) = 
    m.dupe(board = m.board.put(contents))
}