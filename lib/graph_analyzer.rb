# -*- coding: utf-8 -*-

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


def local_maximum_cliques(graph, lower_clique_size=2)
  cliques = []
  (lower_clique_size..graph.node_size).to_a.reverse.each do |nodesize|
    graph.nodes.combination(nodesize).each do |nodes|
      unless cliques.find{ |c| (c & nodes) == nodes} then
        cliques.push nodes if is_clique?(nodes, graph)
      end
    end
  end
  cliques
end


#TODO export to graph parser?
def is_clique?(nodes, graph)
  nodes.each do |n|
    links = graph.linked_nodes(n)
    return false unless (links & (nodes-[n])) == nodes-[n]
  end
  return true
end

def edges_to_outside(nodes, graph)
  nodes.inject(0) do |sum, n|
    sum + (graph.linked_nodes(n) - nodes).size
  end
end

def edges_k_graph(n)
  (1...n).inject(&:+)
end

def is_isolated_clique?(nodes, graph)
  is_clique?(nodes, graph) &&
    (edges_to_outside(nodes, graph) < nodes.size)
end
