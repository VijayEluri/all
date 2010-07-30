class Integer
  def pandigital?
    str = self.to_s
    str.size == str.chars.to_a.uniq.size
  end
end

def pandigital?(num1, num2, num3)
  str1 = num1.to_s
  str2 = num2.to_s
  str3 = num3.to_s
  size = str1.size + str2.size + str3.size
  return 0 if size != 9
  array = str1.chars.to_a + str2.chars.to_a + str3.chars.to_a
  return 0 if array.include?('0')
  size == array.uniq.size ? num1 : 0
end

def test(num)
  sqrt = Math.sqrt(num).ceil
  (2..sqrt).each do |i|
    next unless num % i == 0
    j = num / i
    r = pandigital?(num, i, j)
    return r if r > 0
  end
  0
end

nums = []
(1000..9999).each do |i|
  next unless i.pandigital?
  nums << test(i)
end

sum = nums.uniq.inject(0) { |r, n| r + n }

p sum
