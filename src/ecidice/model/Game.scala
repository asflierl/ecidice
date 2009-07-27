package ecidice.model

class Game(numPlayers: Int) {
  private val board = new Board(10, 10)
  private var currentTime = 0f 
  
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
  }
  
  /**
   * Requests for player <code>p</code> to gain control over the dice below her.
   * <p>
   * The request will not be granted
   * <ul>
   *  <li><b>if</b> there's no solid (that is neither appearing, moving nor 
   *      bursting) dice at the player's location</li>
   *  <li><b>or if</b> the top dice at the player's location is already under 
   *      control of another player</li>
   *  <li><b>or if</b> there's a solid dice at the floor level but the raised
   *      level is involved in another dice's movement.</li>
   * </ul>
   * 
   * @param p the player requesting control over a dice
   * @return the dice that is under the control of the player or 
   *         <code>None</code>
   */
  def requestControl(p: Player) : Option[Dice] =
    board(p.x, p.y).raised.content match {
      case Occupied(d) => requestControl(board(p.x, p.y).raised, p)
      case _ => requestControl(board(p.x, p.y).floor, p)
    }
  
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
  
  def requestMove(p: Player, dir: Direction.Value) : (Int, Int) = {
    (1,2)
  }
  
  def spawn(x: Int, y: Int) = board(x, y).floor.content match {
    case Empty => Some(Dice(this, board(x,y).floor))
    case _ => None
  }
}
