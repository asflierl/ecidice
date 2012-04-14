package ecidice.visual

import com.jme3.asset.AssetManager
import com.jme3.font.BitmapText
import com.jme3.system.Timer
import com.jme3.font.BitmapFont
import com.jme3.math.ColorRGBA._

final class FPSText private (guiFont: BitmapFont, timer: Timer) extends BitmapText(guiFont, false) {
  setSize(guiFont.getCharSet.getRenderedSize)
  setLocalTranslation(0, getLineHeight, 0)
  setColor(Black)
  setText("")
  
  private var secondCounter = 0.8f
  
  override def updateLogicalState(tpf: Float): Unit = {
    secondCounter += timer.getTimePerFrame
    val fps = timer.getFrameRate.toInt
    if (secondCounter >= 1.0f) {
        setText("%4d fps" format fps)
        secondCounter = 0.0f
    }
    super.updateLogicalState(tpf)
  }
}
object FPSText {
  def apply(am: AssetManager, timer: Timer) = new FPSText(am.loadFont("Interface/Fonts/Default.fnt"), timer)
}
