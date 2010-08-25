# -*- coding: utf-8 -*-
require 'graph_analyzer'

module GraphNetwork
  @nodes
  @node_size
  @data
  @graph
  attr_reader :nodes, :node_size, :data, :graph

  include GraphAnalyzer
  
  def get(from, to)
    raise NotImplementedError, 'get method must be implemented'
  end
  
  def has_edge?(form, to, weight=0.0)
    get(form, to).to_f > weight
  end

  def both_direction?(node_a, node_b, weight=0.0)
    has_edge?(node_a, node_b, weight) &&
      has_edge?(node_b, node_a, weight)
  end

  def nodes_from(node, weight=0.0)
    nodes = @nodes.select{ |n| has_edge?(node,n,weight)}
  end

  def nodes_to(node, weight=0.0)
    @nodes.select{ |n| has_edge?(n,node,weight)}
  end

  def linked_nodes(node, weight=0.0)
    @nodes.select{ |n| both_direction?(node,n,weight)}
  end

end

class SubGraph < Array
  @parent_graph
  
  def initialize(ary, parent_graph=nil)
    super(ary)
    @parent_graph = parent_graph
  end

  alias :array_equals :== ;
  def ==(other)
      (self.sort).array_equals(other.sort)
  end
  
  alias :array_include? :include?
  def include?(other)
    (self & other).sort == other
  end

#  alias :array_minus :-
#  def  -(other)
#    raise ArgumentError unless SubGraph === other
#    return self.sort.arry_minus(other.sort)
#  end

end

class AdjacencyMatrix

  def self.open(path)
    self.new(File.open(path).read)
  end

  def initialize(csv)
    @matrix = Hash.new{ |h,k| h[k] = Hash.new} # matrix[from][to]
    parse_csv(csv)
    @data = @matrix
    @graph = self
  end

  include GraphNetwork
  def get(from, to)
    @matrix[from][to]
  end
  
  def parse_csv(csv)
    lines = csv.split("\n")
    @nodes = lines.shift.split(',')[1..-1].map(&:to_sym)
    raise ArgumentError, 'node names must be unique' if @nodes != @nodes.uniq
    @node_size = nodes.length
    lines.each_with_index do |line, col_idx|
      data = line.split(',')
      from = data.shift.to_sym
      raise ArgumentError, 'invalid index order' if from != @nodes[col_idx]
      raise ArgumentError, 'invalid elements count' if data.length != @node_size
      data.each_with_index do |d, row_idx|
        raise TypeError, 'link weight must be Numeric' unless d =~ /\d+/
        weight = d.to_f
        @matrix[from][@nodes[row_idx]] = weight
      end
    end
    true
  end
end







