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


def analyze_log(file=nil, &filter)
  raise RuntimeError 'read no logs' if @loggers.empty?
  dest = []
  @loggers.each do |logger|
    result = logger.analyze(&filter)
    result = result.to_a.sort_by{|i| i[1][0]}.reverse
    result = result.select{ |i| @bdas.include? i[0]}
    result.each do |i|
      dest.push [logger, i]
    end
  end
  return dest
end


def put_analyzed(result)
  result.each do |i|
    name = @username[i[1][0]]
    #      puts "  #{name} detect:#{i[1][1][0]}  meet:#{i[1][1][1]}"
    str = "#{i[0].name} -> #{name} [weight = #{i[1][1][0]}, arrowsize = #{Math.sqrt(i[1][1][0])/10.0}];" 
    puts str
    file.puts str unless file == nil
  end
end


def put_graphviz_header(file)
  file.puts 'digraph sample{'
  file.puts 'graph [size="40,30", concentrate=true];'
end

def put_graphviz_footer(file)
  file.puts '}'
end


#put userlist
File.open('userlist.txt','w'){ |file|
  @username.each do |k,v|
    file.puts v
  end
}


#separate pattern
sep1_morning = lambda{ |i| 6 <= i.date.hour && i.date.hour < 12}
sep1_evening = lambda{ |i| 12 <= i.date.hour && i.date.hour < 18}
sep1_night = lambda{ |i| 18 <= i.date.hour && i.date.hour <= 23}

sep2_0900_1030 = lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 9, 10)
    b = Time.local(i.date.year, i.date.month, i.date.day, 10, 30)
    a <= i.date && i.date <b
  }

sep2_1030_1230 = lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 10, 30)
    b = Time.local(i.date.year, i.date.month, i.date.day, 12, 30)
    a <= i.date && i.date <b
  }

sep2_1230_1500 = lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 12, 30)
    b = Time.local(i.date.year, i.date.month, i.date.day, 15, 00)
    a <= i.date && i.date <b
  }
sep2_1500_1630 = lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 15, 00)
    b = Time.local(i.date.year, i.date.month, i.date.day, 16, 30)
    a <= i.date && i.date <b
  }
sep2_1630_1800 = lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 16, 30)
    b = Time.local(i.date.year, i.date.month, i.date.day, 18, 00)
    a <= i.date && i.date <b
}
sep2_1800_2359 = lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 18, 00)
    b = Time.local(i.date.year, i.date.month, i.date.day, 23, 59)
    a <= i.date && i.date <b
  }

sep1 = {
  'sep1_060000_120000' => sep1_morning,
  'sep1_120000_180000' => sep1_evening,
  'sep1_180000_235959' => sep1_night}

sep2 = {
  'sep2_090000_103000' => sep2_0900_1030,
  'sep2_103000_123000' => sep2_1030_1230,
  'sep2_123000_150000' => sep2_1230_1500,
  'sep2_150000_163000' => sep2_1500_1630,
  'sep2_163000_180000' => sep2_1630_1800,
  'sep2_180000_235900' => sep2_1800_2359
}



read_log
puts '-- all --'
File.open('log_all.txt','w'){ |file|
  put_graphviz_header(file)  
  analyze_log(file)
  put_graphviz_footer(file)
}
puts '---------'

#sep1
sep1.each do |k,v|
  puts '---' + k + '---'
  File.open(k+'.txt','w'){ |file|
    analyze_log(file, &v)
  }
end


#sep2
sep2.each do |k,v|
  puts '---' + k + '---'
  File.open(k+'.txt','w'){ |file|
    analyze_log(file, &v)
  }
end
