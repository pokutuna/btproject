files = [
  'log_all.txt',
  'sep2_090000_103000.txt',
  'sep2_103000_123000.txt',
  'sep2_123000_150000.txt',
  'sep2_150000_163000.txt',
  'sep2_163000_180000.txt'
]


files.each do |f|
  `dot -Tpng #{f} -o #{File.basename(f,'.txt')}.png`
  `circo -Tpng #{f} -o #{File.basename(f,'.txt')}_c.png`
end
