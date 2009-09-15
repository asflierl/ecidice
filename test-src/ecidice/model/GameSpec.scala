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
 * Spec-based tests of the game model.
 * 
 * @author Andreas Flierl
 */
class GameSpec extends SpecBase {
  var b : Board = _
  var g : Game = _
  var p1 : Player = _
  var p2 : Player= _
  
  def reset = {
    b = new Board(3, 3)
    g = new Game(2, b)
    p1 = g.players(0)
    p2 = g.players(1)
  }
  
  "A game of ecidice (on a 3 x 3 board)" should {
    doBefore { reset }
    
    def placePlayer(p: Player, pos: (Int, Int)) : Unit =
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
    def placeDice(pos: (Int, Int)) : Dice = {
      val t = b(pos)
      val s = t.floor.content match {
        case Empty => t.floor
        case Occupied(_) => t.raised
        case _ => throw new IllegalStateException("tile not in a state for a"
                                                  + " dice to be placed")
      }
      val d = new Dice
      s.content = Occupied(d)
      d.state = Dice.Solid(s, None)
      d
    }
        
    "when the board is empty" >> {
      "not grant control on any tile" in {
        for (x <- 0 to 2; y <- 0 to 2) {
          reset
          placePlayer(p1, (x, y))
          
          val request = "control request on (%d, %d)".format(x, y)
          g.requestControl(p1) aka request must be (None)
          
          p1.state mustEqual Player.Standing(b(x, y))
        }
      }
    }
    
    "with or without dice on it" >> {  
      "allow a player in the center to move in all directions" in {
        Direction.elements.foreach((dir) => {
          reset
          placePlayer(p1, (1, 1))
          
          val request = "movement request: " + dir
          g.requestMove(p1, dir) aka request must beTrue
          
          p1.state must haveClass[Player.Moving]
        })
      }
      
      
      "correctly handle player movement in the corners" in {
        
        "corner position" | "allowed movement directions"     |>
        (0, 0)            ! (Direction.UP, Direction.RIGHT)   |
        (2, 0)            ! (Direction.UP, Direction.LEFT)    |
        (0, 2)            ! (Direction.DOWN, Direction.RIGHT) |
        (2, 2)            ! (Direction.DOWN, Direction.LEFT)  | {
          
        (corner, allowed) =>
          reset
          for (dir <- Direction.elements) {
            placePlayer(p1, corner)
            g.requestMove(p1, dir) must be (dir == allowed._1 || dir == allowed._2)
          }
        }
      }
      
      "correctly handle player movement at the board edge" in {
        
        "edge position" | "disallowed movement direction" |>
        (1, 0)          ! Direction.DOWN                  |
        (0, 1)          ! Direction.LEFT                  |
        (1, 2)          ! Direction.UP                    |
        (2, 1)          ! Direction.RIGHT                 | {
          
        (pos, disallowed) =>
          reset
          for (dir <- Direction.elements) {
            placePlayer(p1, pos)
            val correct = (dir != disallowed)
            g.requestMove(p1, dir) must be (correct)
          }
        }
        
      }
    }
    
    "with some dice on it" >> {
      "grant control over a single dice on the floor" in {
        placePlayer(p1, (1, 1))
        val d = placeDice(1, 1)
        
        g.requestControl(p1) mustEqual Some(d)
        d.state mustEqual Dice.Solid(b(1,1).floor, Some(p1))
        p1.state mustEqual Player.Controlling(d)
      }
      
      "grant control over the upper of 2 stacked dice" in {
        placePlayer(p1, (1, 1))
        val d1 = placeDice(1, 1)
        val d2 = placeDice(1, 1)
        
        g.requestControl(p1) mustEqual Some(d2)
        d2.state mustEqual Dice.Solid(b(1,1).raised, Some(p1))
        d1.state mustEqual Dice.Solid(b(1,1).floor, None)
        p1.state mustEqual Player.Controlling(d2)
      }
      
      "allow a player to move in any direction with a floor dice from the center" in {
        for (somewhere <- Direction.elements) {
          reset
          placePlayer(p1, (1, 1))
          val d1 = placeDice(1, 1)
          
          g.requestControl(p1) aka "control request" mustEqual Some(d1)
          g.requestMove(p1, somewhere) aka "move " + somewhere must beTrue
        }
      }
      
      "allow a player to move in any direction with an upper dice from the center" in {
        for (somewhere <- Direction.elements) {
          reset
          placePlayer(p1, (1, 1))
          placeDice(1, 1)
          val d1 = placeDice(1, 1)
          
          g.requestControl(p1) aka "control request" mustEqual Some(d1)
          g.requestMove(p1, somewhere) aka "move " + somewhere must beTrue
        }
      }
      
      "allow a player to move with a dice from the floor onto another dice" in {
        placePlayer(p1, (1, 1))
        val d1 = placeDice(1, 1)
        val d2 = placeDice(1, 2)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestMove(p1, Direction.UP) must beTrue
        
        val m = Movement(d1, b(1,1).floor, b(1,2).raised, 
                         g.nowFor(g.MOVE_DURATION), Transform.FLIP_UP_OR_DOWN)
        
        d1.state mustEqual Dice.Moving(m, p1) 
        b(1,1).floor.content mustEqual m
        b(1,2).raised.content mustEqual m
      }
      
      "allow a player to grab the upper of 2 dice and move onto another dice" in {
        placePlayer(p1, (1, 1))
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        val d2 = placeDice(0, 1)
        
        g.requestControl(p1) aka "control request" mustEqual Some(d1)
        g.requestMove(p1, Direction.LEFT) aka "movement request" must beTrue
        
        val m = Movement(d1, b(1,1).raised, b(0,1).raised, 
                         g.nowFor(g.MOVE_DURATION), Transform.ROTATE_LEFT)
        
        d1.state aka "dice state" mustEqual Dice.Moving(m, p1) 
        b(1,1).raised.content aka "start content" mustEqual m
        b(0,1).raised.content aka "destination content" mustEqual m
      }
      
      "allow a player to grab the upper of 2 dice and move to an empty tile" in {
        placePlayer(p1, (1, 1))
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        
        g.requestControl(p1) aka "control request" mustEqual Some(d1)
        g.requestMove(p1, Direction.RIGHT) aka "movement request" must beTrue
        
        val m = Movement(d1, b(1,1).raised, b(2,1).floor, 
                         g.nowFor(g.MOVE_DURATION), Transform.FLIP_LEFT_OR_RIGHT)
        
        d1.state aka "dice state" mustEqual Dice.Moving(m, p1) 
        b(1,1).raised.content aka "start content" mustEqual m
        b(2,1).floor.content aka "destination content" mustEqual m
      }
      
      "not grant control over an appearing dice" in {
        placePlayer(p1, (1, 1))
        g.spawnDice(1, 1) aka "spawning" must beSome[Dice]
        g.requestControl(p1) aka "control request" must beNone
      }
      
      "not allow a player to move onto an appearing dice" in {
        placePlayer(p1, (2, 1))
        val d1 = placeDice(2, 1)
        
        g.spawnDice(1, 1) aka "spawning" must beSome[Dice]
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestMove(p1, Direction.LEFT) must beFalse
      }
      
      "correctly find a group of matching dice" in {
        val d = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
        d(8).change(Transform.ROTATE_UP)
        d(4).change(Transform.ROTATE_UP)
        
        val inc = d.filter(_.top == 6)
        val exc = d.filter(_.top != 6)
        
        val searchResult = g.find(d(0), b(0, 0))
        
        inc.foreach(searchResult must contain(_))
        exc.foreach(searchResult must not contain(_))
      }
      
      "correctly find only one of two groups of matching dice" in {
        val d00 = placeDice(0, 0)
        val d10 = placeDice(1, 0)
        val d02 = placeDice(0, 2)
        val d12 = placeDice(1, 2)
        val d11 = placeDice(1, 1)
        d11.change(Transform.ROTATE_LEFT)
        
        val s = g.find(d12, b(1, 2))
        
        List(d02, d12) foreach (s must contain(_))
        List(d00, d10, d11) foreach (s must not contain(_))
      }
      
      "correctly find a board full of matching dice" in {
        val dice = (for (x <- 0 to 2; y <- 0 to 2) yield placeDice(x, y)).toList
        
        val s = g.find(dice(4), b(1, 1))
        
        dice foreach (s must contain(_))
      }
    }
    
    "with 2 players involved" >> {
      "not grant a player control over a dice at the floor level " +
      "that is already controlled by another player" in {
        placePlayer(p1, (1, 1))
        placePlayer(p2, (1, 1))
        val d1 = placeDice(1, 1)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestControl(p2) must beNone
      }
      
      "not grant a player control over a dice at the raised level " +
      "that is already controlled by another player" in {
        placePlayer(p1, (1, 1))
        placePlayer(p2, (1, 1))
        placeDice(1, 1)
        val d1 = placeDice(1, 1)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestControl(p2) must beNone
      }
      
      "not let a player move with a dice from the floor onto a " +
      "dice that is controlled by another player" in {
        placePlayer(p1, (1, 1))
        placePlayer(p2, (2, 1))
        
        val d1 = placeDice(1, 1)
        val d2 = placeDice(2, 1)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestControl(p2) mustEqual Some(d2)
        
        g.requestMove(p2, Direction.LEFT) must beFalse
      }
      
      "not let a player move with a dice from the raised level " +
      "onto a dice that is controlled by another player" in {
        placePlayer(p1, (1, 1))
        placePlayer(p2, (2, 1))
        
        val d1 = placeDice(1, 1)
        placeDice(2, 1)
        val d2 = placeDice(2, 1)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestControl(p2) mustEqual Some(d2)
        
        g.requestMove(p2, Direction.LEFT) must beFalse
      }
      
      "not let 2 players move onto the same floor space" in {
        placePlayer(p1, (0, 2))
        placePlayer(p2, (1, 1))
        
        placeDice(0, 2)
        val d1 = placeDice(0, 2)
        val d2 = placeDice(1, 1)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestControl(p2) mustEqual Some(d2)
        
        g.requestMove(p1, Direction.RIGHT) must beTrue
        g.requestMove(p2, Direction.UP) must beFalse
      }
      
      "not let 2 players move onto the same raised space" in {
        placePlayer(p1, (0, 0))
        placePlayer(p2, (1, 1))
        
        val d1 = placeDice(0, 0)
        placeDice(1, 1)
        val d2 = placeDice(1, 1)
        
        placeDice(0, 1)
        
        g.requestControl(p1) mustEqual Some(d1)
        g.requestControl(p2) mustEqual Some(d2)
        
        g.requestMove(p1, Direction.UP) must beTrue
        g.requestMove(p2, Direction.LEFT) must beFalse
      }

    }
  }
}
