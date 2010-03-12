package ecidice.model.dice

import ecidice.model._

class AppearingDice protected (
    factory: AppearingDice => DiceAppearing,
    top: Int = 6, 
    right: Int = 5, 
    front: Int = 4,
    serial: Long = Dice.nextSerial) 
  extends Dice[AppearingDice](top, right, front, serial)
{
  lazy val appearing = factory(this)
  
  protected def create(newTop: Int, newRight: Int, newFront: Int) =
    new AppearingDice(factory, newTop, newRight, newFront, serial)
  
}
