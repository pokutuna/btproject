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
