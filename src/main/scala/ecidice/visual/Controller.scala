package ecidice
package visual

import com.jme3.app.state.AbstractAppState
import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import scalaz.Scalaz.{ none, some }

abstract class Controller[A] extends AbstractAppState {
  private[this] var initializedState = none[A]
  
  override final def initialize(asm: AppStateManager, app: Application): Unit = {
    super.initialize(asm, app)
    initializedState = some(init(asm, app.asInstanceOf[UpdateLoop]))
    onInit
  }
  
  def init(asm: AppStateManager, app: UpdateLoop): A
  
  protected final def ctx = initializedState.get // argh >.<
  
  override final def setEnabled(enabled: Boolean): Unit = {
    super.setEnabled(enabled)
    if (enabled) onEnable
    else onDisable
  }
  
  override final def cleanup: Unit = {
    onCleanup
    initializedState = none
    super.cleanup
  }
  
  def onInit: Unit = ()
  def onEnable: Unit = ()
  def onDisable: Unit = ()
  def onCleanup: Unit = ()
}