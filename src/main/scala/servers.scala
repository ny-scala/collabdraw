package collabdraw

import unfiltered.jetty.Http
import unfiltered.netty.{Http => NHttp}

object Servers {
  
  def main(args: Array[String]) {
    val store = new InMemoryStore
    //TODO test data for now ...
    store.put(Drawing("test", Nil, "test"))
    val collab = new CollaborationPlan(store)
    val ws = NHttp(5679).plan(collab)
    ws.start()
    
    Http(8080)
      .plan(new DrawingPlan(store))
      .resources(getClass.getResource("/"))
      .run()
    
    ws.stop()
  }
  
}
