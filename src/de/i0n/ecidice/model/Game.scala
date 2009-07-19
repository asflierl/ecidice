package de.i0n.ecidice.model

class Game {
  private val board = new Board(10, 10)
  private var currentTime = 0f 
  
  def now = currentTime
  
  def update(elapsed: Float) : Unit = currentTime += elapsed
  
  def requestControl(x: Int, y: Int, p: Player) : Boolean = {
    val t = board(x, y)
    requestControl(t.raised) || requestControl(t.floor)
  }
  
  private def requestControl(where: Space) = where.content match {
    case Occupied(d) => d.state match {
      case Dice.Solid(s, c) => c.isEmpty
      case _ => false
    }
    case _ => false
  }
}
