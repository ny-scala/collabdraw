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
  var paper = Raphael(options.canvas, 700, 700);
  var pather = new collabdraw.Pather(paper);
  
  socket.onopen = function(e) {
    console.log("open: " + e);
  };
  
  socket.onerror = function(e) {
    console.error("error: " + e);
  };
  
  socket.onmessage = function(msg) {
    console.log(msg.data);
    // TODO dirty hack! we want to get complete fragments and append
    var point = eval(msg.data);
    pather.start(point);
    pather.onStroke([point[0] + 10, point[1] + 10]);
    pather.stop();
  };
  
  $("#" + options.canvas).mousedown(function(e) {
    pather.start([e.clientX, e.clientY]);
  })
  .mousemove(function(e) {
    var move = pather.onStroke([e.clientX, e.clientY]);
    if(move.length) {
      var msg = "[" + move[0] + "," + move[1] + "]";
      console.log("send: " + msg);
      socket.send(msg);
    }
  })
  .mouseup(function(e) {
    var path = pather.stop();
    //console.info(path)
    //socket.send(path[0]);
  });
};
