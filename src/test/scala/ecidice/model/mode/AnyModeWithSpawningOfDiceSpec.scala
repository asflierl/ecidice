/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import ecidice.UnitSpec

/**
 * Informal specification of any game mode that supports control requests.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithSpawningOfDiceSpec[A <: Mode[A] with SpawningOfDice[A]](game: A) 
extends UnitSpec with ModelTestHelpers {
  
  "Any mode that supports the spawning of dice" should {

    "spawn a new dice on an empty tile" in {
      val tile = Tile(1, 2)
      val dice = Dice(6, 5, 4)
      
      val contents = game.spawnDice(tile, now, dice)
                         .board(tile.floor)
                         
      contents aka "contents" must be equalTo DiceAppearing(dice, tile.floor, now)
    }
    
    "not spawn a dice on a tile with an appearing dice" in {
      val tile = Tile(1, 0)
      val before = game.spawnDice(tile, now)
      val after = before.spawnDice(tile, now)
      
      after must be equalTo before
    }
    
    "not spawn a dice on a tile with a solid dice" in {
      val tile = Tile(1, 0)
      val before = game.addSolidDice(tile.floor)
      val after = before.spawnDice(tile, now)
      
      after must be equalTo before
    }
    
    "not spawn a dice on a tile with two solid dice" in {
      val tile = Tile(1, 0)
      val before = game.addSolidDice(tile.floor)
                       .addSolidDice(tile.raised)
      val after = before.spawnDice(tile, now)
      
      after must be equalTo before
    }
    
    //TODO specify interaction with movement
  }
}
object AnyModeWithSpawningOfDiceSpec {
  def apply[A <: Mode[A] with SpawningOfDice[A]]()(implicit game: A) = 
    new AnyModeWithSpawningOfDiceSpec(game)
}
