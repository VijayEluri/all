STR = <<'DATA'
75
95 64
17 47 82
18 35 87 10
20 04 82 47 65
19 01 23 75 03 34
88 02 77 73 07 63 67
99 65 04 28 06 16 70 92
41 41 26 56 83 40 80 70 33
41 48 72 33 47 32 37 16 94 29
53 71 44 65 25 43 91 52 97 51 14
70 11 33 28 77 73 17 78 39 68 17 57
91 71 52 38 17 14 91 43 58 50 27 29 48
63 66 04 68 89 53 67 30 73 16 69 87 40 31
04 62 98 27 23 09 70 98 73 93 38 53 60 04 23
DATA

class Pt
  attr_reader :x
  attr_reader :y
  attr_reader :v
  
  def initialize(x, y, v)
    @v = v
    @x = x
    @y = y
  end  
  
end

@array = STR.split("\n").collect { |line| line.split(" ").collect { |n| n.to_i } }
(0..@array.size - 1).each do |x| 
  (0..@array[x].size - 1).each do |y|
    @array[x][y] = Pt.new(x, y, @array[x][y])
  end
end

@cache = {}

#all distances from x, y to 0, 0
def distances(x, y)
  if x == 0
    return [@array[0][0].v]
  end
  
  return @cache[[x, y]] unless @cache[[x, y]].nil?
  
  d = []
  
  if y < x
    d1 = distances(x - 1, y)
    d += d1.collect { |d| d + @array[x][y].v }
  end
  if y > 0
    d2 = distances(x - 1, y - 1) 
    d += d2.collect { |d| d + @array[x][y].v }
  end
  @cache[[x, y]] = d
  d
end

d = []
(0..@array[14].size - 1).each do |i|
  d += distances(14, i)
end
p d.max