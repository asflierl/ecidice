package de.i0n.burst.model

/**
 * A single 6-side dice.
 * <p>
 * The dice layout is 
 * <pre>
 *  3
 *  6512
 *  4
 * </pre>
 * where 6 is on top, 4 in front, 5 on the right side. 
 * This can alternatively represented as
 * <pre>
 *  65
 *  4
 * </pre>
 * to sufficiently describe the rotation of the dice.
 * 
 * @author Andreas Flierl
 */
class Dice {
  private var topFace = 6
  private var frontFace = 4
  private var rightFace = 5
  
  def top = topFace
  def bottom = opposite(topFace)
  def front = frontFace
  def back = opposite(frontFace)
  def right = rightFace
  def left = opposite(rightFace)
  
  private def opposite(eyes: Int) = 7 - eyes
  
  private def set(t: Int, r: Int, f: Int) = {
    topFace = t
    rightFace = r
    frontFace = f
  }
  
  def rotateUp = set(front, right, bottom)
  def rotateDown = set(back, right, top)
  def rotateRight = set(left, top, front)
  def rotateLeft = set(right, bottom, front)
  def rotateClockwise = set(top, back, right)
  def rotateCounterClockwise = set(top, front, left)
  
  def flipUpOrDown = set(bottom, right, back)
  def flipLeftOrRight = set(bottom, left, front)
}