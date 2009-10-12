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

/**
 * Decorates a floating point number, providing useful non-primitive operations.
 * 
 * @author Andreas Flierl
 */
class FloatDecorator(f: Float) {
  /**
   * Enables the syntax <code>2f to 4f step .1f</code> to generate a stream 
   * "range" for floats. The lower and upper bounds are always included.
   * 
   * @param max the upper bound for the returned stream range
   * @return a "continuous" stream from <code>f</code> to <code>max</code>; its 
   *         <code>step</code> method needs to be called to turn it into a 
   *         discrete stream (as known from the std. library)
   */
  def to(max: Float) = {
    object ContinuousStream {
      def step(by: Float) = {
        def seq(now: Float) : Stream[Float] = 
          if (Math.abs(now) >= Math.abs(max)) Stream.cons(max, Stream.empty) 
          else Stream.cons(now, seq(now + by))
        
        if (max == f) Stream.cons(f, Stream.empty)
        else if ((max > f && by <= 0f) || (max < f && by >= 0f)) Stream.empty
        else seq(f)
      }
    }
    ContinuousStream
  }
}
