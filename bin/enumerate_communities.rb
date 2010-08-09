$LOAD_PATH.unshift(File.dirname(__FILE__)+'/../lib')

require 'graph_parser'
require 'graph_analyzer'

Dir.glob('./net/*.txt').each do |path|
  graph = AdjacencyMatrix.open(path)
  p path
  puts '---local_maximum_cliques---'
  p graph.local_maximum_cliques
  puts '---isolated_clique?--'
  p graph.local_maximum{ |subgraph| graph.isolated_clique?(subgraph)}
  puts '---pseudo_clique? AVERAGE=0.9---'
  p graph.local_maximum{ |subgraph| graph.pseudo_clique?(subgraph,0.9)}
  puts '---isolated_pserudo_clique AVERAGE=0.9---'
  p graph.local_maximum{ |subgraph| graph.isolated_pserudo_clique?(subgraph,0.9)}
  puts '---pseudo_clique? AVERAGE=0.8---'
  p graph.local_maximum{ |subgraph| graph.pseudo_clique?(subgraph,0.8)}
  puts '---isolated_pserudo_clique AVERAGE=0.8---'
  p graph.local_maximum{ |subgraph| graph.isolated_pserudo_clique?(subgraph, 0.8)}
  puts '---pseudo_clique? AVERAGE=0.7---'
  p graph.local_maximum{ |subgraph| graph.pseudo_clique?(subgraph,0.7)}
  puts '---isolated_pserudo_clique AVERAGE=0.7---'
  p graph.local_maximum{ |subgraph| graph.isolated_pserudo_clique?(subgraph,0.7)}

  puts "\n\n\n"
end




















