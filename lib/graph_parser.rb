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

  def type
    raise NotImplementedError, 'set graph direction type'
  end
  
  def has_edge?(form, to, weight=0.0)
    get(form, to).to_f > weight
  end

  def both_direction?(node_a, node_b, weight=0.0)
    has_edge?(node_a, node_b, weight) &&
      has_edge?(node_b, node_a, weight)
  end

  def either_direction?(node_a, node_b, weight=0.0)
    has_edge?(node_a, node_b, weight) ||
      has_edge?(node_b, node_a, weight)
  end

  def nodes_from(node, weight=0.0)
    nodes = @nodes.select{ |n| has_edge?(node,n,weight)}
    SubGraph.new(nodes, @graph)
  end

  def nodes_to(node, weight=0.0)
    nodes = @nodes.select{ |n| has_edge?(n,node,weight)}
    SubGraph.new(nodes, @graph)
  end

  def linked_nodes(node, weight=0.0)
    if type == :digraph
      both_linked_nodes(node, weight=0.0)
    elsif type == :graph
      either_linked_nodes(node, weight=0.0)
    end
  end

  def both_linked_nodes(node, weight=0.0)
    nodes = @nodes.select{ |n| both_direction?(node,n,weight)}
    SubGraph.new(nodes, @graph)
  end

  def either_linked_nodes(node, weight=0.0)
    nodes = @nodes.select{ |n| either_direction?(node,n,weight)}
    SubGraph.new(nodes, @graph)
  end

end

class SubGraph < Array
  @parent_graph
  
  def initialize(ary, parent_graph=nil)
    super(ary)
    @parent_graph = parent_graph
  end

  alias :array_equals :==;
  def ==(other)
    (self.sort).array_equals(other.sort)
  end

  alias :array_minus :-
  def -(other)
    SubGraph.new(self.sort.array_minus(other.sort), self)
  end

  alias :array_and :&
  def &(other)
    SubGraph.new(self.sort.array_and(other.sort), self)
  end

  alias :array_include? :include?
  def include?(other)
    (self & other) == other
  end

end

class AdjacencyMatrix < SubGraph

  def self.open(path, type=:digraph)
    self.new(File.open(path).read, type)
  end

  def initialize(csv, type=:digraph)
    @matrix = Hash.new{ |h,k| h[k] = Hash.new} # matrix[from][to]
    parse_csv(csv)
    @data = @matrix
    @graph = self
    @type = type
    super(@nodes, self)
    true
  end

  include GraphNetwork
  def get(from, to)
    @matrix[from][to]
  end

  def type
    @type
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

