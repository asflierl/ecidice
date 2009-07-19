package de.i0n.ecidice.test.model

import org.scalatest._
import org.scalatest.matchers._

import de.i0n.ecidice.model._

class GameSpec extends Spec with ShouldMatchers with BeforeAndAfter 
    with EcidiceTest {
  private val SIZE = 10
      
  private val p = new Player()
  private var g : Game = _
  
  override def beforeEach() = {
    g = new Game
  }
  
  describe("The game") {
    describe("when the board is empty") {
      it("should not grant control on any tile") {
        for (x <- Stream.range(0, SIZE)) {
          for (y <- Stream.range(0, SIZE)) {
            g.requestControl(x, y, p) should be (false)
          }
        }
      }
    }
  }
}
