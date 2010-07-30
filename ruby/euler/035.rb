require 'mylib'

def rotations(num)
  yield num
  chars = num.to_s.chars.to_a
  (1..chars.size - 1).each do |x|
    c = chars.delete_at(0)
    chars << c
    yield chars.join.to_i
  end
end

@found = []

(1..100).each do |x|
  next if @found.include? x
  nums = []
  found = true
  rotations(x) do |i|
    if i.is_prime?
      nums << i
    else
      found = false
      break
    end
  end
  @found += nums if found
end

p @found
p @found.uniq.size
