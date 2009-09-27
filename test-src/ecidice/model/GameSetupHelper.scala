package ecidice.model

trait GameSetupHelper {
  var b : Board = _
  var g : Game = _
    
  def reset() = {
    b = new Board(3, 3)
    g = new Game(2, b)
  }
  
  def p1 = g.players(0)
  def p2 = g.players(1)
  
  def placePlayer(p: Player, pos: (Int, Int)) : Unit =
      p.state = Player.Standing(b(pos))
  
  /**
   * Helper method that creates a new dice and places it at the topmost 
   * available space at the specified position or throws an exception if this
   * fails.
   * 
   * @param x the horizontal component of the position to place the dice
   * @param y the depth component of the position to place the dice
   * @return the newly created and placed dice
   */
  def placeDice(pos: (Int, Int)) : Dice = {
    val destinationTile = b(pos)
    val destinationSpace = destinationTile.floor.content match {
      case Empty => destinationTile.floor
      case Occupied(_) => destinationTile.raised
      case _ => throw new IllegalStateException("tile not in a state for a"
                                                + " dice to be placed")
    }
    
    val dice = new Dice
    destinationSpace.content = Occupied(dice)
    dice.state = Dice.Solid(destinationSpace, None)
    dice
  }
}
