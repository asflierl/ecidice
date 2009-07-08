package de.i0n.burst.model

class TimeSpan(val start: Long, var duration: Long) {
  def end = start + duration
}
