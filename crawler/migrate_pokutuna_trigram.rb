#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

#require File.dirname(__FILE__)+'/model_trigram'

def create_tables
	ActiveRecord::Migration.create_table(:trigrams){ |t|
		t.string :w1, :null => false
		t.string :w2, :null => false
		t.string :w3, :null => false
		t.column :count, :int, :null => false
		t.boolean :head, :null => false
		t.boolean :tail, :null => false
	}

	ActiveRecord::Migration.create_table(:on_records){ |t|
		t.string :url, :null => false
	}
end

if $0 == __FILE__ then
	ActiveRecord::Base.default_timezone = :utc
	logger = ActiveRecord::Base.logger = Logger.new(STDOUT)
	
	ActiveRecord::Base.establish_connection(
		:adapter => 'mysql', :database => 'pokutuna_trigram', :username => 'root', :encoding => 'utf8')

	create_tables
end

