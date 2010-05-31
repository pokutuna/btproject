# -*- coding: utf-8 -*-
require 'spec_helper'
require 'log_parser'

describe Record, 'when parsing log data' do
  before(:all) do
    @sample1 = '2009/11/10 10:39:12	pokutuna-ThinkPad	00:19:7E:F6:F5:5D'
    @sample2 = '2009/11/20 17:8:0		00:22:F3:9C:37:D8'
    @sample3 = '2009/11/20	17:10:0	hoge-pc	00:1B:DC:00:04:18'
    @sample4 = '2009/11/20	17:8:0		00:1B:DC:00:0F:5B'
  end

  it 'should parse a log line' do
    record = Record.new(@sample1)
    record.date.should == Time.local(2009,11,10,10,39,12)
    record.name.should == 'pokutuna-ThinkPad'
    record.bda.should == '00:19:7E:F6:F5:5D'
  end

  it 'should parse a log line without device name' do
    record = Record.new(@sample2)
    record.name.should == ''
    record.bda.should == '00:22:F3:9C:37:D8'
  end

  it 'should parse string has extra TAB between day and time' do
    record = Record.new(@sample3)
    record.name.should == 'hoge-pc'
    record.bda.should == '00:1B:DC:00:04:18'
  end

  it 'should parse string has extra TAB without device name' do
    record = Record.new(@sample4)
    record.name.should == ''
    record.bda.should == '00:1B:DC:00:0F:5B'
  end

end

describe Record, 'when comparing' do
  before(:all) do
    @sample1 = Record.new('2009/11/8 19:13:8	pokutuna-ThinkPad	00:19:7E:F6:F5:5D')
    @sample2 = Record.new('2009/11/8 19:13:8	hoge	00:19:7E:F6:F5:5D')
    @sample3 = Record.new('2009/11/8 19:34:39	pokutuna-ThinkPad	00:19:7E:F6:F5:5D')
  end

  it 'should compare about same time Record object' do
    @sample1.should == @sample2
  end

  it 'should compare about old time Record object' do
    @sample1.should < @sample3
  end

  it 'should compare with Time object' do
    @sample1.should == Time.parse('2009/11/8 19:13:8')
    @sample1.should < Time.parse('2010/11/18 19:13:8')
  end
  
  
end



describe Logger do
  context 'when create new Logger object' do
    it 'should be empty Hashes'
    it 'should be zero, count default'
    it 'should be Unix epoch time, last contact default'
  end

  context 'when add_record' do
    it 'should raise ArgumentError by adding other class'
    it 'should raise RuntimeError by adding older record'
  end

  context 'when count by BDA' do
    it 'should return zero, unregistered BDA'
  end
end
