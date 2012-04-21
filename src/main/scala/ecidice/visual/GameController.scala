package ecidice
package visual

import com.jme3.asset.AssetManager
import com.jme3.font.BitmapText
import com.jme3.system.Timer
import com.jme3.font.BitmapFont
import com.jme3.math.ColorRGBA._
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.app.Application
import com.jme3.scene.Node

final class GameController extends Controller[GameController.Context] {
  private var app: UpdateLoop = _
  
  override def init(asm: AppStateManager, app: UpdateLoop): GameController.Context = {
    this.app = app
    new GameController.Context(app)
  }
  
  override def update(tpf: Float): Unit = if (isEnabled) {
  }
}
object GameController {
  private[GameController] final class Context(val app: UpdateLoop)
}
