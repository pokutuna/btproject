#!/usr/bin/ruby -Ku

require 'logger'
require 'yaml'

require 'rubygems'
gem 'twitter', '0.4.2'
require 'twitter'

require File.dirname(__FILE__)+'/create_trigram.rb'
require File.dirname(__FILE__)+'/reply_action.rb'
require File.dirname(__FILE__)+'/modules/twitterstring.rb'
require File.dirname(__FILE__)+'/modules/timeconv.rb'

class String;	include TwitterString; end
class Time; include TimeConv; end

#load env
$conf = YAML.load_file(File.dirname(__FILE__)+'/meka_pokutuna.conf')
$last_time = Time.parse(File.open(File.dirname(__FILE__)+'/last_time'){ |f| f.gets})
logger = Logger.new(File.dirname(__FILE__)+'/log', 'weekly')


$twit = Twitter::Base.new($conf['twitter_id'], $conf['passwd'], $conf['proxy'] || {})

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
		message = r.text.without_reply_to
		
		#shut up
		if message =~ SHUT_UP then
			shut_up(r)
			break

		#command
		elsif message[0..0] == '/' then
			analyze_command(r)

		#follow
		elsif message =~ FOLLOW then
			follow(r)
			
		#else
		else
			users.push(r.user.screen_name)
		end
	end

	#暫定クソ連呼
	users.uniq.each do |u|
		if rand <= 0.7 then
			$twit.post("@#{u} #{'クソ'*(rand(9)+1)} が")
			logger.info("replied to #{u}")
		end
	end

	#post 3gram
	while true
		message = create_3gram
		followers = $twit.followers.map{ |u| u.screen_name}
		rest = (message.scan_reply_to - followers)
		if rest == [] then
			$twit.post(message)
			break
		end
	end

	#update time
	File.open(File.dirname(__FILE__)+'/last_time','w'){ |f| f.puts Time.now.utc}
	logger.info("update #{Time.now.utc.to_s}")
	
rescue => e
	p e.backtrace
	logger.error($!)
	logger.error(e.backtrace)
	#$twit.post("@#{conf['su']} #{e.backtrace.to_s}")
end

