# -*- coding: utf-8 -*-
$LOAD_PATH.unshift(File.dirname(__FILE__) + '/../lib')

require 'yaml'

require 'log_parser'


@users = YAML.load(ARGF.read)
@bdas = @users.map{ |k,v| v[1]}
@username = Hash.new("")
@users.each { |k,v| @username[v[1]] = k }
@loggers = []


def read_log
  @users.each{ |k,v|
    logger = Logger.new(k)
    @loggers.push logger
    
    puts "reading #{k}'s log files"
    Dir.glob(File.expand_path(v[0])).each do |filename|
      File.open(filename){ |f|
        puts '  '+File.basename(f.path)
        f.each_line do |line|
          logger.add_record(Record.new(line.chomp)) unless line[0] == '#'
        end
      }
    end
  }  
end


def analyze_log(&filter)
  raise RuntimeError 'no logs read' if @loggers.empty?
  @loggers.each do |logger|
    puts "analyzing #{logger.name}'s log"
    result = logger.analyze(&filter)
    result = result.to_a.sort_by{|i| i[1][0]}.reverse
    result = result.select{ |i| @bdas.include? i[0]}
    result.each do |i|
      name = @username[i[0]]
      puts "  #{name} detect:#{i[1][0]}  meet:#{i[1][1]}"
    end
    puts ""
  end
end

read_log
puts '-- all --'
analyze_log
puts '---------'

#sep1
puts '-- 0600 to 1200 --'
analyze_log{ |i| 6 <= i.date.hour && i.date.hour < 12}
puts '-- 1200 to 1800 --'
analyze_log{ |i| 12 <= i.date.hour && i.date.hour < 18}
puts '-- 1800 to 2400 --'
analyze_log{ |i| 18 <= i.date.hour && i.date.hour <= 23}


#sep2
puts '-- 0900 to 1030 --'
analyze_log{ |i|
  a = Time.local(i.date.year, i.date.month, i.date.day, 9, 10)
  b = Time.local(i.date.year, i.date.month, i.date.day, 10, 30)
  a <= i.date && i.date <b
}

puts '-- 1030 to 1230 --'
analyze_log{ |i|
  a = Time.local(i.date.year, i.date.month, i.date.day, 10, 30)
  b = Time.local(i.date.year, i.date.month, i.date.day, 12, 30)
  a <= i.date && i.date <b
}

puts '-- 1230 to 1500 --'
analyze_log{ |i|
  a = Time.local(i.date.year, i.date.month, i.date.day, 12, 30)
  b = Time.local(i.date.year, i.date.month, i.date.day, 15, 00)
  a <= i.date && i.date <b
}

puts '-- 1500 to 1630 --'
analyze_log{ |i|
  a = Time.local(i.date.year, i.date.month, i.date.day, 15, 00)
  b = Time.local(i.date.year, i.date.month, i.date.day, 16, 30)
  a <= i.date && i.date <b
}

puts '-- 1630 to 1800 --'
analyze_log{ |i|
  a = Time.local(i.date.year, i.date.month, i.date.day, 16, 30)
  b = Time.local(i.date.year, i.date.month, i.date.day, 18, 00)
  a <= i.date && i.date <b
}

puts '-- 1800 to 2359 --'
analyze_log{ |i|
  a = Time.local(i.date.year, i.date.month, i.date.day, 18, 00)
  b = Time.local(i.date.year, i.date.month, i.date.day, 23, 59)
  a <= i.date && i.date <b
  }

