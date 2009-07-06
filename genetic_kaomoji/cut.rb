#/usr/bin/ruby -Ku

require 'kconv'
require 'cgi'

=begin
files = ['naku','otikomu','tereru','warau','yorokobu','bikkuri',
  'okoru', 'kyofu', 'bb']

files.each do |file|

	str = File.open(file+'.txt').read
	str = CGI.unescapeHTML str.toutf8
	ary = str.scan(/value="(.+?)"/).flatten
	File.open('data/'+file+'.txt', 'w'){ |f|
		ary.each do |face|
			f.puts face
		end
	}
end
=end


