require 'mylib'

pp = Prime.new
sum = 0
v = pp.next
while v < 2000000
  sum += v
  v = pp.next
end

p sum

raise "Error" unless sum == 142913828922