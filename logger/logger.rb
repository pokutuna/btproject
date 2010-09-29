# -*- coding: utf-8 -*-

require 'date'
require 'yaml'
require 'fileutils'
require 'kconv'
require 'logger'

$script_path = File.expand_path(File.dirname(__FILE__))
$meta_logger = Logger.new($script_path+"/loggerlog.txt")

class HumanNetworkLogger
  def initialize
    @LOGGER_DIR = $script_path
    has_command?
    load_config
    daily_update
  rescue => e
    $meta_logger.fatal('initializing error')
    raise e
  end

  def has_command?
    $meta_logger.debug('check command?')
    raise 'system command not found' unless
      system('which hciconfig > /dev/null') &&
      system('which hcitool > /dev/null') &&
      system('which iwlist > /dev/null') &&
      system('which ifconfig > /dev/null')
    return true
  end

  def load_config
    $meta_logger.debug('load config')
    @config = YAML.load_file(@LOGGER_DIR+"/config.yaml")
    @dev_name = `hciconfig #{@config['bt_dev']} name`.scan(/Name:\s'([^']*)'/).flatten.first
    if @dev_name == ''
      @dev_name = `echo $USER`.chomp
      $meta_logger.warn('blank bluetooth device name')
    end
    @bda = `hciconfig #{@config['bt_dev']} name}`.scan(/BD\sAddress:\s([\w:]*)/).flatten.first
    @LOG_DIR = @LOGGER_DIR + '/logdata/' + @dev_name + '/' + @bda
  end

  def daily_update
    $meta_logger.info('daily update')
    @today = Date.today.strftime("%Y%m%d")
    dir = FileUtils.mkdir_p(@LOG_DIR + '/' + @today).first
    @bda_file.close unless @bda_file == nil
    @wifi_file.close unless @wifi_file == nil
    
    if @config['bt_scan'] then
      @bda_file = File.open(dir+'/bda'+@today+'.tsv','a')
      puts_log(@bda_file, "[LOGGER_BDA]#{@bda}")
    end
    
    if @config['wifi_scan'] then
      @wifi_file = File.open(dir+'/wifi'+@today+'.tsv','a')
    end
  end

  def bluetooth_scan
    now = Time.now.strftime('%Y/%m/%d %X')
    info = '[BT_SCAN]' + now
    data = Kconv.kconv(`hcitool -i #{@config['bt_dev']} scan --flush`, Kconv::UTF8)
    pairs = data.scan(/([\w:]+)\t(.*)\n/)
    log = ''
    pairs.each do |p|
      next if p.size != 2
      log += [now, p[1], p[0]].join("\t") + "\n"
    end
    return {'info'=>info, 'log'=>log }
  end

  def wifi_scan
    now = Time.now.strftime('%Y/%m/%d %X')
    info = '[WIFI_SCAN]' + now
    data = Kconv.kconv(`iwlist #{@config['wifi_dev']} scan 2>/dev/null`, Kconv::UTF8)
    address = data.scan(/Address:\s([\w:]*)/).flatten
    essid = data.scan(/ESSID:"([^"]*)"/).flatten
    signal = data.scan(/Signal level=([-\d]*?)\sdBm/).flatten
    log = ''
    address.zip(essid, signal).each do |ary|
      log += [now, ary[1], ary[0], ary[2]].join("\t") + "\n"
    end
    return {'info'=>info, 'log'=>log}
  end

  def puts_log(file, msg)
    puts msg
    file.puts msg
  end
  
  def start
    loop do
      if @config['bt_scan'] then
        btdata = bluetooth_scan
        puts_log(@bda_file, btdata['info'])
        puts_log(@bda_file, btdata['log'])
      end

      if @config['wf_scan'] then
        wfdata = wifi_scan
        puts_log(@wifi_file, wfdata['info'])
        puts_log(@wifi_file, wfdata['log'])
      end

      daily_update if @today != Date.today.strftime("%Y%m%d")
      sleep @config['scan_interval']
    end

  rescue => e
    @bda_file.close
    @wifi_file.close
    raise e
  end
end


begin
  HumanNetworkLogger.new.start
rescue => e
  $meta_logger.fatal(e.message)
  $meta_logger.fatal(e.backtrace.join("\n"))
end
  
