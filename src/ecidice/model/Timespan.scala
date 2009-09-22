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

import ecidice.util.HashCode

/**
 * Represents a period of time, i.e. the time interval [start, end]
 * 
 * @author Andreas Flierl
 * 
 * @param clock the clock whose time this timespan is relative to
 * @param start the instant this timespan starts
 * @param ttl the initial time-to-live (aka duration)
 */
class Timespan(clock: Clock, val start: Double, private val ttl: Double) {
  private var to = start + ttl
  
  def end = to

  def lengthen(amount: Double) {
    if (amount > 0d) {
      to += amount
    }
  }
  
  def isOver = (progress == 1f)
  
  /**
   * Returns where in this timespan the associated game is now as a number in
   * the interval [0, 1].
   */
  def progress =
    if (clock.now <= start) 0f
    else if (clock.now >= to) 1f
    else ((clock.now - start) / (to - start)).floatValue
  
  /**
   * Note that this definition of equality uses the initialization parameters
   * not current (mutable) state.
   */
  override def equals(obj: Any) = obj match {
    case x : Timespan => (x.start == start) && (x.ttl == ttl)
    case _ => false
  }
  
  /**
   * Along the same lines as <code>equals</code>, this method uses the 
   * initialization parameters not current (mutable) state to compute the hash
   * code.
   */
  override def hashCode = HashCode(start, ttl)
}
