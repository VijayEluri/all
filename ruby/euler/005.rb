require 'mylib'

factors = {}

2.step(20, 1) do |i|
  i.prime_factors_in_hash.each do |k, v|
    factors[k] = 0 if factors[k].nil?
    factors[k] = v if v > factors[k]
  end
end

sum = 1
p factors
factors.each { |k, v| sum *= k ** v }
p sum