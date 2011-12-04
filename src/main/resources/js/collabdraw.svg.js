if(!window.collabdraw) collabdraw = {};
collabdraw.svg = {};
collabdraw.svg.ns = "http://www.w3.org/2000/svg";

collabdraw.svg.serializer = {

	toXml : ( function( ) {
		var _parser = ( window.DOMParser ) ? new DOMParser( ) : new ActiveXObject( "Microsoft.XMLDOM" );
		return function( str ) {
			return window.DOMParser ? 
				_parser.parseFromString( '<svg xmlns="' + collabdraw.svg.ns + '">' + str + '</svg>', "text/xml" ).firstChild : 
				_parser.loadXML( str );
		};
	})( ),

	toString : ( function( ) {
		var _serializer = new XMLSerializer( );
		return function( xml ) {
        	return _serializer.serializeToString( xml );
		};
	})( )
};
