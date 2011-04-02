/*
 * Copyright (c) 2009-2011 Andreas Flierl
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

import Direction._

/**
 * Lists possible transformations of a dice.
 * 
 * @author Andreas Flierl
 */
object Transform extends Enumeration {
  val RotateBackward = Value("rotate backward")
  val RotateForward = Value("rotate forward")
  val RotateLeft = Value("rotate left")
  val RotateRight = Value("rotate right")
  val SpinClockwise = Value("spin clockwise")
  val SpinCounterclockwise = Value("spin counterclockwise")
  val FlipUpOrDown = Value("flip up or down")
  val FlipLeftOrRight = Value("flip left or right")
  
  def apply(from: Space, to: Space, dir: Direction.Value): Transform.Value = {
    if (from.isFloor == to.isFloor) dir match {
      case Backward => RotateBackward
      case Forward => RotateForward
      case Right => RotateRight
      case Left => RotateLeft
    } else dir match {
      case Backward | Forward => FlipUpOrDown
      case Left | Right => FlipLeftOrRight
    }
  }
}
