def sum_of_diagonals(n)
  sum = 1
  last = 1
  (1..n/2).each do |i|
    side = i * 2
    4.times do 
      last += side 
      sum += last
    end
  end
  sum
end

r = sum_of_diagonals 1001
p r
raise "error" unless r == 669171001