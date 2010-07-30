def pythagorean?(x, y, z)
  z * z == x * x + y * y ? true : false
end

(1..999).step(1) do |x|
  (x..999).step(1) do |y|
    z = 1000 - x - y
    print "#{x}, #{y}, #{z} : #{x * y * z}\n" if pythagorean?(x, y, z)
  end
end
