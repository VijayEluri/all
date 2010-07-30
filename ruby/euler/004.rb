require 'mylib'

@r = 0

100.step(999, 1) do |i|
  100.step(999, 1) do |j|
    v = i * j
    @r = v if v > @r and v.palindromic?
  end
end

p @r