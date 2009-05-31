#!/usr/bin/ruby -Ku

txt = File.open('latin-1.txt','r').read
txt.gsub!(/\n|\t/,'')
buf = txt.scan(/<TR><TD ALIGN=center><IMG SRC=[^>]+><TD.+?>(.+?)<TD.+?<!-- --><TD.+?>(.+?)<TD.+?<\/TR>/)
#p buf
#p buf.size
p 'ERROR' unless buf.select{ |i| i.size != 2}

str = "LATIN1_CONVERT_TABLE = {\n"
buf.each do |i|
	str << "  \'"+i[1]+"\'"+" => "+"\'"+i[0]+"\',\n"
end
str[str.rindex(',')] = "\n}"

puts str
