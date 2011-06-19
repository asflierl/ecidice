/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package util

/**
 * Aims to help with the generation of hash codes (Any#hashCode).
 * 
 * @author Andreas Flierl
 */
object HashCode {
  def apply(args: Any*): Int =
    if (args.isEmpty) 42
    else if (args.length == 1) start(args: _*)
    else (start(args: _*) /: args.tail)(combine)
    
  private def start(args: Any*): Int = 17 + args.head.hashCode
  private def combine(left: Int, right: Any): Int = left * 41 + right.hashCode
}
