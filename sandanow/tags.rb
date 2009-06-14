require 'time'
require 'csv'

CONTENT_TYPE = "Content-Type: text/html\n\n"
HEADER = <<EOS
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>バスなう</title>
  </head>
  <body>
EOS

FOOTER = <<EOS
  </body>
</html>
EOS

EXPLAIN = <<EOS
▲:小・中・高校休校日運休  ▼:大学休校日運休
EOS

def h(size, &block)
	puts "<h#{size}>"
	block.call
	puts "</h#{size}>"
end

def a(link, &block)
	puts "<a href=\"#{link}\">"
	block.call
	puts "</a>"
end

def p(&block)
	puts '<p>'
	block.call
	puts '</p>'
end

def br
	puts '<br />'
end

def table(&block)
	puts '<table border="1" cellspacing="0">'
	block.call
	puts '</table>'
end

def tr(&block)
	puts '<tr>'
	block.call
	puts '</tr>'
end

def td(&block)
	puts '<td align="center">'
	block.call
	puts '</td>'
end

def th(&block)
	puts '<th>'
	block.call
	puts '</th>'
end

def center(&block)
	puts '<div align="center">'
	block.call
	puts '</div>'
end

def font(size,&block)
	puts "<font size=\"#{size}\">"
	block.call
	puts "</font>"
end

def small(&block)
	puts "<small>"
	block.call
	puts "</small>"
end
