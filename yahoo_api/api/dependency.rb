class YahooAPI

	class DependencyAnalyzer
		include APIConfig
		
		RequestURL = 'http://jlp.yahooapis.jp/DAService/V1/parse'

		def self.parse(text)
			raise UnintializedError if @@appid == nil
			param = "?appid=#{@@appid}&sentence=#{URI.encode(text)}"
			open(RequestURL+param, @@proxy){ |f|
				return Result.new(f.read)
			}
		end

		class DepArray < Array
			def to_s
				return self.join(',')
			end
		end

		class Result
			@chunks
			@morphems
			@xml
			attr_accessor :chunks, :morphems, :xml
			
			def initialize(xml)
				@chunks = DepArray.new
				@morphems = DepArray.new
				@xml = REXML::Document.new(xml)
				@xml.elements.each('//Chunk') do |c|
					chunk = Chunk.new(c)
					@chunks.push(chunk)
					@morphems.push(chunk.morphems)
				end
				@morphems.flatten!

				@chunks.each do |c|
					unless c.depend == -1
						c.depend = @chunks[c.depend]
					else
						c.depend = Chunk.new(nil)
					end
				end
			end

			def chunk(arg)
				case arg
				when Integer
					return @chunks[arg]
				when Range
					return @chunks[arg]
				when Symbol
					if arg == :head || arg == :first
						return @chunks[0]
					elsif arg == :tail || arg == :last
						return @chunks[@chunks.size-1]
					end
				end
			end

		end
		
		class Chunk
			@id
			@depend
			@morphems
			attr_accessor :id, :depend, :morphems
			
			def initialize(chunk = nil)
				unless chunk == nil
					@morphems = DepArray.new
					@id = chunk.elements['Id'].text.to_i
					@depend = chunk.elements['Dependency'].text.to_i
					chunk.elements.each('MorphemList/Morphem') do |m|
						@morphems.push(Morphem.new(m))
					end
				else
					@morphems = DepArray.new
					@id = -1
					@depend = ""
				end
			end

			def nil?
				if self == nil || @id == -1 then
					return true
				else
					return false
				end
			end

			def inspect
				return @morphems.map{ |m| m.inspect}
			end

			def to_s
				return self.text
			end

			def text
				return @morphems.map{ |m| m.to_s}.join('')
			end

			def morphem(arg)
				case arg
				when Integer
					return @morphems[arg]
				when Range
					return @morphems[arg]
				when Symbol
					if arg == :head || arg == :first
						return @morphems[0]
					elsif arg == :tail || arg == :last
						return @morphems[@morphems.size-1]
					end
				end
			end
			
		end

		class Morphem
			@surface
			@reading
			@baseform
			@pos
			@feature
			attr_accessor :surface, :reading, :baseform, :pos, :feature

			def initialize(morphem)
				@surface = morphem.elements['Surface'].text
				@reading = morphem.elements['Reading'].text
				@baseform = morphem.elements['Baseform'].text
				@pos = morphem.elements['POS'].text
				@feature = morphem.elements['Feature'].text.split(',')
			end

			def inspect
				return @surface
			end

			def to_s
				return @surface
			end
		end
	end

	DA = DependencyAnalyzer
end
