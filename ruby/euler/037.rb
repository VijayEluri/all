require 'mylib'

def truncatable_both_ways?(num)
  return false if num < 10
  s = num.to_s
  (1..s.size - 1).each do |x|
    return false unless s[0, x].to_i.is_prime?
    return false unless s[x..-1].to_i.is_prime?
  end
  true
end

p = Prime.new

@sum = 0
@count = 0
while true
  num = p.next
  if truncatable_both_ways?(num)
    p num
    @sum += num
    @count += 1
  end
  break if @count == 11
end

p @sum