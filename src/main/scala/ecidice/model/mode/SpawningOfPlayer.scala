/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

trait SpawningOfPlayer[A <: Mode[A]] { this: A =>
  def spawnPlayer(there: Tile) =
    dupe(players = players + (Player(nextPlayerID) -> Standing(there))) 
  
  private def nextPlayerID = 
    if (players isEmpty) 1
    else players.keys.max.id + 1
}
