#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

require File.dirname(__FILE__)+'/../dbfiles/pokutuna_trigram.rb'
require File.dirname(__FILE__)+'/../dbfiles/twitter_posts.rb'
require File.dirname(__FILE__)+'/../yahoo_api/yahoo_api.rb'


YahooAPI.set
connect_TrigramDB
connect_TwitterPostsDB

def add_trigram_by_id(id)
	begin
		post = Post.find(id)
		if OnRecord.find(:first, :conditions => ["url=?", post.url.to_s]) == nil then
			result = YahooAPI::DA.parse(post.message)
			p result.chunks.join
			for i in 0..result.morphems.size-3
				head = (i == 0)
				tail = (i == result.morphems.size-3)
				a, b, c = result.morphems[i..i+2].map{ |m| m.to_s}

				rec = Trigram.find(:first, :conditions => ["w1=? and w2=? and w3=?",a,b,c])
				if rec == nil
					Trigram.create(
						:w1 => a, :w2 => b, :w3 => c, :count => 1,
						:head => head, :tail => tail)
					p a+','+b+','+c+': new'
				else
					rec.count += 1
					rec.save
					p a+','+b+','+c+': '+rec.count.to_s
				end
				OnRecord.create(:url => post.url.to_s)
			end
		else
			return false
		end
		true
	rescue => e
		return id
	end
end


id = Post.find(:last).id

buf = Array.new
while id != 0
	sleep 2
	r = add_trigram_by_id(id)
	puts '---'
	buf << r unless r == true
	id -= 1
	break if buf.size >= 5
end

p buf

#76
