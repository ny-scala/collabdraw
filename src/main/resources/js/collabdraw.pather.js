/* 
 * Pather module provides stroke management for generating path elements
 * representing lines created by clicking and dragging on the canvas.
 * Callers can create Pather instances with a paper object and then attach
 * start(), stop(), and onStroke() to mouse down, up, and move, respectively.
 */
if(!window.collabdraw) collabdraw = {};

collabdraw.Pather = function(paper) {
  var lastPoint = [0,0];
  var path = null;
  
  /** Starts a new path.
   *  http://www.w3.org/TR/SVG/paths.html#PathData
   */
  function newPath(start) {
    var path = paper.path("M" + start[0] + "," + start[1])
      .attr({
        fill: '#9cf',
        stroke: '#ddd',
        'stroke-width': 5
      });
    path[0].setAttribute("id", "path_" + new Date().getTime());
    return path;
  };
  
  /** Draw a line segment by augmenting the current path's data string.
   *  http://www.w3.org/TR/SVG/paths.html#PathData
   */
  function drawStroke(path, from, to) {
    var newPath = path[0].getAttribute("d")
      + "M" + from[0] + "," + from[1]
      + "L" + to[0] + "," + to[1];
    path[0].setAttribute("d", newPath);
  };
  
  this.start = function(from) {
    lastPoint = from;
    path = newPath(from);
  };
  
  this.stop = function() {
    var created = path;
    path = null;
    return created;
  };
  
  this.onStroke = function(point) {
    if(path) {
      drawStroke(path, lastPoint, point);
      lastPoint = point;
      return lastPoint;
    } else return [];

  };
};
