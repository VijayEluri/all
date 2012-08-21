@triangles = [1]

def generate_triangles
  (2..1000).each do |i|
    @triangles << @triangles.last + i
  end
end

generate_triangles

puts @triangles

line = open('words.txt') { |f| f.read }
words = line.split(',')
count = 0
words.each do |word|
  sum = 0
  word.each_byte do |i|
    sum += i - 'A'.ord + 1 unless i == 34
  end
  count += 1 if @triangles.include?(sum)
end

puts count