# -*- coding: utf-8 -*-

require 'date'
require 'time'
require 'nkf'

class Record
  @date
  @name
  @bda
  attr_reader :date, :name, :bda

  def initialize(line)
    begin
      ary = line.split("\t")
    rescue ArgumentError
      ary = NKF.nkf("-w -S -m0", line).split("\t")
    end
    
    if ary.length > 3 then
      ary[0]+= ' ' +ary[1]
      ary.delete_at(1)
    end
    
    @date = Time.parse(ary[0])
    @name = ary[1] ||= ''
    @bda = ary[2]
  end

  include Comparable
  def <=> (other)
    if Time === other
      @date <=> other
    else
      @date <=> other.date
    end
  end
  
end


class Logger
  @@meets_threshold = 60 * 5 #TODO
  @@time_threshold = 60
  
  def Logger.meets_threshold=(th); @@meets_threshold = th end
  def Logger.meets_threshold; return @@meets_threshold; end

  def Logger.time_threshold=(sec); @@time_threshold = sec; end
  def Logger.time_threshold; return @@time_threshold; end
  
  @name
  @records
  attr_reader :name, :records

  def initialize(name=nil)
    @name = name 
    @records = []
  end

  def add_record(record)
    raise ArgumentError unless Record === record
    @records.push record
  end

  def sort_record
    @records = @records.sort_by{ |r| r.date}.reverse
  end

  def create_record_list(recs=nil, &filter)

    if recs == nil then
      sort_record
      recs = @records
    end
    
    if block_given? then
      recs = recs.select{ |i| filter.call(i) == true}
    end

    return recs
  end

  def create_inner_result(name, result)
    dest = Hash.new
    result.each { |k,v| dest[k] = { name => v}}
    return dest
  end
  
  def analyze(&filter)
    #recrods = create_record_list(&filter) # !!!bug!!!!
    merge_sub = lambda{ |k,s,o| s.merge(o)}

    results =[
      analyze_detect(records,&filter), # each take filter...
      analyze_meet(records,&filter),
      analyze_time(records,&filter) ]

    dest = Hash.new
    results.each { |h| dest.merge!(h, &merge_sub)}
    analyzed = true
    return dest
  end

  def analyze_detect(records=nil, &filter)
    records = create_record_list(records, &filter)
    detect_count = Hash.new(0) #Hash bda => count
    
    records.each do |i|
      detect_count[i.bda] += 1
    end

    return create_inner_result(:detects, detect_count)
  end
  
  def analyze_meet(records=nil, &filter)
    records = create_record_list(records, &filter)
    meet_count = Hash.new(0)
    last_contact = Hash.new(Time.at(0)) #Hash bda => Time

    records.each do |i|
      diff = i.date - last_contact[i.bda]
      meet_count[i.bda] += 1 if diff > @@meets_threshold
      last_contact[i.bda] = i.date
    end

    return create_inner_result(:meets, meet_count)
  end

  def analyze_time(records=nil, &filter)
    records = create_record_list(records, &filter)
    time_sum = Hash.new(0) #Hash bda => Time(sec)
    last_contact = Hash.new

    records.each do |i|
      unless last_contact[i.bda] == nil
        diff = i.date - last_contact[i.bda]
        time_sum[i.bda] += diff if diff < @@time_threshold
      end
      last_contact[i.bda] = i.date
    end

    return create_inner_result(:time, time_sum)
  end

end

