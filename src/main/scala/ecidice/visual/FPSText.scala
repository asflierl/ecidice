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

final class FPSText extends Controller {
  case class Context(app: UpdateLoop, textNode: BitmapText)
  
  private[this] var secondCounter = 0.8f
  
  override def init(asm: AppStateManager, app: UpdateLoop): Context = {
    val guiFont = app.getAssetManager.loadFont("Interface/Fonts/Default.fnt")
    val textNode = new BitmapText(guiFont, false)
    textNode.setSize(guiFont.getCharSet.getRenderedSize)
    textNode.setLocalTranslation(0, textNode.getLineHeight, 0)
    textNode.setColor(Black)
    textNode.setText("")
    
    Context(app, textNode)
  }
  
  override def onInit: Unit = setEnabled(true)
  
  override def onEnable: Unit = ctx.app.guiNode.attachChild(ctx.textNode)
  
  override def onDisable: Unit = ctx.app.guiNode.detachChild(ctx.textNode)
  
  override def update(tpf: Float): Unit = if (isEnabled) {
    secondCounter += ctx.app.getTimer.getTimePerFrame
    val fps = ctx.app.getTimer.getFrameRate.toInt
    if (secondCounter >= 1.0f) {
        ctx.textNode.setText("%4d fps" format fps)
        secondCounter = 0.0f
    }
  }
}
