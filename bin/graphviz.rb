

a = Dir.glob(File.expand_path(ARGV[0]))
a.each do |f|
  puts f
  Dir.chdir(File.dirname(f))
  `dot -Tpng #{f} -o #{File.basename(f,'.txt')}.png`
  `circo -Tpng #{f} -o #{File.basename(f,'.txt')}_c.png`
end
