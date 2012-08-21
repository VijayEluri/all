# It was proposed by Christian Goldbach that every odd composite number 
# can be written as the sum of a prime and twice a square.

# 9 = 7 + 2 * 1 ^ 2
# 15 = 7 + 2 * 2 ^ 2
# 21 = 3 + 2 * 3 ^ 2
# 25 = 7 + 2 * 3 ^ 2
# 27 = 19 + 2 * 2 ^ 2
# 33 = 31 + 2 * 1 ^ 2

# It turns out that the conjecture was false.

# What is the smallest odd composite that cannot be written as the sum of
# a prime and twice a square?

require './mylib'

@prime = Prime.new

def find_formula(num)
  @prime.reset
  while (p ||= 0) < num
    p = @prime.next
    left = num - p
    next if left < 0 or left % 2 != 0
    left = left / 2
    sqrt = Math.sqrt(left).to_i
    if sqrt * sqrt == left then
      puts "#{num} = #{p} + 2 * #{sqrt} * #{sqrt}" 
      return true
    end
  end
  false
end

i = 33
while true
  abort i.to_s unless find_formula(i)
  i += 2
end