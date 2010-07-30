@x = 1
@y = 2

def fibonacci
  while @y < 4000000
    yield @y
    x = @y
    @y += @x
    @x = x
  end
end

@sum = 0

fibonacci do |i|
  print "#{i}, "
  @sum += i if i % 2 == 0
end

print "\n"
print @sum