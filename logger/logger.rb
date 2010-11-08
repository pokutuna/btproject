#!/usr/bin/ruby
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
    puts_alert(:initializing)
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

    $meta_logger.debug('load virsion')
    version = YAML.load_file(@LOGGER_DIR+'/version.yaml')
    @version = version['version']
  end

  def daily_update
    $meta_logger.info('daily update')
    @today = Date.today.strftime("%Y%m%d")
    dir = FileUtils.mkdir_p(@LOG_DIR + '/' + @today).first
    @bda_file.close unless @bda_file == nil
    @wifi_file.close unless @wifi_file == nil

    output_device_info(dir)

    if @config['bt_scan'] then
      @bda_file = File.open(dir+'/bda'+@today+'.tsv','a')
      puts_log(@bda_file, "[LOGGER_VERSION]#{@version}")
      puts_log(@bda_file, "[LOGGER_BDA]#{@bda}")
    end
    
    if @config['wifi_scan'] then
      @wifi_file = File.open(dir+'/wifi'+@today+'.tsv','a')
      puts_log(@wifi_file, "[LOGGER_VERSION]#{@version}")
    end
  end

  def output_device_info(dir)
    File.open(dir+'/device_info.txt','a'){ |file|
      file.puts '--- bluetooth device information ---'
      file.puts `hciconfig #{@config['bt_dev']} -a`
      file.puts '--- wifi device information ---'
      file.puts `ifconfig #{@config['wifi_dev']}`
      file.puts '--- end ---'
    }
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
    data = Kconv.kconv(`iwlist #{@config['wifi_dev']} scan 2>&1`, Kconv::UTF8)
    if data.include?("Interface doesn't support scanning.") then
      `return_one(){ return 1; }; return_one`
      return
    end
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

  def puts_alert(type=nil)
    str = case type
          when :bt_scan
            "Bluetooth Scanning Error, BT device enabled?\nPlease check BT device or restart this logger."
          when :wifi_scan
            "Wifi Scanning Error, WiFi device enabled?\nPlease check WiFi device or restart this logger."
          when :initializing
            "Error Occured in Initializing."
          else
            "Unknown Error Occurred, Please check logger & devices."
          end
    puts str
    $meta_logger.error("puts alert message: #{str}")
  end

  def sleep_interval
    sleep (@config['scan_interval'] + rand(11) - 5)
  end

  def start
    loop do
      if @config['bt_scan'] then
        btdata = bluetooth_scan
        if $? == 0 then 
          puts_log(@bda_file, btdata['info'])
          puts_log(@bda_file, btdata['log'])
        else
          puts_alert(:bt_scan)
        end
      end

      if @config['wifi_scan'] then
        wfdata = wifi_scan
        if $? == 0 then
          puts_log(@wifi_file, wfdata['info'])
          puts_log(@wifi_file, wfdata['log'])
        else
          puts_alert(:wifi_scan)
        end
      end

      daily_update if @today != Date.today.strftime("%Y%m%d")
      sleep_interval
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
  
