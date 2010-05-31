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
  @@threshold = 60 * 5 #TODO
  @name
  @detect_count
  @meet_count
  @last_contact
  attr_reader :name, :detect_count, :meet_count, :last_contact
  
  def initialize(name)
    @name = name 
    @detect_count = Hash.new(0) #Hash bda => count
    @meet_count = Hash.new(0)
    @last_contact = Hash.new(Time.at(0)) #Hash bda => Time
  end
  
  def add_record(record)
    raise ArgumentError unless Record === record
    @detect_count[record.bda] += 1
    
    diff = record.date - @last_contact[record.bda]
    raise RuntimeError 'added older record' if diff < 0
    @meet_count[record.bda] += 1 if diff > @@threshold
    
    @last_contact[record.bda] = record.date
  end

  def count_byBDA(bda)
    return [@detect_count, @meet_count]
  end
end



=begin
File.open('../spec/sampledata/samplelog_day1.tsv'){ |f|
  hoge = Logger.new('hoge')
  f.each_line do |line|
    hoge.add_record(Record.new(line.chomp)) unless line[0] == '#'
  end
  p hoge
}
=end

    
