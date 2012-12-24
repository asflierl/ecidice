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
import com.jme3.material.RenderState.DEFAULT
import com.jme3.math.ColorRGBA.White
import com.jme3.renderer.RenderManager
import com.jme3.renderer.queue.RenderQueue.Bucket.Gui
import com.jme3.scene.Spatial.CullHint.Never
import util.Logging
import util.Prefs
import ecidice.visual.FPSText
import com.jme3.scene.Node
import ecidice.visual.GameController

final class UpdateLoop extends Application with Logging {
  private[this] var internalTime = 0d
  
  val rootNode = new Node("Root Node")
  val guiNode = new Node("Gui Node")
  val prefs = Prefs.load
  
  def assets = getAssetManager
  
  def time = internalTime
  
  override def start: Unit = {
    setSettings(prefs.settings)
    
    if (JmeSystem.showSettingsDialog(settings, false)) {
      prefs.save
      super.start
    } else {
      logInfo("game startup cancelled")
    }
  }
  
  override def initialize: Unit = {
    super.initialize
    
    renderer applyRenderState DEFAULT

    guiNode setQueueBucket Gui
    guiNode setCullHint Never
    
    viewPort setBackgroundColor White
    
    viewPort attachScene rootNode
    guiViewPort attachScene guiNode
    
    stateManager attach new FPSText
    stateManager attach new GameController
  }
  
  override def update: Unit =
    if (speed > 0 && ! paused) {
      super.update
      customUpdate
    }
  
  def customUpdate: Unit = {
    val tpf = timer.getTimePerFrame * speed
    internalTime += tpf

    stateManager update tpf
    rootNode updateLogicalState tpf
    guiNode updateLogicalState tpf
    
    rootNode.updateGeometricState
    guiNode.updateGeometricState

    stateManager render renderManager
    renderManager render (tpf, true)
  }
  
  override def handleError(message: String, thrown: Throwable): Unit = {
    logSevere(message, thrown)
    stop
  }
}
