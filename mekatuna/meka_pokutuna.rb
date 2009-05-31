#!/usr/bin/ruby -Ku

require 'yaml'

require 'rubygems'
gem 'twitter', '0.4.2'
require 'twitter'

require File.dirname(__FILE__)+'/extention/string.rb'
require File.dirname(__FILE__)+'/extention/time.rb'

#load env
conf = YAML.load_file(File.dirname(__FILE__)+'/meka_pokutuna.conf')
last_time = File.open(File.dirname(__FILE__)+'/last_time'){ |f| f.gets}
ltime = Time.parse(last_time)

#login
twit = Twitter::Base.new(conf['twitter_id'], conf['passwd'])

begin 
	#get new replies
	new_replies = Array.new
	twit.replies.each do |s|
		post_time = Time.parse(s.created_at)
		if (post_time - ltime) > 0 then
			s.text = s.text.unescapeHTML
			new_replies.push(s)
		end
	end

	#analyse replies
	users = Array.new
	
	new_replies.each do |r|
		#command
		if r.text.without_reply_to[0..0] == '/' && r.user.screen_name == conf['su'] then
			text = r.text.without_reply_to
			#暫定followのみ
			if text.include?('/follow') then
				text['/follow'] = ''
				targets = text.scan(/([0-9A-Za-z_]+)/).flatten
				targets.each do |name|
					twit.create_friendship(name)
				end
				twit.post("@#{r.user.screen_name} フォローしたよ")
			else
				twit.post("@#{r.user.screen_name} 帰れカス")
			end
			
			#else		
		else
			users.push(r.user.screen_name)
		end
	end

	p users #kokomade konai!
	#暫定クソ連呼
	kuso = 'クソ'
	users.uniq.each do |u|
		twit.post("@#{u} #{kuso*(rand(9)+1)} が")
	end

	#update time
	File.open(File.dirname(__FILE__)+'/last_time','w'){ |f| f.puts Time.now.utc}

rescue => e
	twit.post("@#{conf['su']} #{e.backtrace.to_s}")
end

