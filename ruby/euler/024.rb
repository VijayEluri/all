require 'mylib'

@target = 999999
@index = []
@data = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']

(1..@data.size - 1).reverse_each do |i|
  count = i.factorial
  @index << @target / count
  @target %= count 
end

@r = ""
@index.each do |i|
  char = @data[i]
  @r << char
  @data -= [char]
end
@r << @data[0]

p @r

raise "Error" unless @r == "2783915460"