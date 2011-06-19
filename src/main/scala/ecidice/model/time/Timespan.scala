/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package time

/**
 * Represents a period of time, i.e. the time interval [start, end]
 * 
 * @author Andreas Flierl
 * 
 * @param start the instant this timespan starts
 * @param duration this timespan's duration
 */
case class Timespan(start: Instant, duration: Duration) {
  val end = start + duration

  def isOver(now: Instant) = (now >= end)
  
  /**
   * Returns where in this timespan the associated game is now as a number in
   * the interval [0, 1].
   */
  def progress(now: Instant) =
    if (now <= start) 0d
    else if (now >= end) 1d
    else (now.time - start.time) / (end.time - start.time)
}

