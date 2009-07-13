#!/usr/bin/ruby -Ku

class Array
	def choice(size=1)
		return self[rand(self.size)] if size == 1
		indexes = []
		while indexes.size <= size
			indexes.push rand(self.size)
			indexes.uniq!
		end
		return indexes.inject([]){ |r,i| r.push self[i]}
	end
end

class GeneticKaomoji
	CATEGORY = [:naku, :otikomu, :tereru, :warau, :yorokobu,
		:bikkuri, :okoru, :kyofu, :bb]

	@all
	@categoric_map
	@categoric_chars
	@charpos_all
	@current_gen
	@next_gen
	@size
	
	attr_accessor :all, :categoric_map, :categoric_chars,	:charpos_all, :current_gen, :next_gen, :size
	
	def initialize(all)
		@all = all
		@categoric_map = Hash.new
		@categoric_chars = Hash.new
		CATEGORY.each do |categ|
			@categoric_map[categ] =
				all.find_all{ |i| i.category.to_sym == categ}
			
			@categoric_chars[categ] = Hash.new
			str = @categoric_map[categ].map{ |i| i.kao.split(//)}.flatten
			str.uniq.each do |i|
				@categoric_chars[categ][i] =
					str.find_all{ |j| j == i}.size.to_f / str.uniq.size
			end
		end
		@charpos_all = calc_charpos(@all)
	end
	
	def calc_charpos(kaomoji)
		dest = Hash.new
		chars = kaomoji.map{ |i| i.kao.split(//)}.flatten.uniq

		kaomoji.map{ |i| i.kao.split(//)}.each do |face|
			face.each_with_index do |item,idx|
				if dest[item] == nil then
					dest[item] = [idx / face.size.to_f]
				else
					dest[item].push(idx/face.size.to_f)
				end
			end
		end

		dest.each_key do |k|
			dest[k] =
				dest[k].inject(0){ |r,i| r+i} / dest[k].size.to_f
		end
		
		return dest
	end

	def start(start_gen, size=5)
		@current_gen = start_gen
		@next_gen = []
		@size = size

		loop do
		@current_gen.each do |face|
			face.fitness = calc_fitness(face)
			face.category = guess_category(face) if face.category == nil
		end

		@current_gen.each do |face|
			@next_gen.push(calc_cross(face, @all.choice)) if rand < 0.3
			@next_gen.push(calc_mutation(face)) if rand < 0.5
			@next_gen.push(calc_improve_2(face)) if rand < 0.5
		end

		@next_gen.each do |i|
			i.fitness = calc_fitness(i)
		end
		print_status
		
		if @next_gen.size > 5
			@next_gen = @next_gen.sort_by{ |i| i.fitness}[0...5]
		end
			@current_gen = @next_gen
			@next_gen = []
			sleep 2
		end
		
	end

	def print_status
		puts 'cuttent generation' + ' finess:' + sprintf("%.3f",(@current_gen.inject(0){ |r,i| r+i.fitness} / @current_gen.size.to_f).to_s)
		@current_gen.each do |face|
			puts '  '+ face.kao+'  ('+face.category.to_s+')'+sprintf("%.3f",face.fitness.to_s)
		end
		
		puts ''
		puts 'next generation' + ' finess:' + sprintf("%.3f",(@next_gen.inject(0){ |r,i| r+i.fitness} / @next_gen.size.to_f).to_s)
		@next_gen.each do |face|
			puts '  '+ face.kao+'  ('+face.category.to_s+')'+sprintf("%.3f",face.fitness.to_s)
		end
	end
	
	def calc_fitness(face, source = nil)
		if source == nil
			source = @charpos_all
		else
			source = calc_charpos(source)
		end

		diff = 0.0
		face.kao.split(//).each_with_index do |item, idx|
			diff += (source[item] - idx / face.kao.split(//).size.to_f).abs
		end
		return diff
	end

	def guess_category(face, source = nil)
		source = @categoric_chars if source == nil
		chars = face.kao.split(//)
		result = Hash.new
		CATEGORY.each do |categ|
			result[categ] = 0
			chars.each do |c|
				result[categ] += source[categ][c] if source[categ].has_key?(c)
			end
		end
		return result.to_a.max_by{ |i| i[1]}[0]
	end
	
	def calc_cross(face1, face2)
		dest = ''
		f1 = face1.kao.split(//).each
		f2 = face2.kao.split(//).each
		itr= [f1,f2]
		itr.reverse! if rand > 0.5
		last_char = itr[0].next
		dest << last_char
		
		itr[0].zip(itr[1]) do |a,b|
			dist_a, dist_b = -1, -1
			dist_a = @charpos_all[a] - @charpos_all[last_char] unless a == nil
			dist_b = @charpos_all[b] - @charpos_all[last_char] unless b == nil
			if dist_a <= dist_b && dist_a > 0
				dest << a
				last_char = a
			elsif dist_b <= dist_a && dist_b > 0
				dest << b
				last_char = b
			end
		end

		kaomoji = Kaomoji.new(dest)
		kaomoji.category = guess_category(kaomoji)
		return kaomoji
	end

	def calc_mutation(face, time = 2)
		str = face.kao.split(//)
		time.times do
			pos = rand
			char = @charpos_all.to_a.sort_by{ |i| i[1]}.reverse.find{ |i| pos > i[1]}
			next if char == nil
			str[pos*str.size.to_i,0] = char[0]
		end
		
		kaomoji = Kaomoji.new(str.join)
		return Kaomoji.new(kaomoji.kao, guess_category(kaomoji))
	end

	def calc_improve_2(face)
		return calc_improve(calc_improve(face))
	end
	
	def calc_improve(face)
		ary = @categoric_chars[face.category].to_a.sort_by{ |i| i[1]}.reverse
		str = face.kao.split(//)
		char = ary.find{ |i| !(str.include?(i[0]))}
		return face if char == nil
		str[@charpos_all[char[0]].to_i,0] = char[0]
		kaomoji = Kaomoji.new(str.join)
		return Kaomoji.new(kaomoji.kao, guess_category(kaomoji))
	end
end

class Kaomoji
	@kao
	@category
	@fitness
	attr_accessor :kao, :category, :fitness
	
	def initialize(kaomoji, category = nil, fitness = nil)
		@kao = kaomoji
		@category = category
		@fitness = fitness
	end
end

all = []
GeneticKaomoji::CATEGORY.each do |name|
	text = File.open(name.to_s+'.txt').read
	text.each_line do |line|
		kao = line.chomp
		all.push Kaomoji.new(kao, name) unless line == ''
	end
end

gk = GeneticKaomoji.new(all)

if $0 == __FILE__ then

	puts 'kaomoji :'+gk.all.size.to_s
	puts 'ave size:'+(gk.all.map{ |i| i.kao}.inject(0){ |r,i| r+i.split(//).size} / gk.all.size.to_f).to_s
	
	puts 'ave fitness:' + (gk.all.inject(0){ |r,i| r+gk.calc_fitness(i)} / gk.all.size.to_f).to_s
	puts 'kigou   :'+all.map{ |i| i.kao.split(//)}.flatten.uniq.size.to_s
	puts 'category:'
	GeneticKaomoji::CATEGORY.each do |categ|
		puts '  '+categ.to_s+':'+gk.categoric_map[categ].size.to_s
	end

	puts 'prob occur:'
	GeneticKaomoji::CATEGORY.each do |categ|
		puts '  '+categ.to_s+':'+	gk.categoric_chars[categ].to_a.sort_by{ |i| i[1]}.reverse[0..5].map{ |i| i[0]}.join(' ')
	end

	puts 'char pos:'
	gk.charpos_all.to_a.sort_by{ |i| i[1]}.each do |ary|
		print ary[0] + ' ' + sprintf("%.2f",ary[1])+', '
	end
	
	#test
=begin
	charset = gk.charpos_all.to_a.map{ |i| i[0]}
	ary = []
	5.times do
		ary.push(Kaomoji.new(charset.choice(5).join))
	end
	
	gk.start(ary)
=end

	gk.all.each do |i|
		puts i.kao + ':'+ gk.calc_fitness(i).to_s
	end
end

#ランダムな文字列 -> 全然顔文字じゃない
#顔文字 -> とても顔文字
# 違いは何か？　記号の並び、視覚的な問題
# 記号の並び、位置を数値的に表現する

#既にある顔文字 = 生存競争を生き抜いてきた！
#  それに近い配置だと生き残りやすい

#顔はおおむね線形に伸び縮みする → 記号の出現場所を顔文字の長さの割合で

#顔文字 : ただの顔じゃなくて、感情を表現している
#→感情の要素を強化していくと顔文字らしくなるかもしれない

#今回扱う顔文字 (URL)からのシンプル顔文字
#これをまず手動で間引く セリフなど顔以外のパーツが多いもの
#さらに珍しい記号を使ったものを削除
#(それぞれの記号が、何個の顔文字で用いられているかを計算
# 3個以上(ヒューリスティックス！) の顔文字で用いられていない記号、それを含む顔文字を削除)


