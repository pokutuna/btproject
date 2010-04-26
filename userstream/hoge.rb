#!/usr/bin/ruby
# -*- coding: utf-8 -*-

require 'net/http'
require 'uri'
require 'rubygems'
require 'json'

USERNAME = 'pokutuna'
puts 'input password:'
PASSWORD = gets.chomp
PROXY = 'proxy.kwansei.ac.jp'
PORT = '8080'

uri = URI.parse('http://chirpstream.twitter.com/2b/user.json')

# Net::HTTPResponseクラスにeach_lineメソッドを追加
module Net
  class HTTPResponse
    def each_line(rs = "\n")
      stream_check
      while line = @socket.readuntil(rs)
        yield line
      end
      self
    end
  end
end


File.open('./log.txt', 'a'){ |file|  
  begin
    Net::HTTP.Proxy(PROXY, PORT).start(uri.host, uri.port) do |http|
      request = Net::HTTP::Get.new(uri.request_uri)
      request.basic_auth(USERNAME, PASSWORD)
      http.request(request) do |response|
        response.each_line("\r\n") do |line|
          file.puts line
          status = JSON.parse(line) rescue next
          puts status
        end
=begin
      next unless status['text']
      user = status['user']
      puts "#{user['screen_name']}: #{status['text']}"
=end
      end
    end
    
  rescue Timeout::Error => ex
    p "<-----!!!!! Timeout::Error!!!!!----->"
    
    retry
  end
}

=begin
/usr/lib/ruby/1.8/timeout.rb:60:in `rbuf_fill': execution expired (Timeout::Error)
	from /usr/lib/ruby/1.8/net/protocol.rb:134:in `rbuf_fill'
	from /usr/lib/ruby/1.8/net/protocol.rb:116:in `readuntil'
	from hoge.rb:21:in `each_line'
	from hoge.rb:36
	from /usr/lib/ruby/1.8/net/http.rb:1053:in `request'
	from /usr/lib/ruby/1.8/net/http.rb:2136:in `reading_body'
	from /usr/lib/ruby/1.8/net/http.rb:1052:in `request'
	from hoge.rb:34
	from /usr/lib/ruby/1.8/net/http.rb:543:in `start'
	from /usr/lib/ruby/1.8/net/http.rb:440:in `start'
	from hoge.rb:31
=end
