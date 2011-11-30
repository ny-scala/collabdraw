package collabdraw

import scala.actors.Actor
import scala.collection.mutable.HashMap

/** Collaborative drawing has an obvious piece of shared mutable state:
 *  the drawing! Actors can help here. A drawing actor should manage access
 *  to current drawings; note that FetchDrawing should allow callers to
 *  receive a Future... so respond!
 */
class DrawingActor extends Actor {

  val data = HashMap.empty[String, List[String]].withDefaultValue(Nil)

  def act {
    loop {
      react {
        case FetchDrawing(id) =>
          reply(data(id).reverse.mkString)

        case AddPath(id, p) =>
          data.put(id, p :: data(id))
      }
    }
  }

}

/* Messages to handle; we'll need more.. */
sealed trait DrawingMessage
case class FetchDrawing(id: String) extends DrawingMessage
case class AddPath(id: String, p: String) extends DrawingMessage
