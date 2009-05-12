def to_rpn(str)
	op = ['(',')','+','-','*','/','%']
	buffer = Array.new
	rev_pn = Array.new
	fomula = str.scan(/([\-]{0,1}\d+|[^\s])/).flatten!

	while c = fomula.shift do
		if op.include?(c) then
			if c == ')' then
				while '(' != c = buffer.pop do
					break if c == nil
					rev_pn.push(c)
				end
			elsif c == '(' then
				buffer.push(c)
			else
				if buffer.empty? or 
					 	op.index(buffer.last) <= op.index(c) then
					buffer.push(c)
				else
					rev_pn.push(buffer.pop)
					redo
				end
			end
		else
			rev_pn.push(c)
		end
	end
	rev_pn.push(c) while c = buffer.pop
	return rev_pn
end

def calc_rpn(rpn)
  op = ['+','-','*','/']
	buffer = Array.new
	while c = rpn.shift do
		if op.include?(c) then
			y = buffer.pop
			x = buffer.pop
			buffer.push(eval("x.to_i #{c} y.to_i"))
		else
			buffer.push(c)
		end
	end
	return buffer.to_s
end


['(-1 + 2) * (3 + 4)',
	'-1 + (-2) * 3 * 2 + 4',
	'(1 + 2) - (12 - 5 / ( 3 + 4))',
	'(23 - 7) /( (18 - 11) *9 )'
].each do |i|
	puts i + ' = ' + eval(i).to_s
	rpn = to_rpn(i)
	puts rpn.join(' ') + ' = ' + calc_rpn(rpn)
	puts '----'
end
