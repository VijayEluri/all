# The number, 1406357289, is a 0 to 9 pandigital number because it is made up of each of the 
# digits 0 to 9 in some order, but it also has a rather interesting sub-string divisibility property.

# Let d1 be the 1st digit, d2 be the 2nd digit, and so on. In this way, we note the following:

# d2d3d4=406 is divisible by 2
# d3d4d5=063 is divisible by 3
# d4d5d6=635 is divisible by 5
# d5d6d7=357 is divisible by 7
# d6d7d8=572 is divisible by 11
# d7d8d9=728 is divisible by 13
# d8d9d10=289 is divisible by 17
# Find the sum of all 0 to 9 pandigital numbers with this property.

class Integer
  def pandigital?
    str = self.to_s
    str.size == str.chars.to_a.uniq.size
  end
end

dividers = [13, 11, 7, 5, 3, 2, 1]
digits = ['1','2','3','4','5','6','7','8','9','0']

res  = []
res1 = []

(102..987).step(17).each do |a|
  res << a.to_s if a.pandigital?
end

dividers.each do |p|
  res.each do |r|
    digits.each do |d|
      next if r.include? d
      newstr = d + r[0..1]
      res1 << d + r if newstr.to_i % p == 0
    end    
  end
  res = res1
  res1 = []
end

sum = 0

res.each do |r|
  puts r
  sum += r.to_i
end

puts sum