# -*- coding: utf-8 -*-

raise ArgumentError unless  ARGV.size >= 2

def prompt(message)
  puts "#{message} [Y/n]"
  answer = STDIN.gets.chomp
  answer == 'Y' ? true : false
end

root_dir = File.join(ARGV.shift, "**/")
patterns = ARGV

patterns.each do |pat|
  paths = Dir.glob(root_dir + pat).to_a
  puts paths
  if prompt "delete them? (#{paths.size} files)" then
    paths.each{ |p| File.delete p}
  end
end
