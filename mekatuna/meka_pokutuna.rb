#!/usr/bin/ruby -Ku

require 'yaml'

require 'rubygems'
gem 'twitter', '0.4.2'
require 'twitter'

require File.dirname(__FILE__)+'/convert_latin1.rb'


conf = YAML::load_file(File.dirname(__FILE__)+'/meka_pokutuna.conf')

#login
twit = Twitter::Base.new(conf['twitter_id'], conf['passwd'])
twit.timeline(:friends).each do |s|
	puts s.user, s.text.unescapeHTML
end
