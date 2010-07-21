module AnalyzeHumanNetwork
  @@meets_threshold = 60 * 5 #TODO
  @@time_threshold = 120
  
  def self.meets_threshold=(th); @@meets_threshold = th end
  def self.meets_threshold; return @@meets_threshold; end

  def self.time_threshold=(sec); @@time_threshold = sec; end
  def self.time_threshold; return @@time_threshold; end
  
  def create_inner_result(name, result)
    dest = Hash.new
    result.each { |k,v| dest[k] = { name => v}}
    return dest
  end
  
  def analyze(&filter)
    record_list = filter_records(&filter)

    results =[
      analyze_detect(record_list),
      analyze_meet(record_list),
      analyze_time(record_list) ]

    dest = Hash.new
    merge_sub = lambda{ |k,s,o| s.merge(o)}
    results.each { |h| dest.merge!(h, &merge_sub)}
    return dest
  end

  def analyze_detect(records)
    detect_count = Hash.new(0) #Hash bda => count
    
    records.each do |i|
      detect_count[i.bda] += 1
    end

    return create_inner_result(:detects, detect_count)
  end
  
  def analyze_meet(records)
    meet_count = Hash.new(0)
    last_contact = Hash.new(Time.at(0)) #Hash bda => Time

    records.each do |i|
      diff = i.date - last_contact[i.bda]
      meet_count[i.bda] += 1 if diff > @@meets_threshold
      last_contact[i.bda] = i.date
    end

    return create_inner_result(:meets, meet_count)
  end

  def analyze_time(records)
    time_sum = Hash.new(0) #bda => Time(sec)
    last_contact = Hash.new(nil) #bda => Time

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
