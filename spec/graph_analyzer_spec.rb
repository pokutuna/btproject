# -*- coding: utf-8 -*-
require 'spec_helper'
require 'graph_parser'
require 'graph_analyzer'

describe GraphAnalyzer do
  before(:all) do
    path = File.dirname(__FILE__)+'/sampledata/valid_graph.csv'
    @graph = AdjacencyMatrix.open(path)
  end

  it "should enumerate local_maximum_cliques" do
    @graph.local_maximum_cliques.should == [[:A,:B,:C,:E]]
  end

  it "should check nodes are clique" do
    @graph.clique?([:A,:B]).should == true
    @graph.clique?([:A,:B,:C,:E]).should == true
    @graph.clique?([:D]).should == true
    @graph.clique?([:A,:D]).should == false
  end

  it "should check isolated clique" do
    @graph.isolated_clique?([:A,:B,:C,:E]).should == true
  end

  it "should get edges in complete k-graph" do
    GraphAnalyzer.edges_k_graph(3).should == 3
    GraphAnalyzer.edges_k_graph(4).should == 6
    GraphAnalyzer.edges_k_graph(5).should == 10
  end
  

end
