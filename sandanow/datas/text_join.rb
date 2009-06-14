#!/usr/bin/ruby -Ku

left = File.open(ARGV[0]){ |f| f.readlines}
right = File.open(ARGV[1]){ |f| f.readlines}

dest = File.open(ARGV[2], 'w'){ |dest|
	left.each_with_index do |str, i|
		dest.puts str.chomp + ' ' + right[i]
	end
}
