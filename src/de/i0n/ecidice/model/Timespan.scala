package de.i0n.ecidice.model

/**
 * Represents a period of time, i.e. the time interval [start, end]
 * 
 * @author Andreas Flierl
 * 
 * @param ttl the initial time-to-live (aka duration)
 */
class Timespan(game: Game, val start: Float, ttl: Float) {
  private var to = start + ttl
  
  def end = to

  def lengthen(amount: Float) {
    if (amount > 0f) {
      to += amount
    }
  }
  
  /**
   * Returns where in this timespan the associated game is now as a number in
   * the interval [0, 1].
   */
  def progress =
    if (game.now <= start) 0f
    else if (game.now >= to) 1f
    else (game.now - start) / (to - start)
}
