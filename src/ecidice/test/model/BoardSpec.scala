package ecidice.test.model

import ecidice.model._

class GameSpec extends TestBase {
  private val SIZE = 10

  private var g : Game = _
  
  override def beforeEach() = {
    g = new Game(1)
  }
  
  describe("The game") {
    describe("when the board is empty") {
      it("should not grant control on any tile") {
        /*
        for (x <- Stream.range(0, SIZE)) {
          for (y <- Stream.range(0, SIZE)) {
            p.x = x
            p.y = y
            g.requestControl(p) should be (false)
          }
        } */
      }
    }
  }
}
