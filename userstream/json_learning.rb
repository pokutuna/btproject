#!/usr/bin/ruby
# -*- coding: utf-8 -*-

require 'rubygems'
require 'json'


File.open('./log.txt'){ |file|
  file.each_line { |line|
    if line[0] == "{"[0] then
      status = JSON.parse(line) rescue next
      puts status['event'] if status.has_key? 'event'
      puts status['text'] if status.has_key? 'text'
    end
  }

}
