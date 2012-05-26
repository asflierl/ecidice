package ecidice
package visual

import com.jme3.asset.AssetManager
import com.jme3.font.BitmapText
import com.jme3.system.Timer
import com.jme3.font.BitmapFont
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.app.Application
import com.jme3.scene.Node
import com.jme3.scene.shape.Box
import com.jme3.scene.Geometry
import com.jme3.material.Material
import com.jme3.math.Vector3f.ZERO
import ecidice.model._
import ecidice.model.mode._
import com.jme3.scene.shape.Quad
import jme3tools.optimize.GeometryBatchFactory
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.math.FastMath.DEG_TO_RAD
import scala.util.Random.nextInt

final class GameController extends Controller {
  case class Context(app: UpdateLoop, gameRoot: Node, boardView: BoardView)
  
  private[this] val g = Gauntlet(Board.sized(10, 10) -- Seq(Tile(0, 0), Tile(2, 1)), Set(), Map())
  
  override def init(asm: AppStateManager, app: UpdateLoop): Context = {
    val root = new Node("root node of the game view")
    val board = new BoardView(app)
    
    root attachChild board.root
    
    Context(app, root, board)
  }
  
  override def onInit: Unit = setEnabled(true)
  
  override def onEnable: Unit = {
    ctx.app.rootNode attachChild (ctx gameRoot)
    ctx.app.getCamera.setLocation(new Vector3f(5f, 10f, 5f))
    ctx.app.getCamera.lookAt(new Vector3f(5.2f, 0f, -4.5f), new Vector3f(0f, 0f, -1f))
  }
  
  override def onDisable: Unit = ctx.app.rootNode detachChild (ctx gameRoot)
  
  override def update(tpf: Float): Unit = if (isEnabled) {
    val i = (ctx.app.getTimer.getTime / (ctx.app.getTimer.getResolution / 15L)).toInt % 100
    val b = Board.sized(10, 10) - Tile(i % 10, i / 10)
    ctx.boardView update b
  }
}
