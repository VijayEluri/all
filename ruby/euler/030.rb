N = 9 ** 5

i = 2

def sum_of_fifth_powers(num)
  sum = 0
  num.to_s.chars.each do |c|
    sum += c.to_i ** 5
  end
  sum
end

sum = 0
while true do
  i += 1
  break if i > i.to_s.size * N
  sum += i if i == sum_of_fifth_powers(i)
end

p sum
