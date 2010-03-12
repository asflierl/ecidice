package ecidice.model.dice

import ecidice.model._

class LockedDice protected (
    factory: LockedDice => DiceLock,
    top: Int = 6, 
    right: Int = 5, 
    front: Int = 4,
    serial: Long = Dice.nextSerial) 
  extends Dice[LockedDice](top, right, front, serial)
{
  lazy val lock = factory(this)
  
  protected def create(newTop: Int, newRight: Int, newFront: Int) =
    new LockedDice(factory, newTop, newRight, newFront, serial)
}
