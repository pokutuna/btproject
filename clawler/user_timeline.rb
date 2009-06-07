#!/usr/bin/ruby -Ku

require 'rexml/document'
require 'open-uri'

require File.dirname(__FILE__)+'/../mekatuna/modules/twitterstring.rb'

class String; include TwitterString; end

Twitter = 'http://twitter.com/'
Twitter_rss = 'http://twitter.com/statuses/user_timeline/'
Twitter_rss_suffix = '.atom?page='

user = 'pokutuna'

doc = REXML::Document.new(open(Twitter+user).read)
doc.elements['//*[@id="rssfeed"]'].to_s =~ /user_timeline\/(\d+)\.rss/
user_id =  $1


doc = REXML::Document.new(open(Twitter_rss+user_id+Twitter_rss_suffix+'10').read.unescapeHTML)
p doc.to_s
#rescue 404
