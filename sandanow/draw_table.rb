#!/usr/local/bin/ruby -Ku

require 'csv'

require File.dirname(__FILE__)+'/tags.rb'

def default
	puts CONTENT_TYPE
	puts HEADER
	h(1){ puts "バスなう"}
	p{
		puts "#{Time.now}"
		br
		puts "アクセスした時間から著しくインテリジェントに、"
		br
		puts "バスのダイヤを表示するウェッブサービスです。"
		br
		puts "要望苦情報告は僕または僕を知ってる人に連絡してください。"
		br
		puts '<ul>'
		puts '<li>土日祝日を判定してダイヤを選択</li>'
		puts '<li>次の3本と2時間以内に到着するバスを表示</li>'
		puts '</ul>'
	}
	a("busnow.cgi?mode=to"){ puts "大学からバスに乗り込む"}
=begin
		 small{
			a("busnow.cgi?mode=to&holiday=false"){ puts "(強制平日モード)"}
			a("busnow.cgi?mode=to&holiday=true"){ puts "(強制土日祝モード)"}
		}
=end
	br
	a("busnow.cgi?mode=from"){ puts "各地からバスに乗り込む"}
=begin
		 small{
			a("busnow.cgi?mode=from&holiday=false"){ puts "(強制平日モード)"}
			a("busnow.cgi?mode=from&holiday=true"){ puts "(強制土日祝モード)"}
		}
=end
	br
	p{
		puts "平成21年4月15日1版訂正版"
	}
	puts FOOTER
end

def from(h=false)
	begin
		if h != true
			h = ''
		else
			h = '_h'
		end

		from_shinsanda = CSV.open("from_shinsanda#{h}.csv",'r')
		from_sanda = CSV.open("from_sanda#{h}.csv",'r')
		from_uegahara = CSV.open('from_uegahara.csv','r')
		from_sannomiya = CSV.open("from_sannomiya#{h}.csv",'r')
		
		puts CONTENT_TYPE
		puts HEADER
		puts EXPLAIN
		h(3){ puts "新三田から"}
		draw_table(from_shinsanda)
		h(3){ puts "三ノ宮方面"}
		draw_table(from_sannomiya)
		h(3){ puts "上ヶ原から"}
		draw_table(from_uegahara)
		puts '(授業期間中の月~金,定期試験中の土のみ運行)'
		h(3){ puts "三田から"}
		draw_table(from_sanda)
		br
		puts FOOTER
		
	rescue => e
		#message
		p e
		p e.backtrace
	ensure
		from_shinsanda.close
		from_sanda.close
		from_uegahara.close
		from_sannomiya.close
	end
end

def to(h=false)
	begin
		if h != true
			h = ''
		else
			h = '_h'
		end
		
		to_shinsanda = CSV.open("to_shinsanda#{h}.csv",'r')
		to_sanda = CSV.open("to_sanda#{h}.csv",'r')
		to_uegahara = CSV.open('to_uegahara.csv','r')
		to_sannomiya = CSV.open("to_sannomiya#{h}.csv",'r')

		puts CONTENT_TYPE
		puts HEADER
		puts EXPLAIN
		h(3){ puts "新三田方面"}
		draw_table(to_shinsanda)
		h(3){ puts "三ノ宮方面"}
		draw_table(to_sannomiya)
		h(3){ puts "上ヶ原へ"}
		draw_table(to_uegahara)
		puts '(授業期間中の月~金,定期試験中の土のみ運行)'
		h(3){ puts "三田方面"}
		draw_table(to_sanda)
		br
		puts FOOTER
		
	rescue => e
		#message tukaenai
		p e
		p e.backtrace
	ensure
		to_shinsanda.close
		to_sanda.close
		to_uegahara.close
		to_sannomiya.close
	end
end

def draw_table(csv)
	now = Time.now
	size = 0
	table{
		tr{
			th_line = csv.shift
			size = th_line.size
			th_line.each do |title|
				th{ puts title}
			end
		}

		nxt2h = Array.new
		after = Array.new
		csv.each do |row|
			#Array.index(&block) is Ruby 1.9 backports!
			#i = row.index{ |v| v =~ /\d+:\d+/}
			i = row.each_with_index{ |e,idx| break(idx) if e=~/\d+:\d+/}
			arrive = Time.parse(row[i])
			diff = arrive - now
			if 0 <= diff && diff <= 7200
				nxt2h.push row
			end
			if arrive > now then
				after.push row
			end
		end

		ary = after[0..2] | nxt2h
		if ary == []
			tr{
				puts "<td align=\"center\" colspan=\"#{size}\">"
				puts "もうバス無い"
				puts "</td>"
			}
		else
			ary.each do |row|
				tr{
					row.each do |i|
						td{ puts i}
					end
				}
			end
		end
	}
end
