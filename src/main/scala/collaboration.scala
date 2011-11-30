package collabdraw

import unfiltered.request._
import unfiltered.netty.websockets._
import scala.collection.immutable.HashMap
import scala.actors.Actor

/** The main activity hub for drawing. All message routing for coordinating
 *  draw events passes through here. New users should be given the current
 *  state of the drawing, incoming messages will represent complete elements
 *  (for now, lines as path elements).
 */
class CollaborationPlan(drawingActor: Actor)
  extends Plan
  with CloseOnException {
  
  @volatile
  var sockets =
    HashMap.empty[String, Set[WebSocket]].withDefaultValue(Set.empty[WebSocket])

  def intent = {
    case GET(Path(Seg("drawing" :: id :: Nil))) => {
      case Open(s) =>
        // update list of websockets for drawing `id`
        sockets += id -> (sockets(id) + s)
        // get the current state
         val future = drawingActor !! FetchDrawing(id)
         future() match {
           case currentSvg: String if(!currentSvg.isEmpty) =>
             sockets(id).foreach(_.send(currentSvg))
           case other =>
             println("discarding response from actor '%s'" format other)
         }

      case Message(s, Text(txt)) =>
        scala.util.control.Exception.allCatch(
          scala.xml.XML.loadString(txt) match {
            case svg =>
              drawingActor ! PutDrawing(id, svg)
              sockets(id).filterNot(_ == s).foreach(_.send(txt))
        })
        
      case Close(s) =>
        sockets += (id -> sockets(id).filterNot(_ == s))

      case Error(s, e) =>
        e.printStackTrace
    }
  }
  
  def pass = Plan.DefaultPassHandler
}
