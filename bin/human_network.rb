# -*- coding: utf-8 -*-
$LOAD_PATH.unshift(File.dirname(__FILE__) + '/../lib')

require 'yaml'
require 'optparse'
require 'fileutils'
require 'log_parser'

OPTS = {}
opt = OptionParser.new
opt.on('-g'){ |b| OPTS[:g] = b}
opt.on('-r'){ |b| OPTS[:r] = b}
opt.on('-w'){ |b| OPTS[:w] = b}
opt.on('-t'){ |b| OPTS[:t] = b}
opt.parse!


@users = YAML.load(ARGF.read)

@bdas = @users.map{ |k,v| v[1]}.delete_if{ |i| i==nil} # BDA List
@bda_to_name = {}
@users.each { |k,v| @bda_to_name[v[1]] = k }
@loggers = []


def read_log
  @users.each{ |k,v|
    logger = Logger.new(k)
    puts "--- reading #{k}'s log files ---"
    logger.read_log(v[0])
    @loggers.push logger
  }
  return @loggers
end


def analyze_log(&filter)
  raise RuntimeError 'no loggers' if @loggers.empty?
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
      str = "#{@bda_to_name[i[0]]} detect: #{i[1][:detects]} meet: #{i[1][:meets]} time: #{i[1][:time]}"
      puts str
      file.puts str unless file == nil
    end
  end
end

def put_graphviz_nodes(analyzed, file=nil)
  analyzed.each_key do |logger|
    str = logger.name
    str += '[style = "filled", fillcolor = "#c0c0c0"]' if analyzed[logger].empty?
    str += ';'
    puts str
    file.puts str unless file == nil
  end
end


def put_analyzed_graphviz(analyzed, file=nil)
  put_graphviz_header(file)
  put_graphviz_nodes(analyzed, file)
  
  analyzed.each do |logger,result|
    result.each do |bda,data|
      str = "#{logger.name} -> #{@bda_to_name[bda]} [weight = #{data[:detects]}, arrowsize = #{Math.sqrt(data[:detects])/10.0}];"
      puts str
      file.puts str unless file == nil
    end
  end
  put_graphviz_footer(file)
end


def put_analyzed_rank(analyzed, file=nil)
  put_graphviz_header(file, false)
  put_graphviz_nodes(analyzed, file)

  def _put_sorted_rank(color, logger, sorted, file=nil)
    sorted.slice(0,3).each_with_index do |i, idx|
      str = "#{logger.name} -> #{@bda_to_name[i[0]]} [weight = #{i[1][:detects]}, color = \"#{color},#{1.0-(idx/2.0)},1.0\"];"
      puts str
      file.puts str unless file == nil
    end
  end

  analyzed.each do |logger,result|
    sorted = result.to_a.sort_by{ |i| i[1][:detects]}.reverse
    _put_sorted_rank(1.0, logger, sorted, file)
    sorted = result.to_a.sort_by{ |i| i[1][:meets]}.reverse
    _put_sorted_rank(0.66,logger, sorted, file)
  end
  
  put_graphviz_footer(file)
end


def put_analyzed_weight(analyzed, file=nil)
  put_graphviz_header(file)
  put_graphviz_nodes(analyzed, file)

  #calc meets weight
  detects, meets = 0, 0
  analyzed.each_value do |result|
    result.each_value do |data|
      detects += data[:detects]
      meets += data[:meets]
    end
  end
  meets_weight = detects / meets.to_f
  p meets_weight
  gets

  analyzed.each do |logger,result|
    result.each do |bda,data|
      str = "#{logger.name} -> #{@bda_to_name[bda]} [weight = #{data[:detects]}, arrowsize = #{Math.sqrt(data[:detects]+data[:meets]*meets_weight)/10.0}];"
      puts str
      file.puts str unless file == nil
    end
  end
  put_graphviz_footer(file)
end

#TODO
@analyzed_container = []
def put_analyze_time(analyzed, file=nil)
  @analyzed_container.push analyzed
end

def lazy_analyze_time(file=nil)
  put_graphviz_header(file)
  
  
  @analyzed_container.each do |analyzed|
    analyzed.each do |logger,result|
      result.each do |bda,data|
        
      end
    end
  end
  
  put_graphviz_footer(file)
end

def put_graphviz_header(file=nil, con=true)
  str = 'digraph sample{'
  puts str
  file.puts str unless file == nil
  if con then
    str = 'graph [size="40,40", concentrate=true];'
  else
    str = 'graph [size="40,40", concentrate=false];'
  end
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

sep3 = {
  'sep3_0900_0930' => generate_timefilter( 9,00,  9,30),
  'sep3_0930_1000' => generate_timefilter( 9,30, 10,00),
  'sep3_1000_1030' => generate_timefilter(10,00, 10,30),
  'sep3_1030_1100' => generate_timefilter(10,30, 11,00),
  'sep3_1100_1130' => generate_timefilter(11,00, 11,30),
  'sep3_1130_1200' => generate_timefilter(11,30, 12,00),
  'sep3_1200_1230' => generate_timefilter(12,00, 12,30),
  'sep3_1230_1300' => generate_timefilter(12,30, 13,00),
  'sep3_1300_1330' => generate_timefilter(13,00, 13,30),
  'sep3_1330_1400' => generate_timefilter(13,30, 14,00),
  'sep3_1400_1430' => generate_timefilter(14,00, 14,30),
  'sep3_1430_1500' => generate_timefilter(14,30, 15,00),
  'sep3_1500_1530' => generate_timefilter(15,00, 15,30),
  'sep3_1530_1600' => generate_timefilter(15,30, 16,00),
  'sep3_1600_1630' => generate_timefilter(16,00, 16,30),
  'sep3_1630_1700' => generate_timefilter(16,30, 17,00),
  'sep3_1700_1730' => generate_timefilter(17,00, 17,30),
  'sep3_1730_1800' => generate_timefilter(17,30, 18,00),
  'sep3_1800_1830' => generate_timefilter(18,00, 18,30)
}



if __FILE__ == $0 then
  read_log
  put_method = nil
  prefix = nil
  if OPTS[:g] == true
    put_method = method(:put_analyzed_graphviz)
    prefix = 'detect'
  elsif OPTS[:r] == true
    put_method = method(:put_analyzed_rank)
    prefix = 'rank'
  elsif OPTS[:w] == true
    put_method = method(:put_analyzed_weight)
    prefix = 'weight'
  elsif OPTS[:t] == true
    put_method = method(:put_analyze_time)
    prefix = 'time'
  else
    put_method = method(:put_analyzed_count)
    prefix = 'count'
  end

  path = './'+prefix
  FileUtils.mkdir_p(path) unless FileTest.exist?(path)
  puts '-- all --'
    File.open(path + '/log_all.txt','w'){ |file|
    put_method.call(analyze_log(&users_included), file)
  }
  puts '---------'

  #sep1
  sep1.each do |k,v|
    puts '---' + k + '---'
    File.open(path + '/' + k+'.txt','w'){ |file|
      put_method.call(analyze_log(&v), file)
    }
  end
  if prefix == 'time' then
    File.open(path+'/sep1.txt','w'){ |file|
      lazy_analyze_time(file)
    }
    @analyzed_container = []
  end
  
  #sep2
  sep2.each do |k,v|
    puts '---' + k + '---'
    File.open(path + '/' + k+'.txt','w'){ |file|
      put_method.call(analyze_log(&v), file)
    }
  end
  if prefix == 'time' then
    File.open(path+'/sep2.txt','w'){ |file|
      lazy_analyze_time(file)
    }
    @analyzed_container = []
  end
  
  #sep3
  sep3.each do |k,v|
    puts '---' + k + '---'
    File.open(path+ '/' + k + '.txt', 'w'){ |file|
      put_method.call(analyze_log(&v), file)
    }
  end
  if prefix == 'time' then
    File.open(path+'/sep3.txt','w'){ |file|
      lazy_analyze_time(file)
    }
    @analyzed_container = []
  end
end

