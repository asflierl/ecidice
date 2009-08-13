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

class GameSpec extends TestBase {
  private var b : Board = _
  private var g : Game = _
  private var p1 : Player = _
  private var p2 : Player = _
  
  override def beforeEach() = {
    b = new Board(3, 3)
    g = new Game(2, b)
    p1 = g.players(0)
    p2 = g.players(1)
  }
  
  /**
   * Helper method that places a player at a position.
   * 
   * @param p the player to place
   * @param x the horizontal component of the target position
   * @param y the depth component of the target position
   */
  private def placePlayer(p: Player, x: Int, y: Int) : Unit = 
    p.state = Player.Standing(b(x, y))
  
  /**
   * Same as the above, only with a coordinate tuple for the position.
   */
  private def placePlayer(p: Player, pos: (Int, Int)) : Unit =
    p.state = Player.Standing(b(pos))
  
  /**
   * Helper method that creates a new dice and places it at the topmost 
   * available space at the specified position or throws an exception if this
   * fails.
   * 
   * @param x the horizontal component of the position to place the dice
   * @param y the depth component of the position to place the dice
   * @return the newly created and placed dice
   */
  private def placeDice(x: Int, y: Int) : Dice = {
    val t = b(x, y)
    val s = t.floor.content match {
      case Empty => t.floor
      case Occupied(_) => t.raised
      case _ => throw new IllegalStateException("tile not in a state for a dice to be placed")
    }
    val d = new Dice
    s.content = Occupied(d)
    d.state = Dice.Solid(s, None)
    d
  }
  
  /**
   * Same as the above, only with a coordinate tuple for the position.
   */
  private def placeDice(pos: (Int, Int)) : Unit = placeDice(pos._1, pos._2)
    
  describe("The game (3 x 3 board)") {
    describe("when the board is empty") {
      it("should not grant control on any tile") {
        for (x <- 0 to 2; y <- 0 to 2) {
          placePlayer(p1, x, y)
          
          expect(None, "pos: " + (x, y))(g.requestControl(p1))
          p1.state should be (Player.Standing(b(x, y)))
        }
      }
    }
    
    describe("with or without dice on it") {  
      it("should allow a player in the center to move in all directions") {
        Direction.elements.foreach((dir) => {
          placePlayer(p1, 1, 1)
          
          assert(g.requestMove(p1, dir), "dir: " + dir)
          p1.state should have ('class (classOf[Player.Moving]))
        })
      }
      
      // these moves should be allowed
      val allowed = List(((0,0), Direction.UP, Direction.RIGHT),
             ((2,0), Direction.UP, Direction.LEFT),
             ((0,2), Direction.DOWN, Direction.RIGHT),
             ((2,2), Direction.DOWN, Direction.LEFT))
      
      for (tc <- allowed; dir <- Direction.elements) {
        it("should correctly handle a player at %s moving %s".format(tc._1, dir)) {
          placePlayer(p1, tc._1)
          
          g.requestMove(p1, dir) should be (dir == tc._2 || dir == tc._3)
        }
      }
      
      //these moves should not be allowed
      val notAllowed = List(((1,0), Direction.DOWN), ((0,1), Direction.LEFT),
           ((1,2), Direction.UP), ((2,1), Direction.RIGHT))
      
      for (tc <- notAllowed; dir <- Direction.elements) {
        it("should correctly handle a player at %s moving %s".format(tc._1, dir)) {
          placePlayer(p1, tc._1)
          g.requestMove(p1, dir) should be (dir != tc._2)
        }
      }
    }
    
    describe("with some dice on it") {
      it("should grant control over a single dice on the floor") {
        placePlayer(p1, 1, 1)
        val d = placeDice(1, 1)
        
        g.requestControl(p1) should be (Some(d))
        d.state should be (Dice.Solid(b(1,1).floor, Some(p1)))
        p1.state should be (Player.Controlling(d))
      }
      
      it("should grant control over the topmost of 2 stacked dice") {
        placePlayer(p1, 1, 1)
        val d1 = placeDice(1, 1)
        val d2 = placeDice(1, 1)
        
        g.requestControl(p1) should be (Some(d2))
        d2.state should be (Dice.Solid(b(1,1).raised, Some(p1)))
        d1.state should be (Dice.Solid(b(1,1).floor, None))
        p1.state should be (Player.Controlling(d2))
      }
      
      for (dir <- Direction.elements) {
        it("should allow a player to grab the only dice at the center tile " 
            + "and move %s with it".format(dir)) {
          placePlayer(p1, 1, 1)
          val d1 = placeDice(1, 1)
            
          g.requestControl(p1) should be (Some(d1))
          g.requestMove(p1, dir) should be (true)
        }
        
        it("should allow a player to grab the top of 2 dice at the center tile " 
            + "and move %s with it".format(dir)) {
          placePlayer(p1, 1, 1)
          val d1 = placeDice(1, 1)
          val d2 = placeDice(1, 1)
            
          g.requestControl(p1) should be (Some(d2))
          g.requestMove(p1, dir) should be (true)
        }
      }
      
      it("should allow a player to grab a tile from the floor and move onto another") {
        placePlayer(p1, 1, 1)
        val d1 = placeDice(1, 1)
        val d2 = placeDice(1, 2)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestMove(p1, Direction.UP) should be (true)
        p1.state should be (Player.Controlling(d1))
        
        val m = Movement(d1, b(1,1).floor, b(1,2).raised, 
                         g.nowFor(g.MOVE_DURATION), Transform.FLIP_UP_OR_DOWN)
        
        d1.state should be (Dice.Moving(m, p1)) 
        b(1,1).floor.content should be(m)
        b(1,2).raised.content should be(m)
      }
    }
  }
}
