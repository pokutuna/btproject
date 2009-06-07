require 'open-uri'
require 'rexml/document'

$LOAD_PATH.unshift(File.dirname(__FILE__))
require 'api/dependency'

class YahooAPI
	include APIC
	class UninitializedError < StandardError; end

end

module APIConfig
	@@appid = nil
	@@proxy = nil
end




