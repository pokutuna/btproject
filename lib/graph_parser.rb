# -*- coding: utf-8 -*-

require 'csv'

module GraphNetwork
  @nodes
  @node_size
  @data
  attr_reader :nodes, :node_size, :data

  def get(from, to)
    raise NotImplementedError
  end
  
  def has_edge?(form, to, weight=0.0)
    get(form, to).to_f > weight
  end

  def nodes_from(node, weight=0.0)
    @nodes.select{ |n| has_edge?(node,n,weight)}
  end

  def nodes_to(node, weight=0.0)
    @nodes.select{ |n| has_edge?(n,node,weight)}
  end
    
end


class AdjacencyMatrix

  def self.open(path)
    self.new(File.open(path).read)
  end

  def initialize(csv)
    @matrix = Hash.new{ |h,k| Hash.new} # matrix[from][to]
    parse_csv(csv)
    @data = @matrix
  end

  include GraphNetwork
  def get(from, to)
    @matrix[from][to]
  end
  
  def parse_csv(csv)
    lines = csv.split("\n")
    @nodes = lines.shift.split(',')[1..-1].map(&:to_sym)
    @node_size = nodes.length
    lines.each_with_index do |line, col_idx|
      data = line.split(',')
      from = data.shift.to_sym
      raise ArgumentError, 'invalid index order' if from != @nodes[col_idx]
      raise ArgumentError, 'invalid elements count' if data.length != @node_size
      data.each_with_index do |d, row_idx|
        raise TypeError, 'link weight must be Numeric' unless d.is_a? Numeric
        @matrix[from][@nodes[row_idx]] = d
      end
    end
  end

end

class AdjacencyList

  include GraphNetwork
end






