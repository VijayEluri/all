# require 'rubygems'
# require 'ruby-prof'

require 'pprime'

# RubyProf.start

@pp = PPrime.new

def abundant_numbers(upper)  
  numbers = []
  (2..upper).each do |i|
    numbers << i if (@pp.sum_of_divisors(i) > i)
  end
  numbers
end

@nums = abundant_numbers 28123

def array_of_sum_abundant_numbers
  @array = []
  (0..@nums.size - 1).each do |i|
    break if @nums[i] > 28123 / 2
    (i..@nums.size - 1).each do |j|
      sum = @nums[i] + @nums[j]
      @array << sum if sum <= 28123
    end
  end
  @array.uniq
end

array = array_of_sum_abundant_numbers

sum2 = array.inject(0) { |r, n| r + n }

sum1 = (1 + 28123) * 28123 / 2

p sum1, sum2

r = sum1 - sum2

p r

raise "error" unless r == 4179871

# result = RubyProf.stop

# Print a flat profile to text
# printer = RubyProf::GraphPrinter.new(result)
# printer.print(STDOUT, 0)