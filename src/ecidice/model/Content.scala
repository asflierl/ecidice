package ecidice.model

sealed abstract class Content

/**
 * Denotes that a space is empty. A dice can move or appear here (if this is
 * on the floor level).
 */
case object Empty extends Content

/**
 * This marks a space as occupied (by a dice). Other dice can not move or
 * appear here.
 */
case class Occupied(d: Dice) extends Content

/**
 * An instance of this class is present on the "from" and "to" spaces that are
 * involved in a dice's movement during the movement.
 */
case class Movement(dice: Dice, from: Space, to: Space, when: Timespan,
  transform: Transform.Value) extends Content
