package ecidice
package visual

import com.jme3.app.state.AbstractAppState
import com.jme3.app.Application
import com.jme3.app.state.AppStateManager

abstract class Controller[A] extends AbstractAppState {
  private[this] var initializedState: A = _
  
  override final def initialize(asm: AppStateManager, app: Application): Unit = {
    super.initialize(asm, app)
    initializedState = init(asm, app.asInstanceOf[UpdateLoop]) // argh >.<
    postInit
  }
  
  def init(asm: AppStateManager, app: UpdateLoop): A
  
  def postInit: Unit = ()
  
  protected def ctx: A = initializedState
  
  override final def setEnabled(enabled: Boolean): Unit = {
    super.setEnabled(enabled)
    if (enabled) onEnable
    else onDisable
  }
  
  def onEnable: Unit = ()
  def onDisable: Unit = ()
}