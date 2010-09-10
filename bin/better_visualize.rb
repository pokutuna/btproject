$LOAD_PATH.unshift(File.dirname(__FILE__)+'/../lib')

require 'graph_parser'
require 'graph_analyzer'
require 'graphviz'

sample = ['sep3_1030_1100', 'sep3_1100_1130', 'sep3_1230_1300']
hci = ['okuura','ushikoshi','kono','tokuami'].map(&:to_sym)
net = ['EEEPC06','EEEPC13','EEEPC16','eeepc04','eeepc08','eeepc11','eeepc12','eeepc15','eeepc18'].map(&:to_sym)

Dir.glob('./net/*.txt').each do |path|
#sample.map{ |s| "./net/#{s}.txt"}.each do |path|
  @graph = AdjacencyMatrix.open(path, :graph)
  p path
  
  cliques = @graph.local_maximum_cliques
 
  isolated = @graph.local_maximum{ |subgraph|
    @graph.isolated_clique?(subgraph)}
  
  pseudo = @graph.local_maximum{ |subgraph|
    @graph.pseudo_clique_density?(subgraph)}

  ipc = @graph.local_maximum{ |subgraph|
    @graph.isolated_pserudo_clique_density?(subgraph)}
  
  #create graphviz
  @viz = GraphViz.new(:G, :type => :graph, :compound => true, :ratio => :compress)
  @nodes = Hash.new #symbol => GraphViz::Node

  #draw nodes with lab subgraph
  def draw_nodes(group, label)
    c = @viz.add_graph(label.to_sym)
    c[:label] = label
    c[:style] = "filled"
    c[:color] = "lightgrey"
    c[:compound] = true
    group.each do |n|
      color = 'white'
      color = 'lightgray' if @graph.nodes_from(n).size == 0
      @nodes[n] = c.add_node(n.to_s,
        :style => "filled", :fillcolor => color)
    end

#    @viz.add_graph(label, :label => label, :style => 'filled', :rank => 'same'){ |c|
#      group.each do |n|
#        color = 'white'
#        color = 'lightgray' if @graph.nodes_from(n).size == 0
#        @nodes[n] = c.add_node(n.to_s,
#          :style => "filled", :fillcolor => color)
#      end
#    }
  end

  draw_nodes(hci, "lab #1")
  draw_nodes(net, "lab #2")

  cliques = [cliques, pseudo, isolated, ipc]
  colors = ["green", "blue", "red", "purple"]
  clique_names = %w[cliques pseudo isolated ipc]

  #  @viz.marks{ |sub|
  #    sub[:rank => "same"]
  #    sub[:root => "true"]
  cliques.zip(colors, clique_names).each do |pair|
    next if pair[0] == nil
    pair[0].each_with_index do |c, idx|
      mark = @viz.add_node("#{pair[2]+idx.to_s}",
        :shape => "diamond",
        :style => "filled",
        :fillcolor => pair[1])
      mark[:root] = true
      c.each do |n|
        @viz.add_edge(mark, @nodes[n],
          :color => pair[1],
          :fontcolor => pair[1])
      end
    end
  end
  #  }

  print @viz.output(:canon => String)
  output_name = File.basename(path, ".*")
  @viz.output(:use => :dot, :png => "./clique_graph/"+output_name+".png")
  @viz.output(:use => :neato, :png => "./clique_graph/"+output_name+"_n.png")
  @viz.output(:use => :fdp, :png => "./clique_graph/"+output_name+"_f.png")
  @viz.output(:use => :circo, :png => "./clique_graph/"+output_name+"_c.png")
  @viz.output(:use => :twopi, :png => "./clique_graph/"+output_name+"_t.png")
end

