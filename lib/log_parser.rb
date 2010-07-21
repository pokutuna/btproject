# -*- coding: utf-8 -*-

require 'date'
require 'time'
require 'nkf'
require 'analyze_module'

class Record
  @date
  @name
  @bda
  attr_reader :date, :name, :bda

  def initialize(line)
    begin
      ary = line.chomp.split("\t")
    rescue ArgumentError
      ary = NKF.nkf("-w -S -m0", line).chomp.split("\t")
    end
    
    if ary.length == 4 then
      ary[0]+= ' ' +ary[1]
      ary.delete_at(1)
    elsif ary.length != 3
      raise ArgumentError, 'invalid line'
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
  include AnalyzeHumanNetwork

  @name
  @records
  attr_reader :name, :records

  def initialize(name=nil)
    @name = name 
    @records = []
  end

  def read_log(glob, messageIO = STDOUT)
    Dir.glob(File.expand_path(glob)).each do |filename|
      File.open(filename){ |file|
        messageIO.puts File.basename(file.path)
        
        file.each_line do |line|
          next if line.strip[0] == '#' or line.strip == ''
          record = Record.new(line)
          add_record(record) unless record == nil
        end
      }
    end
  end
  
  def add_record(record)
    raise ArgumentError unless Record === record
    @records.push record
  end

  def sort_record
    @records = @records.sort_by{ |r| r.date}
  end

  def filter_records(&filter)
    sort_record
    if block_given?
      record_list = @records.select{ |r| filter.call(r) == true}
    else
      @records
    end
  end
end

