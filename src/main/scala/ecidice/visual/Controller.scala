package ecidice
package visual

import com.jme3.app.state.AbstractAppState
import com.jme3.app.Application
import com.jme3.app.state.AppStateManager

abstract class Controller extends AbstractAppState {
  override final def initialize(asm: AppStateManager, app: Application): Unit = {
    super.initialize(asm, app)
    init(asm, app.asInstanceOf[UpdateLoop]) // argh >.<
  }
  
  def init(asm: AppStateManager, app: UpdateLoop): Unit
  
  override final def setEnabled(enabled: Boolean): Unit = {
    super.setEnabled(enabled)
    if (enabled) onEnable
    else onDisable
  }
  
  def onEnable: Unit
  def onDisable: Unit
}