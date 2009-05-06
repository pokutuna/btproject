#!/usr/bin/ruby -Ku

class MMNSimulator
	def initialize(arrival_dist, desks, persons)
		@arrival_dist = arrival_dist
		@desks = desks
		@events = init_persons(persons)
		@people = events.clone
		@waiting = Array.new
		@result = String.new
	end

	def init_persons(persons)
		line = Array.new
		persons.times do
			diff = -@arrival_dist*Math::log(1.0-rand)
			if line.empty? then
				line.push(Event.new(:arrive, diff))
			else
				line.push(Event.new(:arrive, line.last.time+diff))
			end
		end
		return line
	end
	
	def process()
		until @events.empty? do
			step
		end
	end

	def step()
		e = @events.shift
		e.exec(self)
	end

	def find_desk(time)
		found = false
		@desks.each do |desk|
			unless desk.busy?(time) then
				found = desk
				break
			end
		end
		return found
	end

	def count_busy_desk(time)
		num = @desks.inject(0) { |res, itm|
			if itm.busy?(time) then
				res += 1
			else
				res
			end
		}
		return num
	end
	
	def add_event(event)
		@events.push(event).sort!{ |x,y| x.time <=> y.time}
	end

	def result()
		@people.each do |i|
			print "#{i.log[:wait_time]}, #{i.log[:line_size]}, #{i.log[:keinai]}\n"
		end
	end
	
	attr_accessor :desks, :arrival_dist, :events, :waiting
	
end

class Desk
	@@count = 0

	def initialize(service_dist)
		@number = @@count+1
		@@count+=1
		@service_dist = service_dist
		@working_while = 0.0
		true
	end

	def busy?(now)
		(@working_while <= now) ? false : true
	end

	def start_working(event)
		raise ArgumentError unless event.is_a? Event
		@working_while = -@service_dist*Math::log(1.0-rand)+event.time
		return Event.new(:open, @working_while)
	end
	
	attr_reader :number, :service_dist, :working_while
	
end

class Event

  def	initialize(event, time)
		raise ArgumentError unless event.is_a? Symbol
		@action = event
		@time = time
		@log = Hash.new
	end

	def exec(symlator)
		case @action
		when :arrive
			puts "#{@time}: person arrived"
			@log[:line_size] = symlator.waiting.size
			@log[:keinai] = symlator.count_busy_desk(@time) + symlator.waiting.size
			unless desk = symlator.find_desk(@time) then
				puts "  desk is full. waiting: #{symlator.waiting.size}"
				symlator.waiting.push(self)
			else
				puts "  desk #{desk.number} serves - ends #{desk.working_while}"
				new_event = desk.start_working(self)
				symlator.add_event(new_event)
				@log[:wait_time] = 0
			end
			
		when :open
			puts "#{@time}: window open"
			if head_person = symlator.waiting.shift then
				desk = symlator.find_desk(@time)
				puts "  desk #{desk.number} serves - ends #{desk.working_while}"
				puts "  person waited for #{@time - head_person.time}"
				event = desk.start_working(self)
				symlator.add_event(event)
				head_person.log[:wait_time] = @time - head_person.time
			else
				puts "  no people waiting"
			end

		else
			raise ArgumentError
		end
	end

	attr_reader :action, :time, :log
end


a = MMNSimulator.new(5.0, [Desk.new(4.0), Desk.new(4.0)], 30)
p a.events.map{ |x| x.time}
a.process
puts '---------------'
a.result


