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

import ecidice.util.NotYetImplementedException
import scala.util.MurmurHash
import scalaz.ValidationNEL
import scalaz.Validation.failure
import scalaz.Validation.validationMonad
import scalaz.NonEmptyList
import scalaz.NonEmptyList.nel

package object ecidice {
  type =/>[-A, +B] = PartialFunction[A, B]
  
  type Valid[A] = ValidationNEL[String, A]
  
  def init[A](a: A)(f: A => Any) = { f(a); a }
  
  def ??? = throw new NotYetImplementedException
  
  private[this] val seed = "ecidice"##
  
  def murmurHash[A](args: A*): Int = {
    val generator = new MurmurHash[A](seed)
    args foreach generator
    generator hash
  }
  
  //implicit val gameValidationMonad = scalaz.Validation.validationMonad[scalaz.NonEmptyList[String]]
  
  /** TODO evaluate facilitating something like this:
    * import scalaz._
    * import scalaz.syntax.monad._
    *  
    * val y = success(game) ∘
    *         spawnPlayer(Tile(0, 2)) ∘
    *         addSolidDie(Tile(0, 2).floor) >>=
    *         control(Player(1))
    */
}
