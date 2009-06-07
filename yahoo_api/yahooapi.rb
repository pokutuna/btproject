require 'open-uri'
require 'rexml/document'

$LOAD_PATH.unshift(File.dirname(__FILE__))
require 'api/api_config'
require 'api/dependency'

class YahooAPI
	include APIConfig

	def initialize(appid, proxy={})
		@@appid = appid
		@@proxy = proxy
		true
	end
	
	class UninitializedError < StandardError; end

end




