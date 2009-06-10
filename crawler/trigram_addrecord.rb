#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

require File.dirname(__FILE__)+'/model_trigram.rb'
require File.dirname(__FILE__)+'/model_twitter_posts.rb'
require File.dirname(__FILE__)+'/../yahoo_api/yahooapi.rb'


YahooAPI.set
connect_TrigramDB
connect_TwitterPostsDB

last_id = Post.find(:last).id
first_id = Post.find(:first).id

id = last_id
post = Post.find(id)

if OnRecord.find(:first, :conditions => ["url=?", post.url.to_s]) == nil then
	result = YahooAPI::DA.parse(post.message)

	for i in 0..result.morphems.size-3
		head = (i == 0)
		tail = (i == result.morphems.size-3)
		a, b, c = result.morphems[i..i+2].map{ |m| m.to_s}
		p a+','+b+','+c

	end
end


