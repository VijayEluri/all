class DDate
  attr_accessor :year
  attr_accessor :month
  attr_accessor :day
  
  def initialize(year, month, day)
    @year = year
    @month = month
    @day = day
  end

  @@START_DATE = DDate.new(1900,1,1)
  @@days_per_month = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
  def self.start_date
    @@START_DATE
  end
  
  def <(value)
    return true if @year < value.year
    return false if @year > value.year
    return true if @month < value.month
    return false if @month > value.month
    return true if @day < value.day
    false
  end
  
  def >(value)
    return true if @year > value.year
    return false if @year < value.year
    return true if @month > value.month
    return false if @month < value.month
    return true if @day > value.day
    false
  end
  
  def ==(value)
    @year == value.year and @month == value.month and @day == value.day ? true : false
  end
  
  def <=(value)
    self < value or self == value
  end
  
  def next_month
    year = @year
    month = @month + 1
    if @month == 12
      year += 1
      month = 1
    end
    day = @day
    DDate.new(year, month, day)
  end
  
  def is_leap?(y = @year)
    return true if y % 400 == 0
    (y % 4 == 0 and y % 100 != 0) ? true : false
  end
  
  def is_sunday?
    start = DDate.start_date
    day = 0
    (start.year..@year - 1).each do |y|
      is_leap?(y) ? day += 366 : day += 365
    end
    (1..@month - 1).each do |m|
      day += @@days_per_month[m]
      day += 1 if is_leap? and m == 2
    end
    day += @day - 1
    # print "#{@year}-#{@month}-#{@day}: #{day}, "
    day = day % 7 + 1
    # print "#{day}\n"
    day == 7
  end
  
end

date = DDate.new(1901, 1, 1)
end_date = DDate.new(2000, 12, 1)
@count = 0
while(date <= end_date)
  @count +=1 if date.is_sunday?
  date = date.next_month
end

p @count

raise "Error" unless @count == 171
