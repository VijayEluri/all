require 'mylib'

def reduce(a, b)
  bfh = b.prime_factors_in_hash
  c = []
  a.prime_factors_in_hash.each do |k, v|
    c << [k] * [v, bfh[k]].min unless bfh[k].nil?
  end
  mc = c.flatten.inject(1) { |x, y| x * y }
  mc == 0 ? [a, b] : [a / mc, b / mc]
end

def reduce_by_digit(a, b)
  sa = a.to_s.chars.to_a
  sb = b.to_s.chars.to_a
  c = sa & sb
  sa -= c
  sb -= c
  [sa.join.to_i, sb.join.to_i]
end

@a = 1
@b = 1

(11..99).each do |x|
  next if x % 10 == 0
  (x..99).each do |y|
    next if y % 10 == 0
    a2, b2 = reduce_by_digit(x, y)
    next if a2 == x # no digits in common at all
    a1, b1 = reduce(x, y)
    a2, b2 = reduce(a2, b2)
    if a1 == a2 and b1 == b2
      print "#{x}, #{y}\n"
      @a *= a2
      @b *= b2
    end
  end
end

print "#{@a}, #{@b}\n"
p reduce(@a, @b)
