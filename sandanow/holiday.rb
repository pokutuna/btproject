#This script is from ruby-list 
#http://blade.nagaokaut.ac.jp/cgi-bin/scat.rb/ruby/ruby-list/38725
#Thanks to Beyond

class TCalendarHoliday
    # このデータ形式は次のようになっています。
    # 月名  日付  有効年  祝日名称
    # HM2 Happy Monday(2nd monday)
    # HM3 Happy Monday(3rd monday)
    DATA = %w[
        Jan 1       0         元旦
        Jan 15      -1999     成人の日
        Jan HM2     2000-     成人の日
        Feb 11      0         建国記念の日
        Mar SHUNBUN 0         春分の日
        Apr 29      0         緑の日
        May 3       0         憲法記念日
        May 4       1986-     国民の休日
        May 5       0         こどもの日
        Jul 20      1996-2002 海の日
        Jul HM3     2003-     海の日
        Sep 15      -2002     敬老の日
        Sep HM3     2003-     敬老の日
        Sep SHUBUN  0         秋分の日
        Oct 10      -1999     体育の日
        Oct HM2     2000-     体育の日
        Nov 3       0         文化の日
        Nov 23      0         勤労感謝の日
        Dec 23      1989-     天皇誕生日
    ]

    def initialize( year = 0, month = 0 )
        @holiday = {}
        month_name = Time.local( year, month, 1 ).strftime( '%b' )

        data = DATA
        while !data.empty?
            m, d, y, c, *data = data

            if y != '0'
                if y[0,1] == '-'
                    next if year > y[1,4].to_i
                elsif y[-1,1] == '-'
                    next if year < y[0,4].to_i
                elsif y[4,1] == '-'
                    next if year < y[0,4].to_i || year > y[5,4].to_i
                end
            end

            if month_name == m
                case d
                    when 'SHUNBUN'
                        d = syunbun( year )
                    when 'SHUBUN'
                        d = syubun( year )
                    when 'HM2'
                        d = nMonday( year, month, 2 )
                    when 'HM3'
                        d = nMonday( year, month, 3 )
                end
                @holiday[ d.to_i ] = true
            end
        end

        @substitute = {}
        ( 1..5 ).each { | n |
            d = nMonday( year, month, n )
            if @holiday[ d - 1 ]
                @substitute[ d ] = true
            end
        }
    end

    def holiday?( day )
        @holiday[ day ] || false
    end

    def substitute?( day )
        @substitute[ day ] || false
    end

    def nMonday( year, month, n )
        wday = Time.local( year, month, 1 ).wday
        case wday
            when 0..1
                7 * n - wday - 5
            when 2..6
                7 * n - wday + 2
            else
                nil
        end
    end

    #| From: hajima / crimson.gen.u-tokyo.ac.jp (Ryoichi Hajima)
    #| Newsgroups: fj.questions.misc
    #| Subject: Re: vernal/autumnal equinox
    #| Message-ID: <HAJIMA.94Jul13161542 / tanelorn.gen.u-tokyo.ac.jp>
    #| Date: 13 Jul 94 07:15:42 GMT
    #|
    #| 春分日　(31y+2213)/128-y/4+y/100    (1851年-1999年通用)
    #| 　　　　(31y+2089)/128-y/4+y/100    (2000年-2150年通用)
    #|
    #| 秋分日　(31y+2525)/128-y/4+y/100    (1851年-1999年通用)
    #| 　　　　(31y+2395)/128-y/4+y/100    (2000年-2150年通用)

    def syunbun(year)
        if year > 2150
            STDERR.print "over year's: #{year}\n"  #'
            exit 1
        end
        v = if year < 2000 then 2213 else 2089 end
        (31 * year + v)/128 - year/4 + year/100
    end

    def syubun(year)
        if year > 2150
            STDERR.print "over year's: #{year}\n" #'
            exit 1
        end
        v = if year < 2000 then 2525 else 2395 end
        (31 * year + v)/128 - year/4 + year/100
    end
end

if $0 == __FILE__
    c = TCalendarHoliday.new( 2003, 11 )

    (1..30).each { | day |
        puts "#{day} : #{c.holiday?( day )}, #{c.substitute?( day )}"
    }
end
