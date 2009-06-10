#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

require File.dirname(__FILE__)+'/../dbfiles/pokutuna_trigram.rb'

connect_TrigramDB

dest = ''

node = Trigram.find(:all, :conditions => ["head=?", true])
node = node[rand(node.size)]
dest << node.w1 + node.w2 + node.w3

50.times do
	nodes = Trigram.find(:all, :conditions => ["w1=? and w2=?", node.w2, node.w3])
	break if nodes == nil
	nodetable = Array.new
	nodes.each do |n|
		n.count.times{ nodetable.push(n)}
	end
	node = nodetable[rand(nodetable.size)]
	break if node == nil
	dest << node.w3
#	break if node.tail == true
end

p dest

