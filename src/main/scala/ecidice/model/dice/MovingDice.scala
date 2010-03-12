package ecidice.model.dice

import ecidice.model._

class MovingDice protected (
    factory: MovingDice => DiceMovement,
    top: Int, 
    right: Int, 
    front: Int,
    serial: Long) 
  extends Dice[MovingDice](top, right, front, serial)
{
  lazy val movement = factory(this)
  
  protected def create(newTop: Int, newRight: Int, newFront: Int) =
    new MovingDice(factory, newTop, newRight, newFront, serial)
  
}
