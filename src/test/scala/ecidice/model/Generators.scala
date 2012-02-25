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
import time._

object Generators {
  implicit val tiles = Arbitrary[Tile](
    for {
      col <- arbitrary[Int]
      row <- arbitrary[Int]
    } yield Tile(col, row))

  implicit val spaces = Arbitrary[Space](
    for {
      t <- arbitrary[Tile]
      l <- oneOf(Level.values.toSeq)
    } yield Space(t, l))
    
  implicit val instants = Arbitrary[Instant](positive(Double) map Instant)
    
  implicit val durations = Arbitrary[Duration](positive(Double) map Duration)
  
  implicit val timespans = Arbitrary[Timespan](
    for {
      i <- arbitrary[Instant]
      d <- arbitrary[Duration]
    } yield Timespan(i, d))
    
  type Finite[A] = { def MaxValue: A; def MinValue: A }
  
  def positive[A](bounds: Finite[A])(implicit n: Numeric[A], c: Choose[A]): Gen[A] =
    chooseNum(n.zero, bounds.MaxValue)
}
