package collabdraw

import unfiltered.jetty.Http
import unfiltered.netty.{Http => NHttp}

object Servers {
  
  def main(args: Array[String]) {
    
    val drawingActor = new DrawingActor
    val store = new InMemoryStore
    val collab = new CollaborationPlan(store, drawingActor)
    
    val ws = NHttp(5679).plan(collab)
    ws.start()
    
    Http(8080)
      .plan(new DrawingPlan(store))
      .resources(getClass.getResource("/"))
      .run()
    
    ws.stop()
  }
  
}
