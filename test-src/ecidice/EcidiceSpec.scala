package ecidice

import org.specs._
import org.specs.runner._
 
class EcidiceSpec extends SpecificationWithJUnit {
  "ecidice".isSpecifiedBy(
    new model.BoardSpec,
    new model.DiceSpec,
    new model.GameSpec,
    new model.TimespanSpec
  )
}
