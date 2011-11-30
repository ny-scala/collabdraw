package collabdraw

import unfiltered.request._
import unfiltered.response._

class DrawingPlan(drawings: DrawingStore) extends unfiltered.filter.Plan {
  def intent = {
    case GET(Path("/")) =>
      viewIndex

    case GET(Path(Seg("drawing" :: id :: Nil)) & Params(p)) =>
      drawings.get(id) match {
        case Some(d) => viewDrawing(d)
        case None => NotFound
      }
    
    case POST(Path("/drawing") & Params(p)) => 
      p.get("name") match {
        case Some(Seq(name)) => 
	  val drawing = drawings.put(Drawing(name))
          Redirect("/drawing/" + drawing.id)
	case _  => 
	  BadRequest
      }
  }
  
  def viewIndex =
    Html(
      <html>
        <head>
          <script src="/js/jquery.js"></script>
        </head>
        <body>
          Hi there! Check out the <a href="/drawing/test">test drawing</a>
          <form method="post" action="/drawing">
	   <label for="name">Name:</label>: <input name="name"></input>
           <input type="submit"></input>
	  </form>
        </body>
      </html>
    )
    
  def viewDrawing(d: Drawing) =
    Html(
      <html>
        <head>
          <script src="/js/jquery.js"></script>
          <script src="/js/raphael-min.js"></script>
          <script src="/js/collabdraw.pather.js"></script>
          <script src="/js/collabdraw.svg.js"></script>
          <script src="/js/collabdraw.net.js"></script>
          <script>var drawingId = '{d.id}';</script>
          <script><![CDATA[
            (function($) {
              $(document).ready(function() {
                collabdraw.net.Channel({
                  url:    'ws://localhost:5679/drawing/' + drawingId,
                  canvas: 'drawing'
                });
              });
            })(jQuery);
          ]]></script>
          <link href="/css/drawing.css" type="text/css" rel="stylesheet" />
        </head>
        <body>
          <div class="canvas" id="drawing" data-drawing={d.id}></div>
          <div id="scratch_pad" style="display:none;"></div>
        </body>
      </html>
    )
  
}
