package collabdraw

import java.util.UUID.randomUUID
import scala.xml.NodeSeq
case class Drawing(name: String, id: String = randomUUID.toString, body: NodeSeq = NodeSeq.Empty)

/** Simple CRUD service for persisting drawings. Implementations may
 *  store to any backing store (mongo, memory, gist (Doug's great idea)).
 */
trait DrawingStore {
  def get(id: String): Option[Drawing]
  def put(drawing: Drawing): Drawing
  def list: Traversable[Drawing]
}

class InMemoryStore extends DrawingStore {
  import java.util.concurrent.ConcurrentHashMap
  import scala.collection.JavaConversions.{JConcurrentMapWrapper => JCMWrapper}
  private val drawings = JCMWrapper(new ConcurrentHashMap[String, Drawing])
  
  def get(id: String): Option[Drawing] =
    drawings.get(id)
  
  def put(drawing: Drawing): Drawing = {
    drawings += (drawing.id -> drawing)
    drawing
  }
  
  def list: Traversable[Drawing] = drawings.values
}
