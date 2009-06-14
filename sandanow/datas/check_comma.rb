#!/usr/bin/ruby -Ku

num = ARGV[1].to_i
File.open(ARGV[0]){ |file|
	file.each_line do |l|
		if l.count(',') != num then puts l, l.count(',') end
	end
}

