$LOAD_PATH.unshift(File.dirname(__FILE__)+'/../lib')

require 'graph_parser'
require 'graph_analyzer'
require 'graphviz'

sample = ['sep3_1030_1100', 'sep3_1100_1130', 'sep3_1230_1300']
hci = ['okuura','ushikoshi','kono','tokuami']
net = ['EEEPC06','EEEPC13','EEEPC16','eeepc04','eeepc08','eeepc11','eeepc12','eeepc15','eeepc18']
[hci, net].each{ |strs| strs = strs.map(&:to_sym)}

sample.map{ |s| "./net/#{s}.txt"}.each do |path|
  @graph = AdjacencyMatrix.open(path, :graph)
  p path
  
  cliques = @graph.local_maximum_cliques
  
  isolated = @graph.local_maximum{ |subgraph|
    @graph.isolated_clique?(subgraph)}
  
  pseudo = @graph.local_maximum{ |subgraph|
    @graph.pseudo_clique_density?(subgraph)}

  ipc = @graph.local_maximum{ |subgraph|
    @graph.isolated_pserudo_clique_density?(subgraph)}
  
  @viz = GraphViz.new(:G, :type => :graph, :ratio => "compress")
  @nodes = Hash.new #symbol => GraphViz::Node

  #draw nodes with lab subgraph
  def draw_nodes(group, label)
    @viz.subgraph{ |c|
      c[:rank => 'same']
      group.each do |n|
        color = 'white'
        color = 'lightgray' if @graph.nodes_from(n).size == 0
        @nodes[n] = @viz.add_node(n.to_s,
          :style => "filled", :fillcolor => color)
      end
    }
  end

  draw_nodes(hci, "lab #1")
  draw_nodes(net, "lab #2")

  cliques = [cliques, pseudo, isolated, ipc]
  colors = ["green", "blue", "red", "purple"]
  clique_names = %w[cliques pseudo isolated ipc]

#  @viz.subgraph{ |sub|
#    sub[:rank => "same"]
#    sub[:root => "true"]
    cliques.zip(colors, clique_names).each do |pair|
      pair[0].each_with_index do |c, idx|
        root = @viz.add_node("#{pair[2]+idx.to_s}",
          :shape => "diamond",
          :style => "filled",
          :fillcolor => pair[1])
        root[:root] = true
        c.each do |n|
          @viz.add_edge(root, @nodes[n],
            :color => pair[1],
            :fontcolor => pair[1])
        end
      end
    end
#  }

  print @viz.output(:canon => String)
  output_name = File.basename(path, ".*")
  @viz.output(:use => :dot, :png => "./"+output_name+".png")
  @viz.output(:use => :circo, :png => "./"+output_name+"_c.png")
  @viz.output(:use => :twopi, :png => "./"+output_name+"_t.png")
end

