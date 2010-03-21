package ecidice.model

/**
 * A duration in seconds.
 */
case class Duration(seconds: Double = 0d) extends Ordered[Duration] {
  if (seconds < 0d) throw new IllegalArgumentException(
    "no negative durations allowed")
  
  def compare(other: Duration) = seconds.compare(other.seconds)
}
