require 'rake'
require 'rspec/core/rake_task'

desc "Default: run specs."
task :default => :spec

desc "Run specs"
RSpec::Core::RakeTask.new do |t|
  t.pattern = "./spec/**/*_spec.rb"
end

desc "Generate code coverage"
RSpec::Core::RakeTask.new(:coverage) do |t|
  t.pattern = "./spec/**/*_spec.rb"
  t.rcov = true
  t.rcov_opts = ['--exclude', 'spec']
end

#task :spec do
#  Spec::Rake::SpecTask.new do |t|
#    t.spec_files = FileList['spec/**/*_spec.rb']
#    t.spec_opts = ['--colour', '--format specdoc', '--loadby mtime', '--reverse', '--backtrace']
#  end
#end

