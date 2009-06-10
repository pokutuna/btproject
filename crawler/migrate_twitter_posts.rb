#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

def create_tables
	ActiveRecord::Migration.create_table(:posts){ |t|
		t.string :username, :null => false
		t.string :message, :null => false
		t.string :url, :null => false
		t.time :time, :null => false
	}

	ActiveRecord::Migration.create_table(:updates){ |t|
		t.time :time, :null =>false
		t.string :action
	}
end

if $0 == __FILE__ then
	ActiveRecord::Base.default_timezone = :utc
	logger = ActiveRecord::Base.logger = Logger.new(STDOUT)

	ActiveRecord::Base.establish_connection(
		:adapter => 'mysql', :database => 'twitter_posts', :username => 'root', :encoding => 'utf8')
	create_tables
end







