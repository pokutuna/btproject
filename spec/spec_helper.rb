# -*- coding: utf-8 -*-

$LOAD_PATH.unshift(File.dirname(__FILE__) + '/../lib')
require 'rspec'

Rspec.configure do |c|
  c.mock_with :rspec
end



