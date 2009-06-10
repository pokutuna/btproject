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
