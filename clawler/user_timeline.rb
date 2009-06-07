#!/usr/bin/ruby -Ku

require 'rexml/document'
require 'open-uri'

require File.dirname(__FILE__)+'/../mekatuna/modules/twitterstring.rb'

class String; include TwitterString; end

Twitter = 'http://twitter.com/'
Twitter_rss = 'http://twitter.com/statuses/user_timeline/'
Twitter_rss_suffix = '.atom?page='

user = 'pokutuna'
File.open(user+'_posts.txt','w'){ |file|
	
	catch(:exit){
		begin
			doc = REXML::Document.new(open(Twitter+user).read)
			doc.elements['//*[@id="rssfeed"]'].to_s =~ /user_timeline\/(\d+)\.rss/
			user_id =  $1
		rescue => e
			puts e.class, e.message, e.backtrace
			throw :exit
		end

		file.puts Time.now

		buffer404 = Array.new
		count404 = 0
		for pagenum in 1..500
			begin
				doc = REXML::Document.new(open(Twitter_rss+user_id+Twitter_rss_suffix+pagenum.to_s).read.unescapeHTML)
				count404 = 0
				doc.elements.each('//entry') do |e|
					text = e.elements['content'].text.sub(user+': ', '')
					e.elements['updated'].text =~ /(\d+)\-(\d+)\-(\d+)T(\d+):(\d+):(\d+)\+(.*)/
					time = Time.utc($1,$2,$3,$4,$5,$6).to_s
					url =  e.elements['link[@rel="alternate"]/attribute::href'].to_s
					file.puts "#{text},#{time},#{url}"
				end
				sleep 5 #wait
			rescue => e
				count404 += 1
				break if count404 >= 5
				buffer404.push(pagenum)
			end
		end
		p buffer404
	}
	
}
