package collabdraw

import unfiltered.request._
import unfiltered.response._

class DrawingPlan(drawings: DrawingStore) extends unfiltered.filter.Plan {

  def intent = {
    case GET(Path("/")) =>
      viewIndex(drawings.list)

    case GET(Path(Seg("drawing" :: id :: Nil)) & Params(p)) =>
      drawings.get(id).map(viewDrawing).getOrElse(NotFound)
    
    case POST(Path("/drawing")) =>
      val d = drawings.put(Drawing("Untitled"))
      Redirect("/drawing/" + d.id)
  }
  
  def viewIndex(active: Traversable[Drawing]) =
    Html(
      <html>
        <head>
          <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.js"></script>
          <script src="/js/home.js"></script>
        </head>
        <body>
          <div>
            <form id="create_new" action="/drawing" method="post">
            <input type="submit" value="Create New Drawing"/>
            </form>
          </div>
          <div id="active_drawings">
            <ul>
            {active.map(d => <li><a href={"/drawing/" + d.id}>{d.name}</a></li>)}
            </ul>
          </div>
        </body>
      </html>
    )
    
  def viewDrawing(d: Drawing) =
    Html(
      <html>
        <head>
          <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.js"></script>
          <script src="/js/raphael-min.js"></script>
          <script src="/js/collabdraw.pather.js"></script>
          <script src="/js/collabdraw.channel.js"></script>
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
        </body>
      </html>
    )
  
}
