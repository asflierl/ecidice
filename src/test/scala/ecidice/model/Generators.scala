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

object Generators {
  implicit val arbitraryTile = Arbitrary[Tile](
    for (
      col <- arbitrary[Int];
      row <- arbitrary[Int]
    ) yield Tile(col, row))

  implicit val arbitrarySpace = Arbitrary[Space](
    for (
      t <- arbitrary[Tile];
      l <- oneOf(Level.values.toSeq)
    ) yield Space(t, l))
}
