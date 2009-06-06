SHUT_UP = /黙れ|静かに(?:しろ){0,1}/

def shut_up(reply)
	$twit.post("@#{reply.user.screen_name} はい…")
end

def analyze_command(reply)
	if reply.user.screen_name = $conf['su'] then
		text = reply.text.without_reply_to

		#/follow
		if text.include?('/follow') then
			
			text['/follow'] = ''
			targets = text.scan_id
			
			followed_user = Array.new
			targets.each do |t|
				begin
					$twit.create_friendship(t)
					followed_user.push(t)
				rescue => e
					$twit.post("@#{reply.user.screen_name} #{t}フォローできない")
				end
			end
			$twit.post("@#{reply.user.screen_name} #{followed_user.join(' ')}フォローした") unless followed_user.empty?

			#remove
		elsif text.include?('remove') then
			
			text['/remove'] = ''
			targets = text.scan_id

			removed_user = Array.new
			targets.each do |t|
				begin
					$twit.destroy_friendship(t)
					removed_user.push(t)
				rescue => e
					$twit.post("@#{reply.user.screen_name} #{t}removeできない")
				end
			end
			$twit.post("@#{reply.user.screen_name} #{removed_user.join(' ')}removeした") unless removed_user.empty?
			
		#not found	
		else
			$twit.post("@#{reply.user.screen_name} そんなコマンド無いよ")
		end
	else #not su
		$twit.post("@#{reply.user.screen_name} 出直してこい")
	end
	
end
