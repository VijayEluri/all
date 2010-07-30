require 'mylib'

@sum = 0
1.step(1000000, 2) do |x|
  next unless x.palindromic?
  next unless x.binary_s.palindromic?
  @sum += x
  p x
end

p @sum