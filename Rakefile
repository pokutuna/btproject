require 'rake'
require 'spec/rake/spectask'

desc "Run all specs"
task :spec do
  Spec::Rake::SpecTask.new do |t|
    t.spec_files = FileList['spec/**/*_spec.rb']
    t.spec_opts = ['--colour', '--format specdoc', '--loadby mtime', '--reverse', '--backtrace']
  end
end

