#!/usr/local/bin/ruby -Ku

require 'cgi'
require 'time'
require File.dirname(__FILE__)+'/holiday.rb'
require File.dirname(__FILE__)+'/draw_table.rb'
require File.dirname(__FILE__)+'/tags.rb'

busnow = CGI.new
now = Time.new
holiday = nil

if now.wday == 6 || now.wday == 0 then
	holiday = true
else
	cal = TCalendarHoliday.new(now.year, now.month)
	if cal.holiday?(now.day) then
		holiday = true
	else
		holiday = false
	end
end


h_mode = busnow['holiday']
if h_mode != ''
	if h_mode == 'true'
		holiday = true
	else
		holiday = false
	end
end

mode = busnow['mode']
if mode == ''
	default
elsif mode == 'to'
	to(holiday)
elsif mode == 'from'
	from(holiday)
else
	default
end
