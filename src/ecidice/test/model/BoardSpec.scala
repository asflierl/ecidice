package ecidice.test.model

import ecidice.model._

class GameSpec extends TestBase {
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
