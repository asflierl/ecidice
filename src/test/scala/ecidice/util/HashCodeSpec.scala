/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package util

import ecidice.UnitSpec

import org.specs2._

object HashCodeSpec extends UnitSpec {
  "The hash code object" should {
    "return a default value if no parameters are given" in {
      HashCode() must be equalTo 42
    }
    
    "calculate a good hash code for a single given parameter" in {
      HashCode(25) must be equalTo 17 + 25
    }
    
    "calculate a good hash code for several given parameters" in {
      HashCode(1, 2L, " ") must be equalTo (((17 + 1) * 41 + 2) * 41 + 32)
    }
    
    "be equal if the objects passed to it are equal" in {
      def hash = HashCode(42, " ", Some(new String("x")))
      
      hash must be equalTo hash
    }
  }
}
