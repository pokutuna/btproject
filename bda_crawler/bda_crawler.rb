# -*- coding: utf-8 -*-

require 'date'
require 'monitor'

WAIT_TIME = 10

class BDALogger
  attr_accessor :today, :logfile

  def initialize
    @today = Date.today.strftime("%Y%m%d")
    @logfile = File.open(@today+'.txt', 'a')
    @logfile.sync = true
    @mutex = Monitor.new
  end
  
  def inquiry
    `hcitool scan`.each_line do |l|
      data = l.scan(/((?:[\dA-F]{2}:){5}[\dA-F]{2})\t(.+)/).flatten
      if data != [] then
        str = Time.now.strftime('%Y/%m/%d %X')+"\t"+data.join("\t")
        @mutex.synchronize{
          unless @logfile.closed? then
            @logfile.puts str
          else
            raise Error, "Log file #{@logfile.path} is closed."
          end
        }
        puts str
      end
    end
  end

  def refresh_date
    day = Date.today.strftime("%Y%m%d")
    if @today != day then
      @today = day
      close
      @logfile = File.open(@today+'.txt', 'a')
      @logfile.sync = true
      true
    else
      false
    end
  end

  def close
    @mutex.synchronize{ @logfile.close}
  end
end

logger = BDALogger.new

Thread.start{
  loop do
    Thread.start{ logger.inquiry}
    sleep WAIT_TIME
  end
}

gets
logger.close
