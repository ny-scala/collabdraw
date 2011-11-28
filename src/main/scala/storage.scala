package collabdraw

import java.util.UUID.randomUUID

case class User(name: String)

case class Drawing(name: String, id: String = randomUUID.toString)

trait DrawingStore {
  def get(id: String): Option[Drawing]
  def put(drawing: Drawing): Drawing
  def list: Traversable[Drawing]
}

class InMemoryStore extends DrawingStore {
  import java.util.concurrent.ConcurrentHashMap
  import scala.collection.JavaConversions.JConcurrentMapWrapper
  private val drawings = JConcurrentMapWrapper(new ConcurrentHashMap[String, Drawing])
  
  def get(id: String): Option[Drawing] = {
    drawings.get(id)
  }
  
  def put(drawing: Drawing): Drawing = {
    drawings += (drawing.id -> drawing)
    drawing
  }
  
  def list: Traversable[Drawing] = drawings.values
}
