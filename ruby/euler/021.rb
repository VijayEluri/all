require 'mylib'

@pp = Prime.new  

@sum = 0

(4..9999).each do |i|
  sum = @pp.sum_of_divisors(i)
  next if sum == 1
  next if sum == i
  next if sum > 9999
  sum2 = @pp.sum_of_divisors(sum)
  if i == sum2
    print "#{i}, #{sum}\n"
    @sum += i + sum
  end
end

@sum /= 2

p @sum

raise "Error" unless @sum == 31626