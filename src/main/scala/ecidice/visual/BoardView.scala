package ecidice
package visual

import com.jme3.asset.AssetManager
import com.jme3.font.BitmapText
import com.jme3.system.Timer
import com.jme3.font.BitmapFont
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.app.Application
import com.jme3.scene.Node
import com.jme3.scene.shape.Box
import com.jme3.scene.Geometry
import com.jme3.material.Material
import com.jme3.math.Vector3f.ZERO
import ecidice.model._
import ecidice.model.mode._
import com.jme3.scene.shape.Quad
import jme3tools.optimize.GeometryBatchFactory
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.math.FastMath.DEG_TO_RAD
import scala.collection.mutable.HashMap
import com.jme3.scene.Spatial

final class BoardView(app: UpdateLoop) {
  val root = new Node("root node of the board")

  private final val tileRotation = DEG_TO_RAD * -90f
  private val quad = new Quad(0.98f, 0.98f)
  private val material = new Material(app.assets, "Common/MatDefs/Misc/Unshaded.j3md")
  
  private val rendered = HashMap[Tile, Spatial]() 
  
  material setColor ("Color", new ColorRGBA(0.9f, 0.9f, 0.9f, 0f))

  def update(board: Board): Unit = if (board.tiles != rendered.keySet) {
    val surplus = rendered.keySet -- board.tiles
    val missing = board.tiles -- rendered.keySet
    
    surplus foreach remove
    missing foreach attach
  }
  
  private val attach = (tile: Tile) => {
    val geom = new Geometry("Box", quad)
    geom setMaterial material
    
    geom.move(tile.column + 0.01f, 0f, - tile.row - 0.01f)
    geom.rotate(tileRotation, 0f, 0f)
    
    root attachChild geom
    rendered += tile -> geom
  }
  
  private val remove = (tile: Tile) => {
    root detachChild rendered(tile)
    rendered -= tile
  }
}
