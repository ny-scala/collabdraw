package collabdraw

import scala.actors.Actor

/** Collaborative drawing has an obvious piece of shared mutable state:
 *  the drawing! Actors can help here. A drawing actor should manage access
 *  to current drawings; note that FetchDrawing should allow callers to
 *  receive a Future... so respond!
 */
class DrawingActor(store: DrawingStore) extends Actor {
  // ... and dont for get to start and stop me!
  def act {}
}

/* Messages to handle; we'll need more.. */
sealed trait DrawingMessage
case class FetchDrawing(id: String) extends DrawingMessage
case class PutDrawing(id: String, svg: String) extends DrawingMessage
