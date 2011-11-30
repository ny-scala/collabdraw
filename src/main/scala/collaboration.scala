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
        println("connection to %s opened" format s)

        // update list of websockets for drawing `id`
        sockets += id -> (sockets(id) + s)

        // get the current state
         val future = drawingActor !! FetchDrawing(id)
         future() match {
           case fullSvg: Seq[String] =>
             println("pulled svg info %s" format fullSvg)
             fullSvg.foreach(s.send)
         }

      case Message(s, Text(svg)) =>

        /* Message downstream clients with the new stroke and update
          the shared drawing. */
        println("rec message %s from %s" format (svg, s))

        drawingActor ! PutDrawing(id, svg)

        sockets(id).filterNot(_ == s).foreach(_.send(svg))
        
      case Close(s) =>
        /* Remove the associated socket. */
        println("%s connection closed" format (s))
        println("before %s" format sockets)
        sockets += (id -> sockets(id).filterNot(_ == s))
        println("after %s" format sockets)

      case Error(s, e) =>
        /* Remove the associated socket. */
        e.printStackTrace
    }
  }
  
  def pass = Plan.DefaultPassHandler
}
