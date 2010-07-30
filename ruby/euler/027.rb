require 'mylib'

def calc_primes(a, b)
  n = 0
  while true
   num = n * n + a * n + b
   n += 1
   break if num < 0
   break unless num.is_prime? 
  end
  @maxn = n and @max = (a * b) if n > @maxn
end

@maxn, @max = 0, 0

(2..999).each do |b|
  next unless b.is_prime?
  (-999..999).each do |a|
    next if 1 + a + b < 2
    calc_primes(a, b)
  end
end

p @max
raise "Error" unless @max == -59231