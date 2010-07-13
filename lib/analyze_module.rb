module AnalyzeHumanNetwork
  @@meets_threshold = 60 * 5 #TODO
  @@time_threshold = 60
  
  def self.meets_threshold=(th); @@meets_threshold = th end
  def self.meets_threshold; return @@meets_threshold; end

  def self.time_threshold=(sec); @@time_threshold = sec; end
  def self.time_threshold; return @@time_threshold; end
  

  def create_record_list(recs=nil, &filter)
    if recs == nil then
      sort_record
      recs = @records
    end
    
    if block_given? then
      recs = recs.select{ |i| filter.call(i) == true}
    end

    return recs
  end

  def create_inner_result(name, result)
    dest = Hash.new
    result.each { |k,v| dest[k] = { name => v}}
    return dest
  end
  
  def analyze(&filter)
    #recrods = create_record_list(&filter) # !!!bug!!!!
    merge_sub = lambda{ |k,s,o| s.merge(o)}

    results =[
      analyze_detect(records,&filter), # each take filter...
      analyze_meet(records,&filter),
      analyze_time(records,&filter) ]

    dest = Hash.new
    results.each { |h| dest.merge!(h, &merge_sub)}
    analyzed = true
    return dest
  end

  def analyze_detect(records=nil, &filter)
    records = create_record_list(records, &filter)
    detect_count = Hash.new(0) #Hash bda => count
    
    records.each do |i|
      detect_count[i.bda] += 1
    end

    return create_inner_result(:detects, detect_count)
  end
  
  def analyze_meet(records=nil, &filter)
    records = create_record_list(records, &filter)
    meet_count = Hash.new(0)
    last_contact = Hash.new(Time.at(0)) #Hash bda => Time

    records.each do |i|
      diff = i.date - last_contact[i.bda]
      meet_count[i.bda] += 1 if diff > @@meets_threshold
      last_contact[i.bda] = i.date
    end

    return create_inner_result(:meets, meet_count)
  end

  def analyze_time(records=nil, &filter)
    records = create_record_list(records, &filter)
    time_sum = Hash.new(0) #Hash bda => Time(sec)
    last_contact = Hash.new

    records.each do |i|
      unless last_contact[i.bda] == nil
        diff = i.date - last_contact[i.bda]
        time_sum[i.bda] += diff if diff < @@time_threshold
      end
      last_contact[i.bda] = i.date
    end

    return create_inner_result(:time, time_sum)
  end
end
