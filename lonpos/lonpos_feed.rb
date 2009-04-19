#!/usr/local/bin/ruby -Ku

require 'uri'
require 'open-uri'
require 'net/ftp'
require 'rss/maker'
require 'date'
require 'logger'
require 'yaml'

LONPOS_AREAQ_PAGE = 'http://www.lon-pos.com/en/areaq.htm'
CURRENT_DIR = File.dirname(File.expand_path(__FILE__))
#CURRENT_DIR = '/home/hoge/lonpos' make sure this directry for call by simbolic link
FTP_SERVER = 'ftp.hoge.com'
FTP_USERNAME = 'username'
FTP_PASSWORD = 'password'

def save_file(url)
  filename = File.basename(url)
  open(CURRENT_DIR+'/questions/'+filename, 'wb') do |file|
    open(url) do |data|
      file.write(data.read)
    end
  end
end

def upload(filepath, filename)
	ftp = Net::FTP.new
	ftp.connect(FTP_SERVER)
	ftp.login(FTP_USERNAME, FTP_PASSWORD)
	ftp.put(filepath, filename)
	ftp.quit
end

logger = Logger.new(File.open(CURRENT_DIR+'/lonpos.log', File::WRONLY | File::APPEND), 'monthly')
logger.level = Logger::INFO
logger.info(" *** Lonpos feed generator start *** ")
new_pics = Array.new
exist_pics = Array.new
last_pics = YAML::load_file(CURRENT_DIR+'/lonpos_yaml')

rss = RSS::Maker.make('2.0') do |maker|
	maker.channel.about = 'http://www11.atpages.jp/pokutuna/lonpos.xml'
	maker.channel.title = 'LONPOS Question Feed'
	maker.channel.description = 'Feedalized LONPOS Weekly Questions'
	maker.channel.link = LONPOS_AREAQ_PAGE
	maker.items.do_sort = true;
	
	q_top = URI.parse(LONPOS_AREAQ_PAGE).read
	q_urls = q_top.scan(/<a href="([^\."<]+\.(php|htm|html))"/).uniq!.map!{|x| URI.join(LONPOS_AREAQ_PAGE, x[0])}

	q_urls.each do |url|
		begin
			open(url) do |page|
				page.read =~ /(<p class="entxt".+<\/div>)/ms
				q_part = $1
				q_part =~ /<p class="entxt">(.*)<\/p>/
				title = $1
				q_part =~ /<.*\/(.+)\.(jpg|gif|png|JPG|GIF|PNG)".*/
				title << ' : ' + $1
				q_part.gsub!(/<p><a href="areaq.htm.+<img[^"]+src="[^"]+\/menu\.gif".*/ms, '')
				description = q_part
				pics = q_part.scan(/src="([^"]+\/puzzle\/[^"]+)"/).flatten!.map!{|x| URI.join(url.to_s,x)}
				exist_pics << pics.map{|x| x.to_s}
				if (pics.map{|x| x.to_s} - last_pics) != [] then
					new_pics << pics.flatten.map{|x| x.to_s}
					pics.each{|x| save_file(x.to_s); logger.info("find & download #{x.to_s}")}
					item = maker.items.new_item
					item.title = title
					item.link = url
					item.description = description
					item.date = Time.now
					item.guid.content = item.date.to_i.to_s+'@'+title
					item.guid.isPermaLink = false

				end
			end
		rescue OpenURI::HTTPError => e #ignore
		rescue => e
			logger.error(e.to_s + __LINE__.to_s)
		end
	end
end

if new_pics != [] then
	begin
		File.open(CURRENT_DIR+'/lonpos.xml', 'w'){ |file| file.puts(rss.to_s)}
		logger.info('generated lonpos.xml')
		File.open(CURRENT_DIR+'/lonpos_yaml', 'w').write(YAML.dump(exist_pics.flatten.map{|x| x.to_s}))
		logger.info('generated lonpos_yaml')
		upload(CURRENT_DIR+'/lonpos.xml', 'lonpos.xml')
		logger.info('uploaded lonpos.xml')
	rescue => e
		logger.error(e.to_s + __LINE__.to_s)
	end
else
	logger.info('not found new questions')
end
logger.info(' *** Lonpos feed generator closed *** ')

