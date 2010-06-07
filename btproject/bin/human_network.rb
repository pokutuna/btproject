# -*- coding: utf-8 -*-
$LOAD_PATH.unshift(File.dirname(__FILE__) + '/../lib')

require 'yaml'

require 'log_parser'


users = YAML.load(ARGF.read)
bdas = users.map{ |k,v| v[1]} 
loggers = []

users.each{ |k,v|
  puts "--- start processing #{k}'s log ---"
  logger = Logger.new(k)

  puts "reading log files"
  Dir.glob(File.expand_path(v[0])).each do |filename|
    File.open(filename){ |f|
      puts '  '+File.basename(f.path)
      f.each_line do |line|
        logger.add_record(Record.new(line.chomp)) unless line[0] == '#'
      end
    }
  end
  puts "finish reading log files"

  puts "analyzeing log"
  result = logger.analyze
  result = result.to_a.sort_by{|i| i[1][0]}.reverse
  result = result.select{ |i| bdas.include? i[0]}
  result.each do |i|
    puts "  #{i[0]} detect:#{i[1][0]}  meet:#{i[1][1]}"
  end
  
  
  puts "--- finish processing ---"
  puts ""

  loggers.push logger
}

