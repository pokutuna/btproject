# -*- coding: utf-8 -*-

require File.dirname(__FILE__) + '/spec_helper.rb'
require 'date'
require 'time'

describe Time, "when parsing String to Time" do

  it "should parse our log format" do
    Time.parse('2009/11/9 8:19:20').should == Time.local(2009,11,9,8,19,20)
  end

  it "should parse string include TAB" do
    Time.parse('2009/11/20	17:8:0').should == Time.local(2009,11,20,17,8,0)
  end
  

end

