/*
 * Copyright (c) 2009-2012 Andreas Flierl
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

package ecidice
package model

import time._

/** Indicates that a game event is timed and needs to be tracked/managed. */
sealed trait Activity {
  def start: Instant
  def duration: Duration
  
  val time = Timespan(start, duration)
}

case class DieAppearing(
  die: Die,
  location: Space,
  start: Instant
) extends Activity with Contents {
  def duration = DieAppearing.duration
}
object DieAppearing extends ((Die, Space, Instant) => DieAppearing) {
  val duration = Duration(5d)
}

case class DieMovement(
  die: Die,
  origin: Space,
  destination: Space, 
  transform: Transform,
  controller: Player,
  start: Instant
) extends Activity with Contents {
  def duration = DieMovement.duration
}
object DieMovement extends ((Die, Space, Space, Transform, Player, Instant) => DieMovement) {
  val duration = Duration(.25d)
}

case class DieFalling(
  die: Die,
  location: Tile,
  controller: Option[Player],
  start: Instant
) extends Activity with Contents {
  def duration = DieFalling.duration
}
object DieFalling extends ((Die, Tile, Option[Player], Instant) => DieFalling){
  val duration = Duration(.25d)
}

sealed trait DieLock[T <: DieGroup[T]] extends Activity {
  def group: T
}

case class ChargeLock(
  group: ChargeGroup,
  start: Instant
) extends DieLock[ChargeGroup] {
  def duration = ChargeLock.duration
}
object ChargeLock extends ((ChargeGroup, Instant) => ChargeLock){
  val duration = Duration(10d)
}

case class BurstLock(
  group: BurstGroup,
  start: Instant
) extends DieLock[BurstGroup] {
  def duration = BurstLock.duration
}
object BurstLock extends ((BurstGroup, Instant) => BurstLock) {
  val duration = Duration(1d)
}

case class PlayerMovement(
  player: Player,
  origin: Tile,
  destination: Tile,
  start: Instant
) extends Activity {
  def duration = PlayerMovement.duration
}
object PlayerMovement extends ((Player, Tile, Tile, Instant) => PlayerMovement) {
  val duration = Duration(.25d)
}
