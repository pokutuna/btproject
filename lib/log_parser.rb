# -*- coding: utf-8 -*-

require 'date'
require 'time'
require 'nkf'

class BDAPair
  @name
  @bda
  attr_reader :name, :bda

  def initialize(name, bda)
    @name = name
    @bda = bda
  end
end

class Record < BDAPair
  @date
  attr_reader :date

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

  def Logger.meets_threshold=(th)
    raise ArgumentError 'threshold must be Integer' unless Integer === th
    @@meets_threshold = th
  end

  def Logger.meets_threshold
    return @@meets_threshold
  end

  
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
    @records = @records.sort_by{ |r| r.date}
  end

  def create_record_list(recs=nil, &filter)
    if recs == nil then
      sort_record
      recs = @records
    end
    
    if block_given? then
      recs = recs.collect{ |i| filter.call(i) == true}
    end

    return recs
  end

  def analyze(&filter)
    recrods = create_record_list(&filter)
    merge_sub = lambda{ |k,s,o| s.merge(o)}

    results =[
      analyze_detect(records),
      analyze_meet(records)]

    dest = Hash.new
    results.each { |h| dest.merge!(h, &merge_sub)}
    return dest
  end

  def analyze_detect(records=nil, &filter)
    records = create_record_list(records, &filter)
    detect_count = Hash.new(0) #Hash bda => count
    
    records.each do |i|
      detect_count[i.bda] += 1
    end

    dest = Hash.new
    detect_count.each { |k,v| dest[k] = { :detects => v} }
    return dest
  end
  
  def analyze_meet(records=nil, &filter)
    records = create_record_list(records, &filter)
    meet_count = Hash.new(0)
    last_contact = Hash.new(Time.at(0)) #Hash bda => Time

    records.each do |i|
      diff = i.date - last_contact[i.bda]
      raise RuntimeError '' if diff < 0
      meet_count[i.bda] += 1 if diff > @@meets_threshold
      last_contact[i.bda] = i.date
    end
    
    dest = Hash.new
    meet_count.each { |k,v| dest[k] = { :meets => v} }
    return dest
  end

end

