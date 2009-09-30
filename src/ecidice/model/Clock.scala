package ecidice.model

class Clock {
  type Reaction = () => Any
  
  private var currentTime = 0d
  private var reactions = Set.empty[Reaction]
    
  def now = currentTime
    
  def tick(elapsedTime: Double) = {
    if (elapsedTime <= 0d) throw new IllegalArgumentException(
      "only positive time changes are allowed")
    
    currentTime += elapsedTime
    reactions.foreach(_())
  }
  
  def addReaction(r: Reaction) = 
    reactions += r
  
  def removeReaction(r: Reaction) =
    reactions -= r
}
