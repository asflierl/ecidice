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
  private var app: UpdateLoop = _
  private var secondCounter = 0.8f
  private var textNode: BitmapText = _
  private var timer: Timer = _
  
  override def init(asm: AppStateManager, app: UpdateLoop): Unit = {
    this.app = app
    val guiFont = app.getAssetManager.loadFont("Interface/Fonts/Default.fnt")
    textNode = new BitmapText(guiFont, false)
    textNode.setSize(guiFont.getCharSet.getRenderedSize)
    textNode.setLocalTranslation(0, textNode.getLineHeight, 0)
    textNode.setColor(Black)
    textNode.setText("")
    timer = app.getTimer
    setEnabled(true)
  }
  
  override def cleanup: Unit = {
    super.cleanup
    app.guiNode.detachChild(textNode)
  }
 
  def onEnable: Unit = app.guiNode.attachChild(textNode)
  
  def onDisable: Unit = app.guiNode.detachChild(textNode)
  
  override def update(tpf: Float): Unit = if (isEnabled) {
    secondCounter += timer.getTimePerFrame
    val fps = timer.getFrameRate.toInt
    if (secondCounter >= 1.0f) {
        textNode.setText("%4d fps" format fps)
        secondCounter = 0.0f
    }
  }
}
