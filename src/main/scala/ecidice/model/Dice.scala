/*
 * Copyright (c) 2009-2010, Andreas Flierl
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the names of the copyright holders nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.model

/**
 * A single 6-side dice.
 * 
 * This class is final because it is not designed for extension through 
 * inheritance (esp. considering equals(Any)).
 * 
 * @author Andreas Flierl
 */
final class Dice {
  private val serial = Dice.nextSerial
  private var rota = Rotation.initial
  
  private var state: State = _
  
  def top = rota.top  
  def bottom = rota.bottom
  def right = rota.right
  def left = rota.left
  def front = rota.front
  def back = rota.back
  
  /**
   * Changes this dice's rotation according to the specified transform.
   * 
   * @param how the transform to apply
   */
  def change(how: Transform.Value) = (rota = rota.transform(how))
  
  override def equals(obj: Any) = obj match {
    case other: Dice => other.serial == serial
    case _ => false
  }
  
  override def hashCode = serial
  
  override def toString = "Dice[%d]-%s".format(serial, rota)
 
  def isAppearing = state.isInstanceOf[Appearing]
  def isSolid = state.isInstanceOf[Solid]
  def isMoving = state.isInstanceOf[Moving]
  def isLocked = state.isInstanceOf[Locked]
  def isCharging = isLocked && group.isCharging
  def isBursting = isLocked && group.isBursting
  def isBurst = (state == Burst)
  
  def isControlled = state match {
    case Solid(_, controller) if (controller != None) => true
    case _: Moving => true
    case _ => false
  }
  
  def location = state match {
    case Appearing(activity) => activity.location
    case Solid(somewhere, _) => somewhere
    case Locked(_, _, somewhere) => somewhere
    case _ => throw new IllegalStateException("dice location undetermined")
  }
  
  def appearing = state match {
    case Appearing(activity) => activity
    case _ => throw new IllegalStateException("dice not appearing")
  }
  
  def movement = state match {
    case Moving(activity) => activity
    case _ => throw new IllegalStateException("dice not moving")
  }
  
  def controller = state match {
    case Solid(_, controller) if (controller != None) => controller.get
    case Moving(movement) => movement.controller
    case _ => throw new IllegalStateException("dice is uncontrolled")
  }
  
  def initiator = state match {
    case Locked(initiator, _, _) => initiator
    case _ => throw new IllegalStateException("dice has no initiator")
  }
  
  def group = state match {
    case Locked(_, activity, _) => activity.group
    case _ => throw new IllegalStateException("dice is grouped")
  }
  
  def charging = state match {
    case Locked(_, activity, _) if (activity.group.isCharging) => activity
    case _ => throw new IllegalStateException("dice is not charging")
  }
  
  def bursting = state match {
    case Locked(_, activity, _) if (activity.group.isBursting) => activity
    case _ => throw new IllegalStateException("dice is not bursting")
  }
  
  def appear(activity: DiceAppearing) = (state = Appearing(activity))
  
  def solidify(location: Space, controller: Option[Player]) =
    state = Solid(location, controller)
  
  def submitTo(controller: Player) = state match {
    case Solid(location, _) => state = Solid(location, Some(controller))
    case _ => throw new IllegalStateException("dice is not solid")
  }
  
  def move(activity: DiceMovement) = (state = Moving(activity))

  def lock(activity: DiceLock, initiator: Player) =
    state = Locked(initiator, activity, location)

  def burst() = (state = Burst)
  
  /**
   * Supertype of a dice's possible states.
   */
  private sealed abstract class State
  
  /**
   * The dice is appearing. It can not be controlled nor moved. It occupies 
   * some space.
   */
  private case class Appearing(activity: DiceAppearing) extends State
  
  /**
   * The dice is solid now. It can be controlled by a player. It occupies
   * some space.
   */
  private case class Solid(where: Space, controller: Option[Player])
    extends State
  
  /**
   * The dice is moving. During movement, it is always controlled by a player.
   * The movement object defines the space occupied while moving.
   */
  private case class Moving(activity: DiceMovement) extends State
    
  /**
   * The dice has been locked and is part of a dice group, i.e. it is either
   * charging or bursting. This state is always initiated by a player. The dice
   * occupies some space.
   */
  private case class Locked(initiator: Player, activity: DiceLock, where: Space)
    extends State
  
  private case object Burst extends State
}

object Dice {
  private var serial = 0

  def nextSerial = {
    val before = serial
    serial += 1
    before
  }
}