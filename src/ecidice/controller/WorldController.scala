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

package ecidice.controller;

import com.jme.math.Vector3f;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameStateNode;
import com.jmex.game.state.FPSGameState;
import com.jmex.game.state.GameState;

/**
 * Root gamestate of the game world.
 * <p>
 * If this is is deactivated, pretty much nothing is shown or happening anymore.
 * 
 * @author Andreas Flierl
 */
@SerialVersionUID(1L)
class WorldController(game: StandardGame) 
    extends BasicGameStateNode[GameState]("game world") {

  private val boardController = new BoardController()
  attachChild(boardController);
  boardController.setActive(true);

  private val fps = new FPSGameState();
  attachChild(fps);
  fps.setActive(true);

  game.getCamera().setLocation(new Vector3f(-1.5f, -1, 30));
  game.getCamera().lookAt(new Vector3f(0, 0, 0), new Vector3f(0f, 1f, 0f));

  rootNode.updateRenderState();
}