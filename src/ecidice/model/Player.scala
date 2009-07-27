package ecidice.model

class Player(game: Game, spawnPoint: (Int, Int)) {
  private var position = spawnPoint
  private var dice: Option[Dice] = None
  
  def x = position._1
  def y = position._2
  
  
}
