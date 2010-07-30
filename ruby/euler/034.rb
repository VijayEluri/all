require 'mylib'

def test_number(num)
  sum = 0
  num.to_s.chars.each do |c|
    sum += c.to_i.factorial
  end
  num == sum
end

C9 = 9.factorial
digits = 2

while true
  stop = 10 ** digits - 1
  start = 10 ** (digits - 1)
  break if start > C9 * 2
  (start..stop).each do |i|
    p i if test_number(i)
  end
  digits += 1
end
