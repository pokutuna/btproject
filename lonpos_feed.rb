#!/usr/local/bin/ruby

require 'uri'
require 'rss/maker'
require 'rubygems'
require 'mechanize'
require 'date'

LONPOS_AREAQ_PAGE = 'http://www.lon-pos.com/en/areaq.htm'

rss = RSS::Maker.make('2.0') do |maker|

	maker.channel.about = 'http://www11.atpages.jp/pokutuna/lonpos.xml'
	maker.channel.title = 'LONPOS Official Question Feed'
	maker.channel.description = 'Feedalized LONPOS Weekly Questions'
	maker.channel.link = LONPOS_AREAQ_PAGE
	maker.items.do_sort = true;
	
	agent = WWW::Mechanize.new
	q_top = agent.get(LONPOS_AREAQ_PAGE)
	q_urls = q_top.body.scan(/<a href="([^\."<]+\.(php|htm|html))"/).uniq!.map!{|x| x[0]}
	q_urls.each do |url|
		begin
			url =  URI.join(LONPOS_AREAQ_PAGE, url)
			page = agent.get(url)
			page.body =~ /(<p class="entxt".+<\/div>)/ms
			q_part = $1

			item = maker.items.new_item
			q_part =~ /<p class="entxt">(.*)<\/p>/
			item.title = $1
			item.link = url
			q_part.gsub!(/<p><a href="areaq.htm.+<img[^"]+src="[^"]+\/menu\.gif".*/ms, '')
			item.description = q_part
			item.date = Time.now

		rescue => e
		end
	end
end

File.open(File.dirname(File.expand_path(__FILE__))+'/lonpos.xml', 'w'){ |file| file.puts(rss.to_s)}
