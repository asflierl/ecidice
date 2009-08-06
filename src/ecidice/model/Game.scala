package ecidice.model

class Game(numPlayers: Int) {
  private val board = new Board(10, 10)
  private var currentTime = 0f
  
  val MOVE_DURATION = 0.25f
  val APPEAR_DURATION = 5f
  
  lazy val players = createPlayers(0)
  
  /**
   * Creates <code>num</code> players in this game, starting at the boards 
   * predefined spawn locations.
   */
  private def createPlayers(num: Int) : List[Player] =
    if (num == numPlayers) Nil
    else new Player(this, board.spawnPoints(num)) :: createPlayers(num + 1)
  
  def now = currentTime
  
  /**
   * Updates this game after the specified amount of time has elapsed.
   * 
   * @param elapsed the elapsed time (in seconds as float)
   */
  def update(elapsed: Float) {
    currentTime += elapsed
    //TODO everything that has a duration/timespan needs to be checked and updated!
  }
  
  /**
   * Requests for player <code>p</code> to gain control over the dice below her.
   * <p>
   * If the player is already controlling a dice, control is retained on that
   * dice.
   * <p>
   * If there's two dice at the player's location and the upper dice is solid
   * and not under the control of another player, control will be granted on
   * that upper dice.
   * <p>
   * If there's only one dice at the player's location (which must be on the
   * floor level) that is solid and not under the control of any player,
   * control will be granted on that dice.
   * <p>
   * In all other cases, the control request will be rejected.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting control over a dice
   * @return the dice that is under the control of the player or 
   *         <code>None</code>
   */
  def requestControl(p: Player) : Option[Dice] = p.state match {
    case Player.Standing(t) => t.raised.content match {
      case Occupied(d) => requestControl(t.raised, p)
      case _ => requestControl(t.floor, p)
    }
    case Player.Controlling(d) => Some(d)
    case _ => None
  }
  
  /**
   * Helper method for requestControl(Player)
   */
  private def requestControl(where: Space, p: Player) = where.content match {
    case Occupied(d) => d.state match {
      case Dice.Solid(s, c) => 
        if (c.isEmpty) {
          d.state = Dice.Solid(s, Some(p))
          Some(d) 
        } else {
          None
        }
      case _ => None
    }
    case _ => None
  }
  
  /**
   * Requests for player <code>p</code> to move in direction <code>dir</code>.
   * <p>
   * If the player is not controlling a dice and the position after the move
   * would be still on the game board, the request is granted.
   * <p>
   * If the player is controlling a dice, the dice is "solid", the destination
   * is within board bounds and the destination tile has an empty space on top,
   * the request is granted.
   * <p>
   * If the player is already moving to the same position (with or without 
   * dice), the request is granted.
   * <p>
   * In all other cases, the movement request will be rejected.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting to move
   * @param dir the direction the player wants to move in
   * @return whether the move was allowed (and started)
   */
  def requestMove(p: Player, dir: Direction.Value) : Boolean = p.state match {
    /* This is the easy case: the player controls no dice and just wants to move
     * around.
     */
    case Player.Standing(t @ Tile(x, y)) => {
      val pos = positionAfterMove(t, dir)
      
      if (board.isWithinBounds(pos)) {
        p.state = Player.Moving(t, board(pos), 
                                new Timespan(this, now, MOVE_DURATION))
        true
      } else false // destination out of board bounds
    }
    
    /* Somewhat tricky: the player controls a dice and wants to move along with
     * it. This is only granted if the target position is wihin bounds and the
     * tile at that position is free to be moved to.
     */
    case Player.Controlling(d) => d.state match {
      case Dice.Solid(s, Some(c)) if (p == c) => {
        val pos = positionAfterMove(s.tile, dir)
        
        if (board.isWithinBounds(pos)) {
          val t = board(pos)
          t.floor.content match {
            case Empty => {
              startDiceMovement(d, p, s, t.floor, dir)
              true
            }
            case _ : Movement => false // destination floor involved in movement
            case _ : Occupied => t.raised.content match {
              case Empty => {
                startDiceMovement(d, p, s, t.floor, dir)                
                true
              }
              case _ => false // destination floor + raised non-empty
            }
          }
        } else false // destination out of board bounds 
      }
      
      /* Player wants to move the dice she's already moving to the same place
       * it's already moving to: leave things as they are and return 
       * <code>true</code>.
       */
      case Dice.Moving(m @ Movement(_, a, b, _, _), c) 
        if (p == c && b.tile.pos == positionAfterMove(a.tile, dir)) => true
      
      case _ => false // dice appearing, bursting or moving in another direction
    }
    
    /* Player wants to move to the same place she's already moving to: 
     * leave things as they are and return <code>true</code>.
     */
    case Player.Moving(a, b, _) if (b.pos == positionAfterMove(a, dir)) => true
    
    case _ => false
  }
  
  /**
   * Helper method for <code>requestMove</code> that updates the game model
   * state to actually represent the movement of a dice.
   * 
   * @param d the dice that moves
   * @param p the player controlling the dice
   * @param from the starting position
   * @param to the destination position
   * @param dir the movement direction
   */
  private def startDiceMovement(d: Dice, p: Player, from: Space, to: Space, 
                            dir: Direction.Value) : Unit = {
    val transform = if (from.isFloor == to.isFloor) dir match {
      case Direction.UP => Transform.ROTATE_UP
      case Direction.DOWN => Transform.ROTATE_DOWN
      case Direction.RIGHT => Transform.ROTATE_RIGHT
      case Direction.LEFT => Transform.ROTATE_LEFT
    } else dir match {
      case Direction.UP | Direction.DOWN => Transform.FLIP_UP_OR_DOWN
      case Direction.LEFT | Direction.RIGHT => Transform.FLIP_LEFT_OR_RIGHT
    }
      
    val m = Movement(d, from, to, new Timespan(this, now, MOVE_DURATION), 
                     transform)
    
    from.content = m
    to.content = m
    d.state = Dice.Moving(m, p)
  }
  
  /**
   * Determines the position resulting from a movement from tile <code>t</code>
   * in direction <code>dir</code>.
   * 
   * @param t a tile marking the starting position
   * @param dir the direction to move in
   * @return the position resulting from the movement
   */
  private def positionAfterMove(t: Tile, dir: Direction.Value) = dir match {
    case Direction.UP => (t.x, t.y + 1)
    case Direction.DOWN => (t.x, t.y - 1)
    case Direction.RIGHT => (t.x + 1, t.y)
    case Direction.LEFT => (t.x - 1, t.y)
  }
  
  def spawnDice(x: Int, y: Int) = board(x, y).floor.content match {
    case Empty => {
      val d = new Dice()
      board(x, y).floor.content = Occupied(d)
      d.state = Dice.Appearing(board(x, y).floor, 
                               new Timespan(this, now, APPEAR_DURATION))
      Some(d)
    }
    case _ => None
  }
}
