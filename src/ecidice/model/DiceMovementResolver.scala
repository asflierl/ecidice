package ecidice.model

/**
 * Defines the rules for when and where a dice is allowed to move.
 * <p>
 * A movement request (by a player) is granted (only) if the player controls a 
 * dice, the destination position would be on the game board, the destination is
 * either a free floor space or a free space upon an uncontrolled, solid dice.
 * 
 * @author Andreas Flierl
 */
class DiceMovementResolver(board: Board, clock: Clock, tracker: ActivityTracker) {
  private var player : Player = _
  private var direction : Direction.Value = _
  private var dice : Dice = _
  
  def requestMove(player: Player, direction: Direction.Value) : Boolean = {
    this.player = player
    this.direction = direction
    val diceControlledByPlayer = player.controlledDice
    
    if (diceControlledByPlayer.isEmpty) return false
    
    dice = diceControlledByPlayer.get
    
    dice.state match {
      case Dice.Solid(start, Some(controller)) if (player == controller) =>
        tryToMoveFrom(start)

      case Dice.Moving(Movement(_, from, to, _, _), controller) 
        if (wouldBeSameMovement(controller, from, to)) => true
      
      case _ => false // dice appearing, bursting or moving in another direction
    }
  }
  
  private def tryToMoveFrom(start: Space) : Boolean = {
    val position = board.positionInDir(start.tile, direction)
    
    if (! board.isWithinBounds(position)) return false
    
    val tile = board(position)
    val destination = findDestinationSpace(tile)
    
    if (destination.isEmpty) return false
    
    startDiceMovement(start, destination.get)
    true
  }
  
  private def wouldBeSameMovement(controller: Player, from: Space, to: Space) =
    (player == controller) &&
    (to.tile.pos == board.positionInDir(from.tile, direction))
  
  private def startDiceMovement(from: Space, to: Space) = {
    val when = clock.createTimespanWithLength(Game.MOVE_DURATION)
    val transform = Transform(from, to, direction)
    val move = Movement(dice, from, to, when, transform)
    
    tracker.track(move)
    
    from.content = move
    to.content = move
    dice.state = Dice.Moving(move, player)
  }
  
  private def findDestinationSpace(tile: Tile) : Option[Space] = 
    tile.floor.content match {
      case Empty => Some(tile.floor)   
      case _ : Movement => None
      case Occupied(byDiceOnFloor) => examineOccupied(byDiceOnFloor)  
  }
  
  private def examineOccupied(diceOnFloor: Dice) = {
    diceOnFloor.state match {   
      case Dice.Solid(occupiedSpace, controller) =>
        if (controller.isDefined) None
        else examineRaised(occupiedSpace.tile.raised) 
      case _ => None // can only move onto solid dice
    }
  }
  
  private def examineRaised(destination: Space) = destination.content match {
    case Empty => Some(destination)
    case _ => None
  }
}
