#!/usr/bin/ruby -Ku

ARGV.each do |filename|
  str =	File.open(filename).read
	File.open(File.basename(filename,'.*')+'.csv', 'w'){ |file|
		str.each_line do |l|
			file.puts l.gsub(' ',',')
		end
	}

end
