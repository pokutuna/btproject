def to_rpn(str)
	op = ['(',')','+','-','*','/','%']
	buffer = Array.new
	rev_pn = Array.new
	fomula = str.scan(/(\d+|[^\s])/).flatten!

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


['(1 + 2) * (3 + 4)',
	'1 + 2 * 3 * 2 + 4',
	'(1 + 2) - (12 - 5 / ( 3 + 4))',
	'A+B*(C+D)+E',
	'(23 -7) /( (18 - 11) *9 )'
].each do |i|
	puts i
	puts to_rpn(i).join(' ')
	puts '----'
end
