/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice

import org.specs2._
import mock._
import matcher._
import specification._

trait UnitSpec extends mutable.Specification
                  with DataTables 
                  with Mockito 
                  with ScalaCheck 
                  with SpecHelpers
