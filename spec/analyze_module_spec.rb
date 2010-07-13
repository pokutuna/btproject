# -*- coding: utf-8 -*-
require 'spec_helper'
require 'analyze_module'

describe AnalyzeHumanNetwork do
  context 'when setting threhold' do
    it 'should have default threshold' do
      AnalyzeHumanNetwork.meets_threshold.should == 60*5
      AnalyzeHumanNetwork.time_threshold == 60
    end
    
    it 'should be mutable threshold' do
      AnalyzeHumanNetwork.meets_threshold = 100
      AnalyzeHumanNetwork.time_threshold = 200
      AnalyzeHumanNetwork.meets_threshold.should == 100
      AnalyzeHumanNetwork.time_threshold == 200
    end
  end
end
