package collabdraw

import unfiltered.request._
import unfiltered.response._
import scala.actors.Actor

class DrawingPlan(drawings: DrawingStore, drawing_actor: Actor) extends unfiltered.filter.Plan {
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
       case _ => Redirect("/")
     }
  }
  
  def viewIndex =
    Html(
      <html>
        <head>
          <title>collabdraw</title>
          <link href="http://fonts.googleapis.com/css?family=Delius" rel="stylesheet" type="text/css"/>
          <link href="/css/index.css" rel="stylesheet" type="text/css"/>
          <script src="/js/jquery.js" type="text/javascript"></script>
          <script src="/js/index.js" type="text/javascript"></script>
        </head>
        <body>
         <div id="container">
          <h1>collabdraw</h1>
          <p>
            Hi there! Check out the <a href="/drawing/test">test drawing</a>
          </p>
          <form method="post" action="/drawing">
	          <input name="name" type="text" id="name" placeholder="What do you want to draw?"></input>
            <input type="submit" id="submit-drawing" class="btn" value="Start drawing"></input>
	        </form>
          <ul>
	        {
	          val drawing_ids = (drawing_actor !! ActiveDrawings)().asInstanceOf[Seq[String]]
	          for ( drawing_id <- drawing_ids) yield {
	            val drawing = drawings.get(drawing_id).getOrElse { sys.error("Couldn't find Drawing") }
	            <li><a href={"/drawing/"+drawing.id}>{drawing.name}</a></li>
	          }
	        }
	        </ul>
         </div>
        </body>
      </html>
    )
    
  def viewDrawing(d: Drawing) =
    Html(
      <html>
        <head>
          <title>collabdraw { d.name }</title>
          <script src="/js/jquery.js" type="text/javascript"></script>
          <script src="/js/raphael-min.js" type="text/javascript"></script>
          <script src="/js/collabdraw.pather.js" type="text/javascript"></script>
          <script src="/js/collabdraw.svg.js" type="text/javascript"></script>
          <script src="/js/collabdraw.net.js" type="text/javascript"></script>
          <script type="text/javascript">var drawingId = '{d.id}';</script>
          <script type="text/javascript"><![CDATA[
            (function($) {
              $(document).ready(function() {
                collabdraw.net.Channel({
                  url:    'ws://'+window.location.hostname+':5679/drawing/' + drawingId,
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
