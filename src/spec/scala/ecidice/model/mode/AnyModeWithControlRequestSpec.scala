package ecidice.model
package mode

import ecidice.SpecBase

/**
 * Informal specification of any game mode that supports control requests.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithControlRequestSpec[A <: Mode[A]](game: Mode[A] with ControlRequest[A]) 
extends SpecBase with TestHelpers {
  
  "Any mode that supports control requests" should {

    "not grant control when player stands on an empty tile" in {
      val gameWithOnePlayer = game.spawnPlayer(Tile(0, 0))
      val assignment = gameWithOnePlayer.control(Player(1)).players(Player(1))
      
      assignment mustEqual Standing(Tile(0, 0))
    }
    
    "not grant control over an appearing dice" in {
      val before = game.spawnPlayer(Tile(0, 0))
                       .spawnDice(Tile(0, 0), now)
                       
      val after = before.control(Player(1))
      
      after mustEqual before
    }
    
    "grant control over a single dice on the floor" in {
      val dice = Dice.random
      val location = Tile(0, 0).floor
      
      val testGame = game.spawnPlayer(location.tile)
                         .addSolidDice(location -> dice)
                         .control(Player(1))
      
      testGame.players(Player(1)) aka 
        "assignment of player 1" mustEqual ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" mustEqual SolidControlled(dice, Player(1))
    }
    
    "not grant control over dice already controlled by another player" in {
      val dice = Dice.random
      val location = Tile(0, 0).floor
      
      val testGame = game.spawnPlayer(location.tile)
                         .spawnPlayer(location.tile)
                         .addSolidDice(location -> dice)
                         .control(Player(1))
                         .control(Player(2))
      
      testGame.players(Player(2)) aka
        "assignment of player 2" mustEqual Standing(location.tile)
                         
      testGame.players(Player(1)) aka 
        "assignment of player 1" mustEqual ControllingADice(location)
      
      testGame.board(location) aka
        "contents of dice space" mustEqual SolidControlled(dice, Player(1))
    }
    
    "not grant control over any of 2 stacked dice" in {
      val tile = Tile(0, 0)
      
      val before = game.spawnPlayer(tile)
                       .addSolidDice(tile.floor -> Dice.random)
                       .addSolidDice(tile.raised -> Dice.random)
            
      val after = before.control(Player(1))
      
      after mustEqual before
    }
  }
}
object AnyModeWithControlRequestSpec {
  def apply[A <: Mode[A]](game: Mode[A] with ControlRequest[A]) = 
    new AnyModeWithControlRequestSpec(game)
}