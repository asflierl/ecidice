package ecidice.model

class Clock {
  private var currentTime = 0d
  private var reactions = Set.empty[() => Any]
  
  def now = currentTime
    
  def tick(elapsedTime: Double) = {
    if (elapsedTime <= 0d) throw new IllegalArgumentException(
      "only positive time changes are allowed")
    
    currentTime += elapsedTime
    reactions.foreach(_())
  }
  
  def addReaction(r: () => Any) = 
    reactions += r
  
  def removeReaction(r: () => Any) =
    reactions -= r
}
