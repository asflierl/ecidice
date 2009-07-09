package de.i0n.burst.model

import scala.compat.Platform


class Timespan(private var ttl: Float) {
  val initial = ttl
  
  def timeToLive = ttl
  
  /**
   * Indicates to this instance that the specified amount of time has elapsed.
   * The elapsed time is subtracted from this timespan's time-to-live.
   * 
   * @param elapsed the elapsed time; negative values are ignored
   */
  def update(elapsed: Float) = {
    if (elapsed > 0f) {
      ttl -= elapsed
    }
  }
}
