package collabdraw

import unfiltered.request._
import unfiltered.response._

class DrawingPlan(drawings: DrawingStore) extends unfiltered.filter.Plan {

  def intent = {
    case GET(Path("/")) =>
      sys.error("TODO!")

    case GET(Path(Seg("drawing" :: id :: Nil)) & Params(p)) =>
      sys.error("TODO!")
    
    case POST(Path("/drawing")) =>
      sys.error("TODO!")
  }
  
  def viewIndex(active: Traversable[Drawing]) =
    Html(
      <html>
        <head>
          <script src="/js/jquery.js"></script>
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
