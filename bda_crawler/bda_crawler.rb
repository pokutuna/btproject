# -*- coding: utf-8 -*-

require 'date'

WAIT_TIME = 5

today = Date.today.strftime("%Y%m%d")
File.open(today+'.txt', 'a'){ |file|
	loop do
		`hcitool scan`.each_line do |l|
			data = l.scan(/([\dA-F]{2}:[\dA-F]{2}:[\dA-F]{2}:[\dA-F]{2}:[\dA-F]{2}:[\dA-F]{2})\t(.+)/).flatten
			if data != []
				str = Time.now.strftime('%Y/%m/%d %X')+"\t"+data[0]+"\t"+data[1]
				file.puts str
				puts str
			end
		end
		sleep WAIT_TIME
	end
}

