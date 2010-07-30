A = 'A'[0] - 1

line = open('names.txt') { |f| f.read }
index = 0
@sum = 0
line.split(",").sort.each do |str|
  index += 1
  str.gsub!("\"", '')
  r = str.chars.inject(0) { |t, c| t + c[0] - A }
  @sum += r * index
end

p @sum

raise Error unless @sum == 871198282