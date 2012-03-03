/*
 * Copyright (c) 2009-2012 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
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
  
  override def start {
    setSettings(prefs.settings)
    
    if (JmeSystem.showSettingsDialog(settings, false)) {
      prefs.save
      super.start
    } else {
      logInfo("game startup cancelled")
    }
  }
  
  override def initialize {
    super.initialize
    
    renderer.applyRenderState(DEFAULT)

    guiNode.setQueueBucket(Gui)
    guiNode.setCullHint(Never)
    
    // custom initialization here
    viewPort.setBackgroundColor(White)
    loadFPSText
    
    viewPort.attachScene(rootNode)
    guiViewPort.attachScene(guiNode)
    
    val b = new Box(ZERO, 1, 1, 1)
    val geom = new Geometry("Box", b)
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    mat.setColor("Color", Blue)
    geom.setMaterial(mat)
    rootNode.attachChild(geom)
  }
  
  def loadFPSText {
    fpsText.setSize(guiFont.getCharSet.getRenderedSize)
    fpsText.setLocalTranslation(0, fpsText.getLineHeight, 0)
    fpsText.setColor(Black)
    fpsText.setText("")
    
    guiNode.attachChild(fpsText)
  }
  
  override def update {
    if (speed == 0 || paused) return
        
    super.update
    val tpf = timer.getTimePerFrame * speed

    secondCounter += timer.getTimePerFrame
    val fps = timer.getFrameRate.toInt
    if (secondCounter >= 1.0f) {
        fpsText.setText("%4d fps".format(fps))
        secondCounter = 0.0f
    }

    stateManager.update(tpf)

    // custom updates here
    
    rootNode.updateLogicalState(tpf)
    guiNode.updateLogicalState(tpf)
    rootNode.updateGeometricState()
    guiNode.updateGeometricState()

    stateManager.render(renderManager)

    renderManager.render(tpf, true)
    
    // custom render things here
  }
  
  override def handleError(message: String, thrown: Throwable): Unit = {
    logSevere(message, thrown)
    System exit 1
  }
}
