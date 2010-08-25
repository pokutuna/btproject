# -*- coding: utf-8 -*-
require 'spec_helper'
require 'graph_parser'

describe AdjacencyMatrix do
  context 'when opening file' do
    it 'should be openable from class method' do
      path = File.dirname(__FILE__)+'/sampledata/valid_graph.csv'
      graph = AdjacencyMatrix.open(path)
      graph.class.should == AdjacencyMatrix
    end

    it 'should be loadable by initializer' do
      csv = <<EOS
CSV,A,B,C
A,0,1,0
B,1,0,1
C,0,1,0
EOS
      graph = AdjacencyMatrix.new(csv)
      graph.class.should == AdjacencyMatrix
    end
  end
  
  context 'when parsing csv' do
    it 'should raise error when given not uniq node names' do
      path = File.dirname(__FILE__)+'/sampledata/not_uniq_node.csv'
      proc{ graph = AdjacencyMatrix.open(path)}.should raise_error(ArgumentError,'node names must be unique')
    end

    it 'should raise error when given invalid index order' do
      path = File.dirname(__FILE__)+'/sampledata/invalid_index_order.csv'
      proc{ graph = AdjacencyMatrix.open(path) }.should raise_error(ArgumentError,'invalid index order')
    end
    
    it 'should raise error when given invalid elements count' do
      path = File.dirname(__FILE__)+'/sampledata/invalid_elements_count.csv'
      proc{ graph = AdjacencyMatrix.open(path) }.should raise_error(ArgumentError,'invalid elements count')
    end
    
    it 'should raise error when given not Numeric weight' do
      path = File.dirname(__FILE__)+'/sampledata/invalid_not_numeric.csv'
      proc{ graph = AdjacencyMatrix.open(path) }.should raise_error(TypeError, 'link weight must be Numeric')
    end
    
  end
end


describe SubGraph do
  it 'should be a subclass of Array' do
    SubGraph.new([1,2,3]).is_a?(Array).should be_true
  end

  it 'should compare equality like Set' do
    (SubGraph.new([:a,:b,:c,:d]) == SubGraph.new([:c,:d,:a,:b])).should be_true
  end

  it 'should check include other SubGraph' do
    SubGraph.new([:a,:b,:c,:d,:e]).include?([:b,:c]).should be_true
  end
end


describe GraphNetwork, 'when only module' do
  it 'should raise NotImplementedError' do
    class TestGraph; include GraphNetwork; end
    g = TestGraph.new
    proc{ g.get(:a,:b) }.should raise_error(NotImplementedError)
  end

  context 'when getting data' do
    before(:all) do
      path = File.dirname(__FILE__)+'/sampledata/valid_graph.csv'
      @graph = AdjacencyMatrix.open(path)
    end

    it 'should get node to node weight' do
      @graph.get(:A, :B).should == 1.0
      @graph.get(:A, :A).should == 0.0
      @graph.get(:A, :E).should == 4.0
      @graph.get(:E, :E).should == 1.0
      @graph.get(:C, :C).should == 3.3
    end

    it 'should check having edge' do 
      @graph.has_edge?(:A, :A).should == false
      @graph.has_edge?(:A, :D).should == true
      @graph.has_edge?(:D, :A).should == false
      @graph.has_edge?(:C, :D).should == false
    end

    it 'should check having edge with weight' do
      @graph.has_edge?(:A, :B, 0.5).should == true
      @graph.has_edge?(:A, :B, 1.0).should == false
      @graph.has_edge?(:A, :B, 0.0).should == true
    end

    it 'should check having edge both direction' do
      @graph.both_direction?(:A, :B).should == true
      @graph.both_direction?(:B, :A).should == true
      @graph.both_direction?(:A, :D).should == false
    end

    it 'should check having edge both direction with weight' do
      @graph.both_direction?(:A, :B, 0.5).should == true
      @graph.both_direction?(:B, :A, 2.0).should == false
      @graph.both_direction?(:C, :A, 3.1).should == false
    end

    it 'should get nodes by from one node' do
      (@graph.nodes_from(:A)-[:B,:C,:D,:E]).should be_empty
      @graph.nodes_from(:D).should be_empty
      (@graph.nodes_from(:C)-[:A,:B,:C,:E]).should be_empty
    end
    
    it 'should get nodes by from one node with weight' do
      (@graph.nodes_from(:A, 2.5)-[:D,:E]).should be_empty
      (@graph.nodes_from(:E, 0.0)-@graph.nodes).should be_empty
      (@graph.nodes_from(:C, 0.0)-[:A,:B,:C,:E]).should be_empty
    end

    it 'should get nodes by to one node' do
      (@graph.nodes_to(:A)-[:B,:C,:E]).should be_empty
      (@graph.nodes_to(:C)-[:A,:B,:C,:E]).should be_empty
    end
    
    it 'should get nodes by to one node with weight' do
      @graph.nodes_to(:A, 4.0).should == [:B]
      (@graph.nodes_to(:D, 0.0)-[:A,:B,:E]).should be_empty
      @graph.nodes_to(:D, 8.0).should be_empty
      (@graph.nodes_to(:D, 2.5)-[:A,:B]).should be_empty
    end

    it 'should get nodes connected one node'
  end
  
end

