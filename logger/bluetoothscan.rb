# -*- coding: utf-8 -*-

require 'date'

#envcheck
raise 'system command not found' unless
  system('which hciconfig > /dev/null') &&
  system('which hcitool > /dev/null')


#load setting
dev = "hci0"
bda = `hcitool dev`.scan(/#{dev}\t(([\w]{2}:){5}[\w]{2})/).flatten.first

#logging
def inquiry(file)
  puts "Device Inquiry - #{Time.now.strftime('%Y-%m-%d %X')}"
  `hcitool -i hci0 scan --flush`.each_line do |l|
    data = l.scan(/((?:[\dA-F]{2}:){5}[\dA-F]{2})\t(.+)/).flatten
    str = Time.now.strftime('%Y/%m/%d %X')+"\t"+data.join("\t")
    if data != [] then
      file.puts str
      puts "\t"+str
    end
  end
end

loop do
  today = Date.today.strftime("%Y%m%d")
  File.open(today+'.txt', 'a'){ |logfile|
    inquiry(logfile)
  } #ディレクトリをBDAに?
  sleep 10
  
end
