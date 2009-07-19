package ecidice.test

import org.scalatest._
import org.scalatest.matchers._

trait TestBase extends Spec with ShouldMatchers with BeforeAndAfter {
  /** Tolerance for float values in the tests. */
  val DELTA = 1e-5f
}
