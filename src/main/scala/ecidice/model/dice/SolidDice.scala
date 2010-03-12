package ecidice.model.dice

import ecidice.model._

class SolidDice protected (
    location: Space,
    top: Int, 
    right: Int, 
    front: Int,
    serial: Long) 
  extends Dice[SolidDice](top, right, front, serial)
{
  protected def create(newTop: Int, newRight: Int, newFront: Int) =
    new SolidDice(location, newTop, newRight, newFront, serial)
  
}
