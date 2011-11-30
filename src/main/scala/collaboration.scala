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
           case xmlSvg: scala.xml.NodeSeq =>
             println("pulled svg info %s" format xmlSvg)
             xmlSvg.foreach(svg => s.send(svg.toString))
           case strs: Seq[_] =>
             println("actor responded with %s" format strs)
             strs.foreach(str => s.send(str.toString))
           case other =>
             println("actor responded with unhandled msg %s" format other)
         }

      case Message(s, Text(txt)) =>
        println("rec message %s from %s" format (txt, s))
        scala.xml.XML.loadString(txt) match {
          case svg =>
           drawingActor ! PutDrawing(id, svg)
        }

        // update other clients
        sockets(id).filterNot(_ == s).foreach(_.send(txt))
        
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
