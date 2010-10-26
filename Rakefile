require 'rake'
require 'rspec/core/rake_task'

desc "Default: run specs."
task :default => :spec

desc "Run specs"
RSpec::Core::RakeTask.new do |t|
  t.pattern = "./spec/**/*_spec.rb"
  t.rspec_opts = ['-c', '-f documentation', '-b','-r ./spec/spec_helper.rb']
end

