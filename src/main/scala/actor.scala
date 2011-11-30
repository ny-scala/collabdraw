package collabdraw

import scala.actors.Actor
import scala.collection.mutable.HashMap
import scala.xml.NodeSeq

/** Collaborative drawing has an obvious piece of shared mutable state:
 *  the drawing! Actors can help here. A drawing actor should manage access
 *  to current drawings; note that FetchDrawing should allow callers to
 *  receive a Future... so respond!
 */
class DrawingActor(store: DrawingStore) extends Actor {

  val data = HashMap.empty[String, List[String]].withDefaultValue(Nil)

  def act {
    loop {
      react {
        case FetchDrawing(id) =>
          reply(data(id).reverse.mkString)

        case PutDrawing(id, p) =>
          data.put(id, p.toString :: data(id))

        case ActiveDrawings =>
          reply(data.keys.toList)

        case Stop =>
          exit()
      }
    }
  }

}

/* Messages to handle; we'll need more.. */
sealed trait DrawingMessage
case class FetchDrawing(id: String) extends DrawingMessage // reply with
case class PutDrawing(id: String, svg: NodeSeq) extends DrawingMessage
case object ActiveDrawings extends DrawingMessage
case object Stop
