if(!window.collabdraw) collabdraw = {};
collabdraw.svg = {};

collabdraw.svg.collapseAttributes = function(nodeMap) {
  var attrStrings = "";
  if(nodeMap.length)
    for(var i = 0; i < nodeMap.length; i++) {
      var attr = nodeMap.item(i);
      attrStrings += " " + attr.nodeName + "=\"" + attr.nodeValue + "\"";
    }
  return attrStrings;
};

/** Recursively serialize a DOM Node into an XML string. This is totally ad-hoc;
 *  surely a better method is availble internally...
 */
collabdraw.svg.nodeToString = function(node) {
  if(node.nodeType == 1) {
    var attrs = collabdraw.svg.collapseAttributes(node.attributes);
    var open = "<" + node.nodeName + attrs + ">";
    var close = "</" + node.nodeName + ">";
    var children = "";
    for(var i = 0; i < node.childNodes.length; i++)
      children += collabdraw.svg.nodeToString(node.childNodes.item(i));
    
    return open + children + close;
  } else {
    console.warn("Ignoring non-element node: " + node + "; " + node.nodeValue);
    return "";
  }
};

/** Recreate a DOM Node as an SVG node. Note: not yet recursive! */
collabdraw.svg.nodeToSVG = function(node) {
  var svgN = document.createElementNS("http://www.w3.org/2000/svg", node.nodeName.toLowerCase());
  for(var i = 0; i < node.attributes.length; i++) {
    var attr = node.attributes.item(i);
    console.log("svgN.setAttribute(\""+attr.nodeName+"\", \""+attr.nodeValue+"\");");
    svgN.setAttribute(attr.nodeName, attr.nodeValue);
  }
  return svgN;
};
