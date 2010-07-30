NUM = 600851475143

def method2
  require 'mylib'
  p = Prime.new
  @i = p.next
  @num = NUM
  while @i < @num - 1
    if @num % @i == 0
      @num = @num / @i
      print "#{@i}, "
      p.reset
    end
    @i = p.next
  end
  print @num
end

method2