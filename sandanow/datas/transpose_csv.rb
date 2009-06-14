#!/usr/bin/ruby -Ku

require 'csv'

csv = CSV.read(ARGV[0])
p csv
File.open(ARGV[1],'w'){ |file|
	for i in 0..csv[0].size-1 do
		file.puts csv.map{ |e| e[i]}.join(',')
	end
}
