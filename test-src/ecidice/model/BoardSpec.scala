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
 * Spec-based tests of the board model.
 * 
 * @author Andreas Flierl
 */
class BoardSpec extends TestBase {
  private val width = 5
  private val depth = 3
  private var b : Board = _
  
  override def beforeEach() = {
    b = new Board(width, depth)
  }
  
  describe("A %d x %d board".format(width, depth)) {
    "asd %d".format(1)
    
    it("should return its tiles in the right order") {
      val iter = b.tiles
      
      for (x <- 0 until width; y <- 0 until depth) {
        iter.hasNext should be (true)
        iter.next should be(b(x, y))
      }
      
      iter.hasNext should be (false)
    }
    
    List( (Direction.UP,    (2, 2)),
          (Direction.DOWN,  (2, 0)),
          (Direction.LEFT,  (1, 1)),  
          (Direction.RIGHT, (3, 1)) ).foreach((tc) =>
      it("should return the correct position after moving %s from the center".format(tc._1.toString)) {
        b.positionInDir(b(2, 1), tc._1) should be(tc._2)
    })
  }
}
