@cache = {}

def steps(x, y)
  y, x = x, y if x > y
  
  return @cache[[x, y]] unless @cache[[x, y]].nil?
  
  if x == 1
     r = x + y 
  elsif x == y
    r = 2 * steps(x - 1, y)
  else
    r = steps(x - 1, y) + steps(x, y - 1)
  end

  @cache[[x, y]] = r
  return r
  
end

r = steps(20, 20)
p r
raise "Error" unless r == 137846528820