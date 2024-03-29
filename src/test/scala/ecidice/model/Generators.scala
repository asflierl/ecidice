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

import org.scalacheck.{ Gen, Arbitrary }, Gen._
import time._

object Generators {
  implicit val tiles = Arbitrary[Tile](resultOf(Tile))
  implicit val levels = Arbitrary[Level](enumerated(Level))
  implicit val spaces = Arbitrary[Space](resultOf(Space))
  implicit val instants = Arbitrary[Instant](positive(Double) map Instant)
  implicit val durations = Arbitrary[Duration](positive(Double) map Duration)
  implicit val timespans = Arbitrary[Timespan](resultOf(Timespan))

  def enumerated[A](c: {def values: Set[A]}): Gen[A] = oneOf(c.values.toSeq)
  
  type Finite[A] = { def MaxValue: A; def MinValue: A }
  
  def positive[A](bounds: Finite[A])(implicit n: Numeric[A], c: Choose[A]): Gen[A] =
    chooseNum(n.zero, bounds.MaxValue)
}
