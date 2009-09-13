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

import org.scalatest._
import org.scalatest.matchers._

/**
 * Spec-based tests of the game model.
 * 
 * @author Andreas Flierl
 */
class GameOld extends Spec with Assertions with ShouldMatchers 
    with BeforeAndAfter {
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
    
  describe("A game of ecidice (on a 3 x 3 board)") {
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
      
      it("should grant control over the upper of 2 stacked dice") {
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
        
        it("should allow a player to grab the upper of 2 dice at the center tile " 
            + "and move %s with it".format(dir)) {
          placePlayer(p1, 1, 1)
          val d1 = placeDice(1, 1)
          val d2 = placeDice(1, 1)
            
          g.requestControl(p1) should be (Some(d2))
          g.requestMove(p1, dir) should be (true)
        }
      }
      
      it("should allow a player to grab a dice from the floor and move onto another dice") {
        placePlayer(p1, 1, 1)
        val d1 = placeDice(1, 1)
        val d2 = placeDice(1, 2)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestMove(p1, Direction.UP) should be (true)
        
        val m = Movement(d1, b(1,1).floor, b(1,2).raised, 
                         g.nowFor(g.MOVE_DURATION), Transform.FLIP_UP_OR_DOWN)
        
        d1.state should be (Dice.Moving(m, p1)) 
        b(1,1).floor.content should be(m)
        b(1,2).raised.content should be(m)
      }
      
      it("should allow a player to grab the upper of 2 dice and move onto another dice") {
        placePlayer(p1, 1, 1)
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        val d2 = placeDice(0, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestMove(p1, Direction.LEFT) should be (true)
        
        val m = Movement(d1, b(1,1).raised, b(0,1).raised, 
                         g.nowFor(g.MOVE_DURATION), Transform.ROTATE_LEFT)
        
        d1.state should be (Dice.Moving(m, p1)) 
        b(1,1).raised.content should be(m)
        b(0,1).raised.content should be(m)
      }
      
      it("should allow a player to grab the upper of 2 dice and move to an empty tile") {
        placePlayer(p1, 1, 1)
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestMove(p1, Direction.RIGHT) should be (true)
        
        val m = Movement(d1, b(1,1).raised, b(2,1).floor, 
                         g.nowFor(g.MOVE_DURATION), Transform.FLIP_LEFT_OR_RIGHT)
        
        d1.state should be (Dice.Moving(m, p1)) 
        b(1,1).raised.content should be(m)
        b(2,1).floor.content should be(m)
      }
      
      it("should not grant control over an appearing dice") {
        placePlayer(p1, 1, 1)
        val d1 = g.spawnDice(1, 1).getOrElse(fail("dice could not be spawned"))
        g.requestControl(p1) should be (None)
      }
      
      it("should not allow a player to move onto an appearing dice") {
        placePlayer(p1, 2, 1)
        val d1 = placeDice(2, 1)
        
        val d2 = g.spawnDice(1, 1).getOrElse(fail("dice could not be spawned"))
        
        g.requestControl(p1) should be (Some(d1))
        g.requestMove(p1, Direction.LEFT) should be (false)
      }
      
      it("should correctly find a group of matching dice") {
        val d = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
        d(8).change(Transform.ROTATE_UP)
        d(4).change(Transform.ROTATE_UP)
        
        val inc = d.filter(_.top == 6)
        val exc = d.filter(_.top != 6)
        
        val s = g.find(d(0), b(0, 0))
        
        inc.foreach(s should contain(_))
        exc.foreach(s should not contain(_))
      }
      
      it("should correctly find only one of two groups of matching dice") {
        val d00 = placeDice(0, 0)
        val d10 = placeDice(1, 0)
        val d02 = placeDice(0, 2)
        val d12 = placeDice(1, 2)
        val d11 = placeDice(1, 1)
        d11.change(Transform.ROTATE_LEFT)
        
        val s = g.find(d12, b(1, 2))
        
        List(d02, d12) foreach (s should contain(_))
        List(d00, d10, d11) foreach (s should not contain(_))
      }
      
      it("should correctly find a board full of matching dice") {
        val dice = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
        
        val s = g.find(dice(4), b(1, 1))
        
        dice foreach (s should contain(_))
      }
    }
    
    describe("with 2 players involved") {
      it("should not grant a player control over a dice at the floor level " +
         "that is already controlled by another player") {
        placePlayer(p1, 1, 1)
        placePlayer(p2, 1, 1)
        val d1 = placeDice(1, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestControl(p2) should be (None)
      }
      
      it("should not grant a player control over a dice at the raised level " +
         "that is already controlled by another player") {
        placePlayer(p1, 1, 1)
        placePlayer(p2, 1, 1)
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestControl(p2) should be (None)
      }
      
      it("should not let a player move with a dice from the floor onto a " +
         "dice that is controlled by another player") {
        placePlayer(p1, 1, 1)
        placePlayer(p2, 2, 1)
        
        val d1 = placeDice(1, 1)
        val d2 = placeDice(2, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestControl(p2) should be (Some(d2))
        
        g.requestMove(p2, Direction.LEFT) should be (false)
      }
      
      it("should not let a player move with a dice from the raised level " +
         "onto a dice that is controlled by another player") {
        placePlayer(p1, 1, 1)
        placePlayer(p2, 2, 1)
        
        val d1 = placeDice(1, 1)
        placeDice(2, 1)
        val d2 = placeDice(2, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestControl(p2) should be (Some(d2))
        
        g.requestMove(p2, Direction.LEFT) should be (false)
      }
      
      it("should not let 2 players move onto the same floor space") {
        placePlayer(p1, 0, 2)
        placePlayer(p2, 1, 1)
        
        placeDice(0, 2)
        val d1 = placeDice(0, 2)
        val d2 = placeDice(1, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestControl(p2) should be (Some(d2))
        
        g.requestMove(p1, Direction.RIGHT) should be (true)
        g.requestMove(p2, Direction.UP) should be (false)
      }
      
      it("should not let 2 players move onto the same raised space") {
        placePlayer(p1, 0, 0)
        placePlayer(p2, 1, 1)
        
        val d1 = placeDice(0, 0)
        placeDice(1, 1)
        val d2 = placeDice(1, 1)
        
        placeDice(0, 1)
        
        g.requestControl(p1) should be (Some(d1))
        g.requestControl(p2) should be (Some(d2))
        
        g.requestMove(p1, Direction.UP) should be (true)
        g.requestMove(p2, Direction.LEFT) should be (false)
      }
      

    }
  }
}
