package collabdraw

import unfiltered.jetty.Http
import unfiltered.netty.{Http => NHttp}

object Servers {
  
  def main(args: Array[String]) {
    
    val store = new InMemoryStore

    val drawingActor = new DrawingActor(store)

    val collab = new CollaborationPlan(drawingActor)
    
    /* Always provide a blank test drawing. */
    store.put(new Drawing("Test", "test"))
    
    val ws = NHttp(5679).plan(collab)
    ws.start()
    
    Http(8080)
      .plan(new DrawingPlan(store))
      .resources(getClass.getResource("/"))
      .run({ s =>
        drawingActor.start
      }, {
        s =>
          drawingActor ! Stop
          println("stopping")
      })
  }
  
}
