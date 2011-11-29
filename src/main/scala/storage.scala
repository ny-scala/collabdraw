package collabdraw

import java.util.UUID.randomUUID
import scala.xml.NodeSeq
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection

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

object MongoStore extends DrawingStore {

  /**
   * Define a collection where we will store our drawing documents
   */
  val collection = MongoConnection()("collabdraw")("drawings")

  /**
   * This is how we will serialize a Drawing object to a MongoDB document
   */
  def toDBObject(d: Drawing) = {
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> d.id  // MongoDB expects an _id field - just
    builder += "name" -> d.name
    builder += "body" -> d.body.toString()
    builder.result()
  }

  /**
   * This is how we will deserialize a MongoDB document to a Drawing object
   */
  def toObject(dbo: DBObject) = {
    val _id = dbo.getAs[String]("_id").getOrElse(sys.error("toObject: dbo does not supply _id\n%s".format(dbo)))
    val name = dbo.getAs[String]("name").getOrElse(sys.error("toObject: dbo does not supply name\n%s".format(dbo)))
    val body = scala.xml.XML.loadString {
      dbo.getAs[String]("body").getOrElse(sys.error("toObject: dbo does not supply body\n%s".format(dbo)))
    }
    Drawing(name = name,
    id = _id,
    body = body)
  }

  def get(id: String) = collection.findOne(MongoDBObject("_id" -> id)).map(toObject(_))

  def list = collection.find(MongoDBObject.empty).map(toObject(_)).toSeq

  def put(drawing: Drawing) = {
    val writeConcern = collection.writeConcern
    val writeResult = collection.insert(toDBObject(drawing), writeConcern)
    val commandResult = writeResult.getLastError(writeConcern)
    if (!commandResult.ok()) {
      sys.error("put: failed for drawing.id=%s because\n%s".format(drawing.id, commandResult.getErrorMessage))
    }
    drawing
  }
}
