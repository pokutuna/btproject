#!/usr/bin/ruby -Ku

require 'rubygems'
require 'active_record'

class BaseDB < ActiveRecord::Base; end
class TwitterPostsDB < BaseDB; end

TwitterPostsDB.default_timezone = :utc


def connect_TwitterPostsDB
	TwitterPostsDB.establish_connection(
		:adapter => 'mysql', :database => 'twitter_posts', :username => 'root', :encoding => 'utf8')
	true
end

def connect_TwitterPostsDB_with_log
	twitter_posts_logger = TwitterPostsDB.logger = Logger.new(STDOUT)
	connect_TwitterPostsDB
end

class Post < TwitterPostsDB
	set_table_name 'posts'
	def to_s
		return "#{username}: #{message} #{time} #{uri}"
	end
end

class Update < TwitterPostsDB
	set_table_name 'updates'
end



#Migration
if $0 == __FILE__ then
	
	logger = ActiveRecord::Base.logger = Logger.new(STDOUT)

	ActiveRecord::Base.establish_connection(
		:adapter => 'mysql', :database => 'twitter_posts', :username => 'root', :encoding => 'utf8')

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
