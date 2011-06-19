/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package time

/**
 * An instant in time.
 */
case class Instant(time: Double = 0d) extends Ordered[Instant] {
  require(time >= 0d, "an instant may never be negative")
  
  def +(duration: Duration) = Instant(time + duration.seconds)
  def -(duration: Duration) = Instant(time - duration.seconds)
  def -(other: Instant)= Duration((time - other.time).abs)
  def compare(other: Instant) = time.compare(other.time)
}
