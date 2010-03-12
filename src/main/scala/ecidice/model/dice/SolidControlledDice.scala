package ecidice.model.dice

import ecidice.model._

class SolidControlledDice protected (
    val location: Space,
    val controller: Player,
    top: Int, 
    right: Int, 
    front: Int,
    serial: Long) 
  extends Dice[SolidControlledDice](top, right, front, serial)
{
  protected def create(newTop: Int, newRight: Int, newFront: Int) =
    new SolidControlledDice(location, controller, newTop, newRight, newFront, serial)
}
