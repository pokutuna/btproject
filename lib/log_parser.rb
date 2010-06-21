# -*- coding: utf-8 -*-

require 'date'
require 'time'

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
    ary = line.split("\t")
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

  def analyze(&filter)
    sort_record

    detect_count = Hash.new(0) #Hash bda => count
    meet_count = Hash.new(0)
    last_contact = Hash.new(Time.at(0)) #Hash bda => Time

    @records.each do |i|
      if block_given? then
        next unless filter.call(i) == true
      end
      
      detect_count[i.bda] += 1
      
      diff = i.date - last_contact[i.bda]
      raise RuntimeError '' if diff < 0
      meet_count[i.bda] += 1 if diff > @@meets_threshold
      
      last_contact[i.bda] = i.date
    end

    result = Hash.new
    keys = detect_count.keys
    keys.each do |i|
      result[i] =
        { :detects => detect_count[i], :meets => meet_count[i]}
    end
    
    return result
  end
  
end

