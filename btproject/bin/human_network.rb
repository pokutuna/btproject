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
  raise RuntimeError 'no logs read' if @loggers.empty?
  @loggers.each do |logger|
#    puts "analyzing #{logger.name}'s log"
    result = logger.analyze(&filter)
    result = result.to_a.sort_by{|i| i[1][0]}.reverse
    result = result.select{ |i| @bdas.include? i[0]}
    result.each do |i|
      name = @username[i[0]]
      #      puts "  #{name} detect:#{i[1][0]}  meet:#{i[1][1]}"
      str = "#{logger.name} -> #{name} [weight = #{i[1][0]}, arrowsize = #{Math.sqrt(i[1][0])/10.0}];" 
      puts str
      file.puts str unless file == nil
    end
    puts ""
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

read_log
puts '-- all --'
File.open('log_all.txt','w'){ |file|
  put_graphviz_header(file)  
  analyze_log(file)
  put_graphviz_footer(file)
}
puts '---------'



#sep1
puts '-- 0600 to 1200 --'
File.open('sep1_060000_120000.txt','w'){ |file|
  analyze_log(file){ |i| 6 <= i.date.hour && i.date.hour < 12}
}

puts '-- 1200 to 1800 --'
File.open('sep1_120000_180000.txt','w'){ |file|
  analyze_log(file){ |i| 12 <= i.date.hour && i.date.hour < 18}
}

puts '-- 1800 to 2400 --'
File.open('sep1_180000_235959.txt','w'){ |file|
  analyze_log(file){ |i| 18 <= i.date.hour && i.date.hour <= 23}
}


#sep2
puts '-- 0900 to 1030 --'
File.open('sep2_090000_103000.txt','w'){ |file|
  put_graphviz_header(file)
  analyze_log(file){ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 9, 10)
    b = Time.local(i.date.year, i.date.month, i.date.day, 10, 30)
    a <= i.date && i.date <b
  }
  put_graphviz_footer(file)
}

puts '-- 1030 to 1230 --'
File.open('sep2_103000_123000.txt','w'){ |file|
  put_graphviz_header(file)
  analyze_log(file){ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 10, 30)
    b = Time.local(i.date.year, i.date.month, i.date.day, 12, 30)
    a <= i.date && i.date <b
  }
  put_graphviz_footer(file)  
}

puts '-- 1230 to 1500 --'
File.open('sep2_123000_150000.txt','w'){ |file|
  put_graphviz_header(file)
  analyze_log(file){ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 12, 30)
    b = Time.local(i.date.year, i.date.month, i.date.day, 15, 00)
    a <= i.date && i.date <b
  }
  put_graphviz_footer(file)
}

puts '-- 1500 to 1630 --'
File.open('sep2_150000_163000.txt','w'){ |file|
  put_graphviz_header(file)
  analyze_log(file){ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 15, 00)
    b = Time.local(i.date.year, i.date.month, i.date.day, 16, 30)
    a <= i.date && i.date <b
  }
  put_graphviz_footer(file)
}

puts '-- 1630 to 1800 --'
File.open('sep2_163000_180000.txt','w'){ |file|
  put_graphviz_header(file)
  analyze_log(file){ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 16, 30)
    b = Time.local(i.date.year, i.date.month, i.date.day, 18, 00)
    a <= i.date && i.date <b
  }
  put_graphviz_footer(file)
}

puts '-- 1800 to 2359 --'
File.open('sep2_180000_235900.txt','w'){ |file|
  analyze_log(file){ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, 18, 00)
    b = Time.local(i.date.year, i.date.month, i.date.day, 23, 59)
    a <= i.date && i.date <b
  }
}


