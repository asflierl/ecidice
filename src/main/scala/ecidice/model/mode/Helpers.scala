/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

trait Helpers {
  def isEmpty(c: Contents) = c match {
    case Empty => true
    case _ => false
  }
}
