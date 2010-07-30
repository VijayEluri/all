@x = 1
@y = 2

@index = 3

while @y.to_s.size < 1000
  x = @y
  @y += @x
  @index += 1
  @x = x
end

p @index
