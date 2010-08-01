package ecidice

import com.jme3.app.Application
import com.jme3.system.JmeSystem
import com.jme3.material.Material
import com.jme3.math._
import com.jme3.scene._
import com.jme3.scene.shape._
import com.jme3.math.ColorRGBA

import ecidice.util.Logging

class App extends Application with Logging {
  val rootNode = new Node("Root Node");
  val guiNode = new Node("Gui Node");
  val prefs = Prefs.load
  
  override def start() {
    setSettings(prefs.settings)
    
    if (JmeSystem.showSettingsDialog(settings)) {
      prefs.save()
      super.start()
    } else {
      Logger.info("game startup cancelled")
    }
  }
  
  def simpleInitApp() {
    val b = new Box(Vector3f.ZERO, 1, 1, 1)
    val geom = new Geometry("Box", b)
    val mat = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md")
    mat.setColor("m_Color", ColorRGBA.Blue)
    geom.setMaterial(mat)
    rootNode.attachChild(geom)
  }
}