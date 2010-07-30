coins = [1, 2, 5, 10, 20, 50, 100, 200]
ways = Array.new(11, 0)
ways[0] = 1

coins.each do |i|
  i.step(10) do |j|
    ways[j] += ways[j - i]
  end
  p ways
end