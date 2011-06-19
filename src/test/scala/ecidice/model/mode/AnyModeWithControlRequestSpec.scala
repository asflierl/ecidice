/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import ecidice.UnitSpec

/**
 * Informal specification of a player's request to control a dice.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithControlRequestSpec[A <: Mode[A] with ControlRequest[A]](game: A) 
extends UnitSpec with ModelTestHelpers {
  
  "Any mode that allows a player to control a dice" should {

    "not grant control when player stands on an empty tile" in {
      val gameWithOnePlayer = game.spawnPlayer(Tile(0, 0))
      val assignment = gameWithOnePlayer.control(Player(1)).players(Player(1))
      
      assignment must be equalTo Standing(Tile(0, 0))
    }
    
    "not grant control over an appearing dice" in {
      val before = game.spawnPlayer(Tile(0, 0))
                       .spawnDice(Tile(0, 0), now)
                       
      val after = before.control(Player(1))
      
      after must be equalTo before
    }
    
    "grant control over a single dice on the floor" in {
      val dice = Dice.random
      val location = Tile(0, 0).floor

      val testGame = game.spawnPlayer(location.tile)
                         .addSolidDice(location -> dice)
                         .control(Player(1))
      
      testGame.players(Player(1)) aka 
        "assignment of player 1" must be equalTo ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" must be equalTo SolidControlled(dice, Player(1))
    }
    
    "not grant control over dice on the floor level already controlled by another player" in {
      val dice = Dice.random
      val location = Tile(0, 0).floor
      
      val testGame = game.spawnPlayer(location.tile)
                         .spawnPlayer(location.tile)
                         .addSolidDice(location -> dice)
                         .control(Player(1))
                         .control(Player(2))
      
      testGame.players(Player(2)) aka
        "assignment of player 2" must be equalTo Standing(location.tile)
                         
      testGame.players(Player(1)) aka 
        "assignment of player 1" must be equalTo ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" must be equalTo SolidControlled(dice, Player(1))
    }
    
    "grant control over the upper of 2 stacked dice" in {
      val lowerDice, upperDice = Dice.random
      val tile = Tile(0, 0)
      
      val testGame = game.spawnPlayer(tile)
                         .addSolidDice(tile.floor -> lowerDice)
                         .addSolidDice(tile.raised -> upperDice)
                         .control(Player(1))
      
      testGame.players(Player(1)) aka 
        "assignment of player 1" must be equalTo ControllingADice(tile.raised)
      
      testGame.board(tile.floor) aka
        "contents of floor space" must be equalTo lowerDice
      
      testGame.board(tile.raised) aka
        "contents of raised space" must be equalTo SolidControlled(upperDice, Player(1))
    }
    
    "not grant control over dice on the raised level already controlled by another player" in {
      val dice = Dice.random
      val location = Tile(0, 0).raised
      
      val testGame = game.spawnPlayer(location.tile)
                         .spawnPlayer(location.tile)
                         .addSolidDice(location.floor -> Dice.random)
                         .addSolidDice(location -> dice)
                         .control(Player(2))
                         .control(Player(1))
      
      testGame.players(Player(1)) aka
        "assignment of player 1" must be equalTo Standing(location.tile)
                         
      testGame.players(Player(2)) aka 
        "assignment of player 2" must be equalTo ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" must be equalTo SolidControlled(dice, Player(2))
    }
    
    //TODO specifiy movement cases
  }
}

object AnyModeWithControlRequestSpec {
  def apply[A <: Mode[A] with ControlRequest[A]]()(implicit game: A) = 
    new AnyModeWithControlRequestSpec(game)
}
