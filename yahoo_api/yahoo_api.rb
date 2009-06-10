require 'yaml'
require 'open-uri'
require 'rexml/document'

$LOAD_PATH.unshift(File.dirname(__FILE__))
require 'api/api_config'
require 'api/dependency'

module YahooAPI
	include APIConfig

	def set(appid=nil, proxy={})
		case appid
		when NilClass
			yaml = YAML.load_file(File.dirname(__FILE__)+'/conf.yaml')
			@@appid = yaml['appid']
			@@proxy = yaml['proxy'] || {}
		when String
			@@appid = appid
			@@proxy = proxy
		when Hash
			@@appid = appid['appid'] || appid[:appid]
			@@proxy = appid['proxy'] || appid[:proxy] || {}
		end
		true
	end

	module_function :set
	
	class UninitializedError < StandardError; end
end




