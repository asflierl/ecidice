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

package ecidice
package model
package time

import ecidice.SpecBase

/**
 * Informal specification of a duration. 
 */
object DurationSpec extends SpecBase {
  "A duration" should {
    "take 0 seconds by default" in {
      Duration().seconds mustEqual 0d
    }
    
    "never be negative" in {
      Duration(-42) must throwAn[IllegalArgumentException]
    }
    
    "result in a new duration when a duration is added to it" in {
      Duration(42) + Duration(7) mustEqual Duration(49)
    }
    
    "result in an instant when an instant is added to it" in {
      Duration(42) + Instant(23) mustEqual Instant(65)
    }
    
    "be comparable" in {
      "duration a" | "duration b" | "result" |>
      Duration(1)  ! Duration(0)  ! 1        |
      Duration(0)  ! Duration(1)  ! -1       |
      Duration(42) ! Duration(42) ! 0        | {
        (a, b, result) => a.compare(b) mustEqual result
      }
    }
  }
}
