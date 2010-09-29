#!/usr/bin/ruby

require 'fileutils'
require 'logger'

$script_path = File.expand_path(File.dirname(__FILE__))
$meta_logger = Logger.new($script_path+"/loggerlog.txt")

$meta_logger.info('try seinding log')

$meta_logger.debug('check rsync command')
raise 'system command not found' unless
  system('which rsync > /dev/null') &&
  system('which ssh > /dev/null')

$meta_logger.info('copy loggerlog')
Dir.glob($script_path + "/logdata/*"){ |path|
  if File.directory?(path) then
    FileUtils.cp($script_path+"/loggerlog.txt", path)
  end
}



system("rsync -avz --stats -e \"ssh -i #{$script_path}/key\" #{$script_path + '/logdata/'} tokuami@ns.hcilab.jp:~/human_network")
