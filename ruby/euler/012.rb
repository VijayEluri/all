require 'mylib'

def factors_count(num)
  c = 1
  num.prime_factors_in_hash.each do |k, v|
    c *= (v + 1)
  end
  return c
end

triangle = 0
i = 1
while true
  triangle += i
  i += 1
  c = factors_count(triangle)
  break if c > 500
end

p triangle

raise "Error" unless triangle == 76576500