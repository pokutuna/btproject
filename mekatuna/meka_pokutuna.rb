#!/usr/bin/ruby -Ku

require 'yaml'

require 'rubygems'
gem 'twitter', '0.4.2'
require 'twitter'

require File.dirname(__FILE__)+'/reply_action.rb'
require File.dirname(__FILE__)+'/modules/twitterstring.rb'
require File.dirname(__FILE__)+'/modules/timeconv.rb'

class String;	include TwitterString; end
class Time; include TimeConv; end

#load env
$conf = YAML.load_file(File.dirname(__FILE__)+'/meka_pokutuna.conf')
$last_time = Time.parse(File.open(File.dirname(__FILE__)+'/last_time'){ |f| f.gets})

#login
#twit = Twitter::Base.new(conf['twitter_id'], conf['passwd'], conf['proxy'])
$twit = Twitter::Base.new($conf['twitter_id'], $conf['passwd'])

begin 
	#get new replies
	new_replies = Array.new
	$twit.replies.each do |s|
		post_time = Time.parse(s.created_at)
		if (post_time - $last_time) > 0 then
			s.text = s.text.unescapeHTML
			new_replies.push(s)
		end
	end

	#analyse replies
	users = Array.new
	
	new_replies.reverse.each do |r|

		#shut up
		if r.text.without_reply_to =~ SHUT_UP then
			shut_up(r)
			break

		#command
		elsif r.text.without_reply_to[0..0] == '/' then
			analyze_command(r)
			
		#else
		else
			users.push(r.user.screen_name)
		end
	end

	#暫定クソ連呼
	users.uniq.each do |u|
		$twit.post("@#{u} #{'クソ'*(rand(9)+1)} が")
	end

	#update time
	File.open(File.dirname(__FILE__)+'/last_time','w'){ |f| f.puts Time.now.utc}

rescue => e
	p 'hoge'
	p e
	p e.backtrace.to_s
	#$twit.post("@#{conf['su']} #{e.backtrace.to_s}")
end

