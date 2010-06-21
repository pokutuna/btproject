# -*- coding: utf-8 -*-
$LOAD_PATH.unshift(File.dirname(__FILE__) + '/../lib')

require 'yaml'
require 'optparse'
require 'log_parser'

OPTS = {}
opt = OptionParser.new
opt.on('-g'){ |v| OPTS[:g] = v}
opt.parse!


@users = YAML.load(ARGF.read)

@bdas = @users.map{ |k,v| v[1]} # BDA List
@bda_to_name = {}
@users.each { |k,v| @bda_to_name[v[1]] = k }
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
  raise RuntimeError 'read no logs' if @loggers.empty?
  dest = Hash.new
  @loggers.each do |logger|
    result = logger.analyze(&filter)
    dest[logger] = result
  end
  return dest
end


def put_analyzed_count(analyzed, file=nil)
  analyzed.each do |logger,result|
    str = logger.name + "---"
    puts str
    file.puts str unless file == nil

    sorted = result.to_a.sort_by{ |i| i[1][:detects]}.reverse
    sorted.each do |i|
      str = "#{@bda_to_name[i[0]]} detect: #{i[1][:detects]} meet: #{i[1][:meets]}"
      puts str
      file.puts str unless file == nil
    end
  end
end

def put_analyzed_graphviz(analyzed, file=nil)
  put_graphviz_header(file)

  analyzed.each_key do |logger|
    str = logger.name
    str = str + '[color = "#c0c0c0"]' if analyzed[logger].empty?
    str = str + ';'
    puts str
    file.puts str unless file == nil
  end

  analyzed.each do |logger, result|
    result.each do |bda, data|
      str = "#{logger.name} -> #{@bda_to_name[bda]} [weight = #{data[:detects]}, arrowsize = #{Math.sqrt(data[:detects])/10.0}];"
      puts str
      file.puts str unless file == nil
    end
  end
  put_graphviz_footer(file)
end


def put_graphviz_header(file=nil)
  str = 'digraph sample{'
  puts str
  file.puts str unless file == nil
  
  str = 'graph [size="40,30", concentrate=true];'
  puts str
  file.puts str unless file ==nil
end

def put_graphviz_footer(file=nil)
  puts '}'
  file.puts '}' unless file == nil
end



#separate pattern
users_included = lambda { |i| @bdas.include?(i.bda) }

sep1_morning = lambda{ |i| 
  @bdas.include?(i.bda) && (6 <= i.date.hour && i.date.hour < 12)}
sep1_evening = lambda{ |i|
  @bdas.include?(i.bda) && (12 <= i.date.hour && i.date.hour < 18)}
sep1_night = lambda{ |i|
  @bdas.include?(i.bda) && (18 <= i.date.hour && i.date.hour <= 23)}

def generate_timefilter(a_hour, a_min, b_hour, b_min)
  lambda{ |i|
    a = Time.local(i.date.year, i.date.month, i.date.day, a_hour, a_min)
    b = Time.local(i.date.year, i.date.month, i.date.day, b_hour, b_min)
    @bdas.include?(i.bda) && (a <= i.date && i.date <b)
  }
end

sep2_0900_1030 = generate_timefilter( 9,00, 10,30)
sep2_1030_1230 = generate_timefilter(10,30, 12,30)
sep2_1230_1500 = generate_timefilter(12,30, 15,00)
sep2_1500_1630 = generate_timefilter(15,00, 16,30)
sep2_1630_1800 = generate_timefilter(16,30, 18,00)
sep2_1800_2359 = generate_timefilter(18,00, 23,59)

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




if __FILE__ == $0 then
  read_log
  put_method = nil
  if OPTS[:g] == true
    put_method = method(:put_analyzed_graphviz)
  else
    put_method = method(:put_analyzed_count)
  end
  
  puts '-- all --'
  File.open('log_all.txt','w'){ |file|
    put_method.call(analyze_log(&users_included), file)
  }
  puts '---------'

  #sep1
  sep1.each do |k,v|
    puts '---' + k + '---'
    File.open(k+'.txt','w'){ |file|
      put_method.call(analyze_log(&v), file)
    }
  end


  #sep2
  sep2.each do |k,v|
    puts '---' + k + '---'
    File.open(k+'.txt','w'){ |file|
      put_method.call(analyze_log(&v), file)
    }
  end

end
