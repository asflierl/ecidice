package ecidice.model

trait Clock {
  private var currentTime = 0d
  
  def now = currentTime
  
  def createTimespanWithLength(someTime: Double) = 
    new Timespan(this, now, someTime)
  
  protected def addToCurrentTime(elapsedTime: Double) =
    currentTime += elapsedTime
}
