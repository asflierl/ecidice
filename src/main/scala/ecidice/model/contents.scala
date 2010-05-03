/*
 * Copyright (c) 2009-2010 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.model

/**
 * Marker trait for things that can occupy a space. 
 */
sealed trait Contents {
  protected def maybeBursting: Option[Bursting]
  def remembersBursting: Boolean = maybeBursting isDefined
  def bursting = maybeBursting.getOrElse(() => throw new IllegalStateException)
}

case class Empty(maybeBursting: Option[Bursting]) extends Contents

case class Appearing(
  activity: DiceAppearing,
  maybeBursting: Option[Bursting]
) extends Contents

case class Solid(
  dice: Dice,
  maybeBursting: Option[Bursting]
) extends Contents

case class SolidControlled(
  dice: Dice,
  controller: Player,
  maybeBursting: Option[Bursting]
) extends Contents

case class Moving(
  activity: DiceMovement,
  maybeBursting: Option[Bursting]
) extends Contents

case class Charging(dice: Dice) // FIXME dice locks/groups should be managed globally
case class Bursting(dice: Dice) // FIXME same here
