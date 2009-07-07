package de.i0n.burst.model

/**
 * Represents an area or space on the game board, either on the ground or on a
 * raised level. Such a space can be empty, occupied by a dice or be the start
 * or destination of a dice's movement.
 * 
 * @author Andreas Flierl
 */
class Space(var content: Endpoint)

abstract class Endpoint
case object Empty extends Endpoint
case class Occupied(d: Dice) extends Endpoint
case class Movement(start: Space, destination: Space,
                    when: Long, duration: Long) extends Endpoint
