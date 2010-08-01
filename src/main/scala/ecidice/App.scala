package ecidice

import com.jme3.app.Application
import com.jme3.system.JmeSystem
import com.jme3.material._
import com.jme3.math._
import com.jme3.scene._
import com.jme3.scene.shape._
import com.jme3.math.ColorRGBA
import com.jme3.renderer.RenderManager
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.scene.Spatial.CullHint
import com.jme3.font._

import ecidice.util.Logging

class App extends Application with Logging {
  val rootNode = new Node("Root Node");
  val guiNode = new Node("Gui Node");
  val prefs = Prefs.load
  
  private var secondCounter = 0.0f
  private lazy val fpsText = new BitmapText(guiFont, false)
  private lazy val guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt")
  
  override def start() {
    setSettings(prefs.settings)
    
    if (JmeSystem.showSettingsDialog(settings)) {
      prefs.save()
      super.start()
    } else {
      Logger.info("game startup cancelled")
    }
  }
  
  override def initialize() {
    super.initialize()
    
    renderer.applyRenderState(RenderState.DEFAULT)

    guiNode.setQueueBucket(Bucket.Gui)
    guiNode.setCullHint(CullHint.Never)
    
    // custom initialization here
    loadFPSText()
    
    viewPort.attachScene(rootNode)
    guiViewPort.attachScene(guiNode)
    
    val b = new Box(Vector3f.ZERO, 1, 1, 1)
    val geom = new Geometry("Box", b)
    val mat = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md")
    mat.setColor("m_Color", ColorRGBA.Blue)
    geom.setMaterial(mat)
    rootNode.attachChild(geom)
  }
  
  def loadFPSText() {
    fpsText.setSize(guiFont.getCharSet().getRenderedSize())
    fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0)
    fpsText.setText("fps")
    guiNode.attachChild(fpsText)
  }
  
  override def update() {
    if (speed == 0 || paused) return
        
    super.update()
    val tpf = timer.getTimePerFrame() * speed

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
}