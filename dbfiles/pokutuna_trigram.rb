#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

class BaseDB < ActiveRecord::Base; end
class TrigramDB < BaseDB; end

TrigramDB.default_timezone = :utc


def connect_TrigramDB
	TrigramDB.establish_connection(
		:adapter => 'mysql', :database => 'pokutuna_trigram', :username => 'root', :encoding => 'utf8')
end

def connect_TrigramDB_with_log
	trigram_logger = TrigramDB.logger = Logger.new(STDOUT)
	connect_TrigramDB
end

class Trigram < TrigramDB
	set_table_name 'trigrams'
	def to_s
		return "#{w1},#{w2},#{w3}:#{count}"
	end
end

class OnRecord < TrigramDB
	set_table_name 'on_records'
end


#Migration
if $0 == __FILE__ then

	logger = ActiveRecord::Base.logger = Logger.new(STDOUT)
	
	ActiveRecord::Base.establish_connection(
		:adapter => 'mysql', :database => 'pokutuna_trigram', :username => 'root', :encoding => 'utf8')

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
