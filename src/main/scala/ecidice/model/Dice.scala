package ecidice.model

object Dice {
  def on(board: Board) = DieMatcher(board)
}