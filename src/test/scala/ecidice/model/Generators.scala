/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import org.scalacheck._
import Prop.forAll
import Arbitrary.arbitrary
import Gen._

/**
 * Defines generators for various model classes to be used with ScalaCheck
 * specifications.
 */
object Generators {
  implicit def arbitraryTile: Arbitrary[Tile] = Arbitrary(
    for (
      col <- arbitrary[Int];
      row <- arbitrary[Int]
    ) yield Tile(col, row))

  implicit def arbitrarySpace: Arbitrary[Space] = Arbitrary(
    for (
      t <- arbitrary[Tile];
      l <- oneOf(Level.values.toSeq)
    ) yield Space(t, l))
}
