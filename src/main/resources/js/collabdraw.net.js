/*
 * Channel module facilitates client/server communication, pushing draw events
 * upstream as well as receiving and updating the local view with data drawn
 * by other clients.
 */
if(!window.collabdraw) collabdraw = {};

collabdraw.net = {
  newWebSocket: function(uri) {
    if(window.WebSocket)
      return new WebSocket(uri);
    else if(window.MozWebSocket)
      return new MozWebSocket(uri);
    else throw "No Websocket Support!"
  }
};

collabdraw.net.Channel = function(options) {
  var socket = new collabdraw.net.newWebSocket(options.url);
  var paper = Raphael(options.canvas, 1500, 1500);
  var pather = new collabdraw.Pather(paper);
  
  socket.onopen = function(e) {
    console.log("channel open: " + e);
  };
  
  socket.onerror = function(e) {
    console.error("channel error: " + e);
  };
  
  socket.onmessage = function(msg) {
    console.log("channel recv");
    var scratch = document.getElementById("scratch_pad");
    scratch.innerHTML = msg.data;
    var svgN = collabdraw.svg.nodeToSVG(scratch.firstChild);
    var appended = document.getElementById(options.canvas).firstChild.appendChild(svgN);
  };
  
  $("#" + options.canvas).mousedown(function(e) {
    pather.start([e.clientX, e.clientY]);
  })
  .mousemove(function(e) {
    pather.onStroke([e.clientX, e.clientY]);
  })
  .mouseup(function(e) {
    var path = pather.stop();
    var pathNode = document.getElementById(path[0].getAttribute("id"));
    var elementXmlString = collabdraw.svg.nodeToString(pathNode);
    socket.send(elementXmlString);
  });
};
