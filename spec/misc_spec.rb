# -*- coding: utf-8 -*-

require File.dirname(__FILE__) + '/spec_helper.rb'
require 'date'
require 'time'

describe Time do

  context 'parse method' do
    it "should parse our log format" do
      Time.parse('2009/11/9 8:19:20').should == Time.local(2009,11,9,8,19,20)
    end

    it "should parse string include TAB" do
      Time.parse('2009/11/20	17:8:0').should == Time.local(2009,11,20,17,8,0)
    end
  end

  context 'compare methods' do
    it "-@ method return difference of seconds" do
      before = Time.local(2010,5,31,13,56,10)
      after = Time.local(2010,5,31,13,56,20)
      (after-before).should == 10.0

      before = Time.local(2010,5,31,13,56,10)
      after = Time.local(2010,6,1,13,56,10)
      (after-before).should == 60*60*24.to_f
    end
  end

  context 'generate methods' do
    it "generate by at method with 0 to_i is zero" do
      Time.at(0).to_i.should == 0
    end
  end
  
end



