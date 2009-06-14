#!/usr/local/bin/ruby -Ku

require 'socket'

content_type = "Content-Type: text/html\n\n"
html_start = '<html>'
html_end = '</html>'
body_start = '<body>'
body_end = '</body>'
br = '<br>'

puts content_type
puts html_start
puts body_start

puts Time::now
puts br
puts 'CurrentDirectory:' << Dir::pwd
puts br
puts 'CurrentDirectoryFiles:'
puts br
Dir::entries(Dir::pwd).each do |dir|
	puts '　　' + dir
	puts br
end
puts IPSocket::getaddress(Socket.gethostname)
puts br
puts body_end
puts html_end
