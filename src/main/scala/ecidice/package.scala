/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package object ecidice {
  def init[A](a: A)(f: A => Any) = { f(a); a }
}
