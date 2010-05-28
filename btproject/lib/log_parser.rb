# -*- coding: utf-8 -*-

require 'date'
require 'time'

class Record
  @date
  @name
  @bda
  attr_reader :date, :name, :bda

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


class DataContainer
  @loggers
end

class Logger
  @name
  @detect_count = Hash.new(0) #Hash bda => count
  @last_contact = Hash.new() #Hash bda => Time
end






    
