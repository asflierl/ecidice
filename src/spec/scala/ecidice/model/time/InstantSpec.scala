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
package time

import ecidice.SpecBase

/**
 * Informal specification of an instant. 
 */
object InstantSpec extends SpecBase {
  "An instant" should {
    "point to time zero by default" in {
      Instant().time mustEqual 0d
    }
    
    "never be negative" in {
      Instant(-42) must throwAn[IllegalArgumentException]
    }
    
    "be comparable" in {
      "instant a" | "instant b" | "result" |>
      Instant(1)  ! Instant(0)  ! 1        |
      Instant(0)  ! Instant(1)  ! -1       |
      Instant(42) ! Instant(42) ! 0        | {
        (a, b, result) => a.compare(b) mustEqual result
      }
    }
    
    "result in a new instant when a duration is added to it" in {
      Instant(42) + Duration(23) mustEqual Instant(65)
    }
    
    "result in a new instant when a duration is subtracted from it" in {
      Instant(42) - Duration(23) mustEqual Instant(19)
    }
    
    "not allow to subtract a duration greater than the instant" in {
      Instant(19) - Duration(23) must throwAn[IllegalArgumentException]
    }
    
    "correctly calculate the duration between 2 instants" in {
      "instant a" | "instant b" | "result"     |>
      Instant(42) ! Instant(23) ! Duration(19) |
      Instant(11) ! Instant(42) ! Duration(31) |
      Instant(5)  ! Instant(5)  ! Duration(0)  | {
        (a, b, result) => a - b mustEqual result
      }
    }
  }
}
