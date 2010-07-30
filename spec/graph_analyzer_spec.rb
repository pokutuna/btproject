# -*- coding: utf-8 -*-
require 'spec_helper'
require 'graph_parser'
require 'graph_analyzer'

describe "graph_analyzer.rb" do
  before(:all) do
    path = File.dirname(__FILE__)+'/sampledata/valid_graph.csv'
    @graph = AdjacencyMatrix.open(path)
  end

  it "should enumerate local_maximum_cliques" do
    local_maximum_cliques(@graph).should == [[:A,:B,:C,:E]]
  end

  it "should check nodes are clique" do
    is_clique?([:A,:B], @graph).should == true
    is_clique?([:A,:B,:C,:E],@graph).should == true
    is_clique?([:D],@graph).should == true
    is_clique?([:A,:D],@graph).should == false
  end

  it "should check isolated clique" do
    is_isolated_clique?([:A,:B,:C,:E],@graph).should == true
  end

  it "should get edges in complete k-graph" do
    edges_k_graph(3).should == 3
    edges_k_graph(4).should == 6
    edges_k_graph(5).should == 10
  end
  

end
