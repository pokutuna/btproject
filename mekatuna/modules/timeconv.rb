module TimeConv

	require 'parsedate'
	
	def self.parse(str)
		ary = ParseDate.parsedate(str)
		return Time.utc(*ary[0..-3])
	end
	
end
