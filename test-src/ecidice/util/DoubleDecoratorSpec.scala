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

package ecidice.util

class DoubleDecoratorSpec extends SpecBase {
  "A double decorator" should {
    val deco = new DoubleDecorator(0d)
    
    "produce a correct upwards stream" in {
      val stream = deco to 1d step .5d
      
      stream.toList mustEqual List(0d, .5d, 1d)
    }
    
    "produce a correct downwards stream" in {
      val stream = deco to -1.2d step -.4d
      
      stream.toList mustEqual List(0d, -.4d, -.8d, -1.2d)
    }
    
    "produce an empty stream if target is unreachable" in {
      (deco to 1d step -.1d) aka "0 to 1 step -0.1" must beEmpty
      (deco to -1d step .1d) aka "0 to -1 step 0.1" must beEmpty
    }
    
    "produce a single-element stream if start equals target" in {
      (deco to 0d step 1d).toList aka "0 to 0 step 1" mustEqual List(0d)
      (deco to 0d step 0d).toList aka "0 to 0 step 0" mustEqual List(0d)
      (deco to 0d step -1d).toList aka "0 to 0 step -1" mustEqual List(0d)
    }
  }
}
