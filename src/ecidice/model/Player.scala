package ecidice.model

class Player(game: Game, spawnPoint: Tile) {
  var state : Player.State = Player.Standing(spawnPoint)
}
object Player {
  sealed abstract class State
  
  case class Standing(p: Tile) extends State
  
  case class Controlling(dice: Dice) extends State
  
  case class Moving(from: Tile, to: Tile, when: Timespan) extends State
}