class String
	
	require 'cgi'
	
	LATIN1_CONVERT_TABLE = {
		'&#160;' => '&nbsp;',
		'&#161;' => '&iexcl;',
		'&#162;' => '&cent;',
		'&#163;' => '&pound;',
		'&#164;' => '&curren;',
		'&#165;' => '&yen;',
		'&#166;' => '&brvbar;',
		'&#167;' => '&sect;',
		'&#168;' => '&uml;',
		'&#169;' => '&copy;',
		'&#170;' => '&ordf;',
		'&#171;' => '&laquo;',
		'&#172;' => '&not;',
		'&#173;' => '&shy;',
		'&#174;' => '&reg;',
		'&#175;' => '&macr;',
		'&#176;' => '&deg;',
		'&#177;' => '&plusmn;',
		'&#178;' => '&sup2;',
		'&#179;' => '&sup3;',
		'&#180;' => '&acute;',
		'&#181;' => '&micro;',
		'&#182;' => '&para;',
		'&#183;' => '&middot;',
		'&#184;' => '&cedil;',
		'&#185;' => '&sup1;',
		'&#186;' => '&ordm;',
		'&#187;' => '&raquo;',
		'&#188;' => '&frac14;',
		'&#189;' => '&frac12;',
		'&#190;' => '&frac34;',
		'&#191;' => '&iquest;',
		'&#192;' => '&Agrave;',
		'&#193;' => '&Aacute;',
		'&#194;' => '&Acirc;',
		'&#195;' => '&Atilde;',
		'&#196;' => '&Auml;',
		'&#197;' => '&Aring;',
		'&#198;' => '&AElig;',
		'&#199;' => '&Ccedil;',
		'&#200;' => '&Egrave;',
		'&#201;' => '&Eacute;',
		'&#202;' => '&Ecirc;',
		'&#203;' => '&Euml;',
		'&#204;' => '&Igrave;',
		'&#205;' => '&Iacute;',
		'&#206;' => '&Icirc;',
		'&#207;' => '&Iuml;',
		'&#208;' => '&ETH;',
		'&#209;' => '&Ntilde;',
		'&#210;' => '&Ograve;',
		'&#211;' => '&Oacute;',
		'&#212;' => '&Ocirc;',
		'&#213;' => '&Otilde;',
		'&#214;' => '&Ouml;',
		'&#215;' => '&times;',
		'&#216;' => '&Oslash;',
		'&#217;' => '&Ugrave;',
		'&#218;' => '&Uacute;',
		'&#219;' => '&Ucirc;',
		'&#220;' => '&Uuml;',
		'&#221;' => '&Yacute;',
		'&#222;' => '&THORN;',
		'&#223;' => '&szlig;',
		'&#224;' => '&agrave;',
		'&#225;' => '&aacute;',
		'&#226;' => '&acirc;',
		'&#227;' => '&atilde;',
		'&#228;' => '&auml;',
		'&#229;' => '&aring;',
		'&#230;' => '&aelig;',
		'&#231;' => '&ccedil;',
		'&#232;' => '&egrave;',
		'&#233;' => '&eacute;',
		'&#234;' => '&ecirc;',
		'&#235;' => '&euml;',
		'&#236;' => '&igrave;',
		'&#237;' => '&iacute;',
		'&#238;' => '&icirc;',
		'&#239;' => '&iuml;',
		'&#240;' => '&eth;',
		'&#241;' => '&ntilde;',
		'&#242;' => '&ograve;',
		'&#243;' => '&oacute;',
		'&#244;' => '&ocirc;',
		'&#245;' => '&otilde;',
		'&#246;' => '&ouml;',
		'&#247;' => '&divide;',
		'&#248;' => '&oslash;',
		'&#249;' => '&ugrave;',
		'&#250;' => '&uacute;',
		'&#251;' => '&ucirc;',
		'&#252;' => '&uuml;',
		'&#253;' => '&yacute;',
		'&#254;' => '&thorn;',
		'&#255;' => '&yuml;'
	}

	def unescapeHTML
		return CGI.unescapeHTML(self.gsub(/(&#\d+;)/){|pat| LATIN1_CONVERT_TABLE[pat]||pat})
	end

	def without_reply_to
		dest = self.strip
		dest =~ /^@\w+(.+)/
		return $1.strip
	end

	def count_reply_to
		dest = self.strip
		return dest.scan(/@([0-9A-Za-z_])+/).size
	end

end
