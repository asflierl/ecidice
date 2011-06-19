/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package time

/**
 * A duration in seconds.
 */
case class Duration(seconds: Double = 0d) extends Ordered[Duration] {
  require(seconds >= 0d, "a duration may never be negative")
  
  def +(other: Duration) = Duration(seconds + other.seconds)
  def +(instant: Instant) = instant + this 
  def compare(other: Duration) = seconds.compare(other.seconds)
}
