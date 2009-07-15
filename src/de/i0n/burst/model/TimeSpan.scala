package de.i0n.burst.model

/**
 * Represents a period of time of a specified length or time-to-live. This
 * time-to-live can be decreased and optionally increased. Once it reaches 0,
 * this timespan ends.
 * 
 * @author Andreas Flierl
 * 
 * @param ttl the initial time-to-live (aka duration)
 */
class Timespan(private var ttl: Float) {
  /**
   * Remembers the initial time-to-live, set at creation time.
   */
  val initial = ttl
  
  private var totalElapsed = 0f
  private var totalDelay = 0f 
  
  /**
   * Returns the time from now on until this timespan ends. Never returns
   * a value < 0.
   */
  def timeToLive = ttl
  
  /**
   * Returns the total amount of time elapsed in this timespan.
   */
  def elapsed = totalElapsed
  
  /**
   * Returns how long the initial time-to-live has been extended after the
   * creation of this instance.
   */
  def delay = totalDelay
  
  /**
   * Indicates to this instance that the specified amount of time has elapsed.
   * The elapsed time is subtracted from this timespan's time-to-live.
   * 
   * @param amount the elapsed time; negative values are ignored
   */
  def elapsed(amount: Float) = {
    if (amount > ttl) {
      ttl = 0
      totalElapsed += ttl
    } else if (amount > 0) {
      ttl -= amount
      totalElapsed += amount
    }
  }
  
  /**
   * Adds the specified amount of time to this timespan's time-to-live.
   * 
   * @param amount the delay time; negative values are ignored
   */
  def delay(amount: Float) {
    if (amount > 0f) {
      ttl += amount
      totalDelay += amount
    }
  }
  
  /**
   * Returns how close this timespan is to its end as a percentage of the 
   * initial time-to-live + the total delay. The returned number is in the
   * interval [0,1].
   */
  def progress = 1f - (timeToLive / (initial + totalDelay)) 
}
