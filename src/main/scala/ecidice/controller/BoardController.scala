/*
 * Copyright (c) 2009, Andreas Flierl
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the names of the copyright holders nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.controller

import scala.collection.mutable._

import com.jme.light.PointLight
import com.jme.math.Vector3f
import com.jme.renderer.ColorRGBA
import com.jme.scene.state.BlendState
import com.jme.scene.state.LightState
import com.jme.system.DisplaySystem
import com.jmex.game.state.BasicGameStateNode
import com.jmex.game.state.GameState

import ecidice.view.BubbleView

/**
 * Represents the game board, i.e. the area with 8 rows of 8 columns of bubbles.
 * 
 * @author Andreas Flierl
 */
class BoardController extends BasicGameStateNode[GameState]("game board") {    
  addBubbles()
  setupBlending()
  addHighlights()
  
  rootNode.updateRenderState()
  
  /**
   * Adds the 64 bubbles as children to this node.
   */
  def addBubbles() =
    for (ring <- 0 until 8; index <- 0 until 8)
      rootNode.attachChild(new BubbleView(ring, index, 8))
    
   
  /**
   * Enables alpha blending for the whole board.
   */
  def setupBlending() = {
    // Blending
    
    val alphaState = DisplaySystem.getDisplaySystem.getRenderer.createBlendState
    alphaState.setBlendEnabled(true)
    alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha)
    alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha)
    alphaState.setTestEnabled(true)
    alphaState.setTestFunction(BlendState.TestFunction.GreaterThan)
    alphaState.setEnabled(true)
    
    rootNode.setRenderState(alphaState)
  }
  
  /**
   * Adds a specular highlighting point light.
   */
  def addHighlights() = {
    val ls = DisplaySystem.getDisplaySystem.getRenderer.createLightState

    val spec = new PointLight
    spec.setLocation(new Vector3f(1, 20, 0))
    spec.setSpecular(ColorRGBA.white)
    spec.setAttenuate(true)
    spec.setConstant(.25f)
    spec.setLightMask(LightState.MASK_AMBIENT | 
            LightState.MASK_GLOBALAMBIENT | LightState.MASK_DIFFUSE)
    spec.setEnabled(true)
    ls.attach(spec)
    
    rootNode.setRenderState(ls)
  }
}
