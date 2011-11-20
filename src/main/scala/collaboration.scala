package collabdraw

import unfiltered.request._
import unfiltered.netty.websockets._
import scala.collection.JavaConversions.JConcurrentMapWrapper
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.HashMap

class CollaborationPlan(drawings: DrawingStore) extends Plan with CloseOnException {
  /* Drawing Id => List[WebSocket] */
  val sockets = new JConcurrentMapWrapper(
    new ConcurrentHashMap[String, List[WebSocket]]).withDefaultValue(Nil)
  
  def intent = {
    case GET(Path(Seg("drawing" :: id :: Nil))) => {
      case Open(s) =>
        // TODO push down the current state from this image's actor!
        sockets += id -> (s :: sockets(id))
      case Message(s, Text(msg)) =>
        // TODO message this image's actor w/ new state!
        sockets(id).filterNot(_ == s).foreach(_ send msg)
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
