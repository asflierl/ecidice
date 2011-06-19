/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice

import com.jme3.material.RenderState.BlendMode.Alpha
import com.jme3.app.Application
import com.jme3.system.JmeSystem
import com.jme3.material._
import com.jme3.material.RenderState.DEFAULT
import com.jme3.math._
import com.jme3.math.Vector3f.ZERO
import com.jme3.scene._
import com.jme3.scene.shape._
import com.jme3.math.ColorRGBA._
import com.jme3.renderer.RenderManager
import com.jme3.renderer.queue.RenderQueue.Bucket.Gui
import com.jme3.scene.Spatial.CullHint.Never
import com.jme3.font._

import util.Logging
import util.Prefs

class App extends Application with Logging {
  val rootNode = new Node("Root Node");
  val guiNode = new Node("Gui Node");
  val prefs = Prefs.load
  
  private var secondCounter = 0.0f
  private lazy val fpsText = new BitmapText(guiFont, false)
  private lazy val guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt")
  
  override def start() {
    setSettings(prefs.settings)
    
    if (JmeSystem.showSettingsDialog(settings, false)) {
      prefs.save()
      super.start()
    } else {
      logInfo("game startup cancelled")
    }
  }
  
  override def initialize() {
    super.initialize()
    
    renderer.applyRenderState(DEFAULT)

    guiNode.setQueueBucket(Gui)
    guiNode.setCullHint(Never)
    
    // custom initialization here
    viewPort.setBackgroundColor(White)
    loadFPSText()
    
    viewPort.attachScene(rootNode)
    guiViewPort.attachScene(guiNode)
    
    val b = new Box(ZERO, 1, 1, 1)
    val geom = new Geometry("Box", b)
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    mat.setColor("Color", White)
    geom.setMaterial(mat)
    rootNode.attachChild(geom)
  }
  
  def loadFPSText() {
    fpsText.setSize(guiFont.getCharSet().getRenderedSize())
    fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0)
    fpsText.setColor(Black)
    fpsText.setText("")
    
    guiNode.attachChild(fpsText)
  }
  
  override def update() {
    if (speed == 0 || paused) return
        
    super.update()
    val tpf = timer.getTimePerFrame * speed

    secondCounter += timer.getTimePerFrame
    val fps = timer.getFrameRate.toInt
    if (secondCounter >= 1.0f) {
        fpsText.setText("%d fps".format(fps))
        secondCounter = 0.0f
    }

    stateManager.update(tpf)

    // custom updates here
    
    rootNode.updateLogicalState(tpf)
    guiNode.updateLogicalState(tpf)
    rootNode.updateGeometricState()
    guiNode.updateGeometricState()

    stateManager.render(renderManager)

    renderManager.render(tpf)
    
    // custom render things here
  }
  
  override def handleError(message: String, thrown: Throwable): Unit = {
    logSevere(message, thrown)
    System exit 1
  }
}
