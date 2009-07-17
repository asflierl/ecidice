package de.i0n.burst.test.model

import org.scalatest._
import org.scalatest.matchers._

import de.i0n.burst.model._

class BoardSpec extends Spec with ShouldMatchers with BeforeAndAfter 
    with BurstTest {
  private val SIZE = 10
      
  private val p = new Player()
  private var b : Board = _
  
  override def beforeEach() = {
    b = new Board(SIZE, SIZE)
  }
  
  describe("The game board") {
    describe("when empty") {
      it("should not grant control on any tile") {
        for (x <- Stream.range(0, SIZE)) {
          for (y <- Stream.range(0, SIZE)) {
            b.requestControl(x, y, p) should be (false)
          }
        }
      }
    }
  }
}
