/*
 * Copyright (c) 2009, Andreas Flierl
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
 * Lists possible transformations of a dice.
 * 
 * @author Andreas Flierl
 */
object Transform extends Enumeration {
  /** Rotates a dice upwards (to the back). */
  val ROTATE_UP = Value
  
  /** Rotates a dice downwards (to the front). */
  val ROTATE_DOWN = Value
  
  /** Rotates a dice to the right. */
  val ROTATE_LEFT = Value
  
  /** Rotates a dice to the left. */
  val ROTATE_RIGHT = Value
  
  /** Spins a dice clock-wise (top and bottom remain unchanged). */
  val SPIN_CLOCKWISE = Value
  
  /** Spins a dice counter-clock-wise (top and bottom remain unchanged). */
  val SPIN_COUNTERCLOCKWISE = Value
  
  /** Flips a dice 180&deg; up (or down, doesn't matter). */
  val FLIP_UP_OR_DOWN = Value
  
  /** Flips a dice 180&deg; left (or right, doesn't matter). */
  val FLIP_LEFT_OR_RIGHT = Value
}
