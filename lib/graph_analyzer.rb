# -*- coding: utf-8 -*-

module GraphAnalyzer

  def local_maximum(min_size=3, max_size=@node_size, &block)
    raise ArgumentError unless min_size < max_size
    subgraphs = []
    (min_size..max_size).to_a.reverse.each do |nodesize|
      @nodes.combination(nodesize).each do |ns|
        unless subgraphs.find{ |c| (c & ns) == ns} then
          cond = block.call(ns)
          subgraphs.push ns if cond
        end
      end
    end
    subgraphs
  end

  def local_maximum_cliques(min_size=3, max_size=@node_size)
    local_maximum(min_size,max_size){ |ns| clique?(ns)}
  end
  
  def clique?(nodes)
    nodes.each do |n|
      links = linked_nodes(n)
      return false unless (links & (nodes-[n])) == nodes-[n]
    end
    return true
  end
  
  def isolated_clique?(nodes)
    clique?(nodes) &&
      (count_edges_to_outside(nodes) < nodes.size)
  end

  def pseudo_clique?(nodes, deg_ave=0.9, deg_min=0.0)
    degrees = nodes.map{ |n| (linked_nodes(n) & nodes).size}
    deg_ave <= (degrees.inject(&:+) / degrees.size) &&
      deg_min <= degrees.min
  end

  def count_edges_to_outside(clique)
    clique.inject(0) do |sum, n|
      sum + (linked_nodes(n) - clique).size
    end
  end

  def edges_k_graph(n)
    (1...n).inject(&:+)
  end
  module_function :edges_k_graph

  #TODO
  def shortest_pass_betweenness(graph)
    betweenness = Hash.new{ |h,k|
      h[k] = Hash.new{ |hash,key|
        hash[key] = Hash.new
      }
    }

    @graph.nodes.each do |s|
      bet = betweenness[s]
      bet[s][:d] = 0
      bet[s][:w] = 1
      linked_nodes(s).each do |n|
        bet[n][:d] = bet[s][:d] + 1
        bet[n][:w] = bet[s][:w]

        #TODO
      end
    end
  end



end







