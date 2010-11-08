require 'rake/packagetask'
require 'yaml'

files = FileList.new do |f|
  f.include "*.rb"
  f.include "version.yaml"
  f.include 'update.sh'
  f.include "setup/**/*"
  f.exclude "**/*key"
  f.exclude "latest_logger"
  f.exclude "**/*~"
  f.exclude "**/*#"
  f.exclude "delete_annotation.rb"
end
task 'all' => ['repackage', 'upload']
task 'default' => 'package'
task 'withconfig' => ['addconfig', 'repackage']

task 'addconfig' do
  files.include 'config.yaml'
end


version = YAML.load_file('version.yaml')['version']
Rake::PackageTask.new('logger', version) do |p|
  p.package_dir = './pkg'
  p.package_files = files
  p.need_zip = true
end

Rake::PackageTask.new('logger', :noversion) do |p|
  p.package_dir = './pkg'
  p.package_files = files
  p.need_zip = true
end

task 'upload' do
  sh "scp ./pkg/*.zip tokuami@ns.hcilab.jp:~/public_html/"
end

