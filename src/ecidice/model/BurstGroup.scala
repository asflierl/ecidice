package ecidice.model

import scala.collection.mutable._

class BurstGroup(val when: Timespan) {
  private val dice = new HashSet[Dice]
  
  def +=(d: Dice) { 
    dice += (d) 
  }
  
  def contains(d: Dice) = dice.contains(d)
}
