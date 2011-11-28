package collabdraw

import unfiltered.request._
import unfiltered.netty.websockets._
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions.JConcurrentMapWrapper
import java.util.concurrent.ConcurrentHashMap

class CollaborationPlan(drawings: DrawingStore) extends Plan with CloseOnException {
  /* Drawing Id => List[WebSocket] */
  val sockets = new JConcurrentMapWrapper(
    new ConcurrentHashMap[String, List[WebSocket]]).withDefaultValue(Nil)
  /* TODO Roll into a single service */
  val actors = new JConcurrentMapWrapper(
    new ConcurrentHashMap[String, scala.actors.Actor])
    
  def imageActor() =
    new scala.actors.Actor {
      private var image = scala.xml.NodeSeq.Empty
      
      def act =
        loop {
          react {
            case ('update, path: String) =>
              image = scala.xml.XML.loadString(path) +: image
            case 'fetch => reply(image)
            case 'exit => exit()
          }
        }
      
      start()
    }
  
  def intent = {
    case GET(Path(Seg("drawing" :: id :: Nil))) => {
      case Open(s) =>
        sockets += id -> (s :: sockets(id))
        actors.get(id) match {
          case Some(a) =>
            (a !! 'fetch)() match {
              case svg: scala.xml.NodeSeq =>
                sockets(id).filterNot(_ == s).foreach { s =>
                  s.send(svg.toString)
                }
            }
          case None => actors(id) = imageActor()
        }
      case Message(s, Text(msg)) =>
        sockets(id).filterNot(_ == s).foreach(_ send msg)
        actors(id) ! ('update -> msg)
      case Close(s) =>
        removeSocket(id, s)
      case Error(s, e) =>
        removeSocket(id, s)
        e.printStackTrace
    }
  }
  
  def removeSocket(id: String, ws: WebSocket) {
    sockets(id) = sockets(id).filterNot(_ == ws)
  }
  
  def pass = Plan.DefaultPassHandler
}
