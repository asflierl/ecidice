/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice

import org.specs2.matcher._

trait SpecHelpers { this: DataTables =>
  val floatDelta = 1E-6f
  val delta = 1E-12d
  implicit def enrichString(s: String) = new EnrichedString(s)
}

class EnrichedString(pre: String) {
  def /(post: String) = pre + "\n" + post
}
