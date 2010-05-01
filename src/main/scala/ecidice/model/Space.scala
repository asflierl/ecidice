package ecidice.model

case class Space(tile: Tile, level: Level.Value) {
  def isFloor = level equals Level.Floor 
  def isRaised = level equals Level.Raised
}