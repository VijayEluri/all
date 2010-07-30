@items = []

(2..100).each do |a|
  (2..100).each do |b|
    @items << a ** b
  end
end

p @items.uniq.size
