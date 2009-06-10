require 'rubygems'
require 'active_record'

class BaseDB < ActiveRecord::Base; end
class TwitterPostsDB < BaseDB; end

TwitterPostsDB.default_timezone = :utc
twitter_posts_logger = TwitterPostsDB.logger = Logger.new(STDOUT)

def connect_TwitterPostsDB
	TwitterPostsDB.establish_connection(
		:adapter => 'mysql', :database => 'twitter_posts', :username => 'root', :encoding => 'utf8')
	true
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
