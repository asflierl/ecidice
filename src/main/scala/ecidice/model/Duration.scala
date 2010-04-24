package ecidice.model

/**
 * A duration in seconds.
 */
case class Duration(seconds: Double = 0d) extends Ordered[Duration] {
  require(seconds >= 0d, "a duration may never be negative")
  
  def +(other: Duration) = Duration(seconds + other.seconds)
  def +(instant: Instant) = instant + this 
  def compare(other: Duration) = seconds.compare(other.seconds)
}
object Duration {
  def between(a: Instant, b: Instant): Duration = Duration((a.time - b.time).abs)
}
